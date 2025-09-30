package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.*;
import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import com.otd.otd_pointShop.repository.PointRepository;
import com.otd.otd_pointShop.repository.PointImageRepository;
import com.otd.otd_user.application.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class PointshopService {

    @Value("${upload.pointshop-pic}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointImageRepository pointImageRepository;

    private static final String POINT_NOT_FOUND = "포인트를 찾을 수 없습니다";
    private static final String UNAUTHORIZED_ACCESS = "접근 권한이 없습니다";

    private void validateImageExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!List.of("jpg", "jpeg", "png", "gif", "bmp").contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }
    }

    private List<PointImage> storeImages(MultipartFile[] images, Point point) {
        List<PointImage> imagesList = new ArrayList<>();
        if (images == null || images.length == 0) return imagesList;

        for (MultipartFile file : images) {
            validateImageExtension(file);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path savePath = Paths.get(uploadDir, filename);

            try {
                Files.createDirectories(savePath.getParent());
                file.transferTo(savePath.toFile());
            } catch (IOException e) {
                throw new RuntimeException("이미지 저장에 실패하였습니다", e);
            }

            PointImage pointImage = new PointImage();
            pointImage.setImageUrl(filename);
            pointImage.setPoint(point);
            imagesList.add(pointImage);
        }
        return imagesList;
    }

    public List<PointListRes> getPointListByUser(Long userId, Pageable pageable) {
        Page<Point> page = pointRepository.findByUser_UserId(userId, pageable);
        return page.getContent().stream()
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
                        .build())
                .toList();
    }

    public Page<PointGetRes> pointGetResList(Long userId, Pageable pageable) {
        // 포인트 목록 페이징 조회
        Page<Point> pointPage = pointRepository.findByUser_UserId(userId, pageable);

        // 각 포인트 이미지 페이징 (최대 10장)
        List<PointGetRes> pointGetResList = pointPage.getContent().stream().map(point -> {
               PageRequest imagePageRequest = PageRequest.of(0,10);
               Page<PointImage> imagePage = pointImageRepository.findByPoint_PointId(point.getPointId(), imagePageRequest);

                    List<String> imageUrls = imagePage.getContent().stream()
                            .map(PointImage::getImageUrl)
                            .toList();

                return PointGetRes.builder()
                        .pointId(point.getPointId())
                        .pointItemName(point.getPointItemName())
                        .pointItemContent(point.getPointItemContent())
                        .pointScore(point.getPointScore())
                        .createdAt(point.getCreatedAt())
                        .images(imageUrls)
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

    private Set<String> extractKeywords(String content) {
        if (content == null) return Set.of();
        return Arrays.stream(content.split("\\s+"))
                .map(word -> word.replaceAll("[^\\p{IsAlphabetic}\\d]", ""))
                .filter(word -> word.length() > 1)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void createPointItem(PointPostReq dto, MultipartFile[] images, Long userId) {
        Point point = new Point();
        point.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자 없음")));
        point.setPointScore(dto.getPointScore());
        point.setPointItemName(dto.getPointItemName());
        point.setPointItemContent(dto.getPointItemContent());

        List<PointImage> imagesList = storeImages(images, point);
        point.setPointItemImage(imagesList);

        pointRepository.save(point);
    }

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
}