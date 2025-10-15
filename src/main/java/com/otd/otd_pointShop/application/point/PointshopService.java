package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.*;
import com.otd.otd_pointShop.application.purchase.model.PurchaseHistoryRes;
import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import com.otd.otd_pointShop.entity.PurchaseHistory;
import com.otd.otd_pointShop.repository.PointRepository;
import com.otd.otd_pointShop.repository.PointImageRepository;
import com.otd.otd_pointShop.repository.PurchaseHistoryRepository;
import com.otd.otd_pointShop.repository.RechargeHistoryRepository;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.otd_user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PointshopService {

    @Value("${constants.file.pointshop-pic}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointImageRepository pointImageRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final RechargeHistoryRepository rechargeHistoryRepository;

    // 유저별 포인트 리스트 조회
    public List<PointListRes> getPointListByUser(Long userId, Pageable pageable) {
        Page<Point> page = pointRepository.findByUser_UserId(userId, pageable);

        return page.getContent().stream()
                .peek(Point::syncUserPoint) // User의 point값 동기화
                .map(point -> PointListRes.builder()
                        .pointId(point.getPointId())
                        .pointItemName(point.getPointItemName())
                        .pointItemContent(point.getPointItemContent())
                        .pointItemImage(
                                point.getPointItemImage().stream()
                                        .map(PointImage::getImageUrl)
                                        .collect(Collectors.toList())
                        )
                        .pointScore(point.getPointScore())
                        .createdAt(point.getCreatedAt())
                        .userCurrentPoint(point.getUserCurrentPoint())
                        .build())
                .toList();
    }

    public Page<PointGetRes> pointGetResList(Long userId, Pageable pageable) {
        // 포인트 목록 페이징 조회
        Page<Point> pointPage = pointRepository.findByUser_UserId(userId, pageable);

        List<PointGetRes> pointGetResList = pointPage.getContent().stream().map(point -> {
            // 유저 포인트 동기화
            point.syncUserPoint();

            // 이미지 페이징 (최대 10장)
            PageRequest imagePageRequest = PageRequest.of(0,10);
            Page<PointImage> imagePage = pointImageRepository.findByPoint_PointId(point.getPointId(), imagePageRequest);

            List<PointImageRes> imageDtoList = imagePage.getContent().stream()
                    .map(this::toImageDto)
                    .toList();

            return PointGetRes.builder()
                    .pointId(point.getPointId())
                    .pointItemName(point.getPointItemName())
                    .pointItemContent(point.getPointItemContent())
                    .pointScore(point.getPointScore())
                    .createdAt(point.getCreatedAt())
                    .images(imageDtoList)
                    .userCurrentPoint(point.getUserCurrentPoint())
                    .build();
        }).toList();

        // 최종 page 포장 후 반환
        return new PageImpl<>(pointGetResList, pageable, pointPage.getTotalElements());
    }

    public Set<String> getPointKeywordByUser(Long userId, String keyword, Pageable pageable) {
        return pointRepository.findByUser_UserIdAndPointItemContentContaining(userId, keyword, pageable)
                .stream()
                .flatMap(p -> extractKeywords(p.getPointItemContent()).stream())
                .collect(Collectors.toSet());
    }

    // 포인트 아이템 등록
    @Transactional
    public void createPointItem(PointPostReq dto, MultipartFile[] images, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보가 존재하지 않습니다."));

        Point point = new Point();
        point.setUser(user);
        point.setPointScore(dto.getPointScore());
        point.setPointItemName(dto.getPointItemName());
        point.setPointItemContent(dto.getPointItemContent());
        pointRepository.save(point);

        // User의 현재 포인트 누적 갱신
        int newBalance = user.getPoint() + dto.getPointScore();
        user.setPoint(newBalance);
        userRepository.save(user);

        // Point entity 동기화
        point.syncUserPoint();

        // 이미지 저장
        if (images != null && images.length > 0) {
            List<PointImage> imagesList = storeImages(images, point);
            point.setPointItemImage(imagesList);
        }
        log.info("포인트 적립 완료: userId={}, 추가점수={}, 총점={}", userId, dto.getPointScore(), newBalance);
    }

    // 포인트 사용, 차감
    @Transactional
    public void usePoint(Long userId, int usedScore) {
        User user = Optional.ofNullable(userRepository.findByUserId(userId))
                .orElseThrow(() -> new RuntimeException("사용자 정보가 존재하지 않습니다."));

        if (user.getPoint() < usedScore) {
            throw new RuntimeException("보유 포인트가 부족합니다.");
        }

        user.setPoint(user.getPoint() - usedScore);
        userRepository.save(user);

        log.info("포인트 차감 완료: userId={}, 차감={}, 잔액={}", userId, usedScore, user.getPoint());
    }

    // 포인트 수정
    @Transactional
    public void updatePointItem(PointPutReq dto, MultipartFile[] images, Long userId) {
        Point point = pointRepository.findById(dto.getPointId())
                .orElseThrow(() -> new EntityNotFoundException("포인트를 찾을 수 없습니다."));

        if (!point.getUser().getUserId().equals(userId)) {
            throw new EntityNotFoundException("조회할 권한이 없습니다.");
        }

        point.setPointItemName(dto.getPointItemName());
        point.setPointItemContent(dto.getPointItemContent());

        List<PointImage> oldImages = pointImageRepository.findByPoint(point);
        pointImageRepository.deleteAll(oldImages);

        List<PointImage> newImages = storeImages(images, point);
        point.setPointItemImage(newImages);
        pointRepository.save(point);
    }

    // 포인트 삭제
    @Transactional
    public void deletePointItem(Long pointId, Long userId) {
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new EntityNotFoundException("포인트를 찾을 수 없습니다"));

        if (!point.getUser().getUserId().equals(userId)) {
            throw new EntityNotFoundException("삭제할 권한이 없습니다");
        }
        pointImageRepository.deleteAllByPoint(point);
        pointRepository.delete(point);
    }

    private PointImageRes toImageDto(PointImage pointImage) {
        return PointImageRes.builder()
                .imageId(pointImage.getImageId())
                .imageUrl(pointImage.getImageUrl())
                .imageType(pointImage.getImageType())
                .altText(pointImage.getAltText())
                .build();
    }

    // 이미지 저장
    private List<PointImage> storeImages(MultipartFile[] images, Point point) {
        List<PointImage> imagesList = new ArrayList<>();
        if (images == null || images.length == 0) return imagesList;

        Path directoryPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 경로 생성 실패", e);
        }

        for (MultipartFile file : images) {
            validateImageExtension(file);

            String originalName = FilenameUtils.getBaseName(file.getOriginalFilename());
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID().toString() + "." + extension;

            Path savePath = directoryPath.resolve(fileName);
            try {
                file.transferTo(savePath.toFile());
            } catch (IOException e) {
                log.error("이미지 저장 실패: {}", originalName, e);
                throw new RuntimeException("이미지 저장에 실패하였습니다: " + originalName, e);
            }

            PointImage pointImage = new PointImage();
            pointImage.setImageUrl(fileName);
            pointImage.setImageType(file.getContentType());
            pointImage.setAltText(originalName);
            pointImage.setPoint(point);

            imagesList.add(pointImage);
        }
        return imagesList;
    }

    private void validateImageExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!List.of("jpg", "jpeg", "png", "gif", "bmp").contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // 키워드 조회
    private Set<String> extractKeywords(String content) {
        if (content == null || content.isBlank()) return Set.of();
        return Arrays.stream(content.split("\\s+"))
                .map(word -> word.replaceAll("[^\\p{IsAlphabetic}\\d]", ""))
                .filter(word -> word.length() > 1)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    // 유저 포인트 잔액 조회
    public int getUserPointBalance(Long userId) {
        int balance = userRepository.findPointByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저 포인트 정보를 찾을 수 없습니다."));
        log.info("[현재 포인트 조회] userId={}, balance={}", userId, balance);
        return balance;
    }

    // 유저 구매 이력 조회
    public List<PurchaseHistoryRes> getUserPurchaseHistory(Long userId) {
        List<PurchaseHistory> list = purchaseHistoryRepository.findByUser_UserId(userId);

        return list.stream()
                .peek(purchaseHistory -> purchaseHistory.getUser().getPoints().forEach(Point::syncUserPoint))
                .map(p -> PurchaseHistoryRes.builder()
                        .purchaseId(p.getPurchaseId())
                        .pointId(p.getPoint().getPointId())
                        .pointItemName(p.getPoint().getPointItemName())
                        .pointScore(p.getPoint().getPointScore())
                        .purchaseAt(p.getPurchaseAt())
                        .userCurrentPoint(p.getUser().getPoint())
                        .build())
                .toList();
    }

    @Transactional
    public void updateUserPointRef(Long userId, Long pointId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new RuntimeException("포인트를 찾을 수 없습니다."));

        user.addPoint(point);
        userRepository.save(user);
    }
}