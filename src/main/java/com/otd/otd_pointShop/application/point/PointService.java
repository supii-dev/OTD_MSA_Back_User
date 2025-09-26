package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.*;
import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import com.otd.otd_pointShop.repository.PointRepository;
import com.otd.otd_pointShop.repository.PointImageRepository;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.otd_user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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
public class PointService {
    @Value("${upload.point-pic}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointImageRepository pointImageRepository;
    private final PointMapper pointMapper;

    private void validateImageExtension(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!List.of("jpg", "jpeg", "png", "gif", "bmp").contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다.");
        }
    }

    // image save logic
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
                throw new RuntimeException("이미지 저장 실패", e);
            }

            PointImage pointImage = new PointImage();
            pointImage.setImageUrl(filename);
            pointImage.setPoint(point);
            imagesList.add(pointImage);
        }
        if (!imagesList.isEmpty()) {
            point.setPointItemImage(imagesList.get(0).getImageUrl());
        }
        return imagesList;
    }

    public List<PointListRes> getPointListByUser(Long userId, Pageable pageable) {
        Page<Point> page = pointRepository.findByUser_UserId(userId, pageable);
        return page.getContent().stream()
                .map(point -> PointListRes.builder()
                        .pointId(point.getPointId())
                        .pointItemName(point.getPointItemName())
                        .pointitemImages(point.getPointItemImages())
                        .pointScore(point.getPointScore())
                        .createdAt(point.getCreatedAt())
                        .build())
                .toList();
    }


    public List<PointGetRes> pointGetResList(Long userId, Pageable pageable) {
        return pointRepository.findByUser_UserId(userId, pageable)
                .map(point -> {
                    List<String> imageUrls = pointImageRepository.findByPoint_PointId(point.getPointId())
                    .stream()
                    .map(PointImage::getImageUrl)
                    .toList();
            return PointGetRes.builder()
                    .pointId(point.getPointId())
                    .pointItemName(point.getPointItemName())
                    .pointItemContent(point.getPointItemContent())
                    .pointScore(point.getPointScore().intValue())
                    .createdAt(point.getCreatedAt())
                    .images(imageUrls)
                    .build();
        }).toList();
    }

    public Set<String> getPointKeywordByUser(Long userId, String keyword, Pageable pageable) {

        return pointRepository.findByUser_UserIdAndPointItemContentContaining(userId, keyword, pageable)
                .stream()
                .flatMap(p -> extractKeywords(p.getPointItemContent()).stream())
                .collect(Collectors.toSet());
    }

    private Set<String> extractKeywords(String pointItemContent) {
        if (pointItemContent == null) return Set.of();
        return Arrays.stream(pointItemContent.split("\\s+"))
                .map(word -> word.replaceAll("[^\\p{IsAlphabetic}\\d]", ""))
                .filter(word -> word.length() > 1)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void createPointItem(PointPostReq dto, MultipartFile[] images, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다!"));

        Point point = new Point();
        point.setUser(user);
        point.setPointScore(dto.getPointScore().intValue());
        point.setPointItemName(dto.getPointItemName());
        point.setPointItemContent(dto.getPointItemContent());

        List<PointImage> imagesList = storeImages(images, point);
        point.setImages(imagesList);
        pointRepository.save(point);
    }

    @Transactional
    public void updatePointItem(PointPutReq dto, MultipartFile[] images, Long userId) {
        User user  = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Point point = pointRepository.findById(dto.getPointId())
                .orElseThrow(() -> new RuntimeException("포인트 항목을 찾을 수 없습니다."));
        if (!point.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("수정할 권한이 없습니다.");
        }

        point.setPointScore(dto.getPointScore().intValue());
        point.setPointItemName(dto.getPointItemName());
        point.setPointItemContent(dto.getPointItemContent());

        List<PointImage> oldImages = pointImageRepository.findByPoint_PointId(dto.getPointId(), dto.getItem);
        pointImageRepository.deleteAll(oldImages);

        List<PointImage> newImageList = storeImages(images, point);
        point.setImages(newImageList);
        pointRepository.save(point);
    }

    @Transactional
    public void deletePointItem(Long pointId, Long userId) {
        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new RuntimeException("포인트 항목을 찾을 수 없습니다."));
        if (!point.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("삭제할 권한이 없습니다.");
        }
        pointImageRepository.deleteAllByPoint(point);
        pointRepository.delete(point);
    }

    public List<Point> getPointByUser(Long userId) {
        return pointRepository.findByUser_UserId(userId);
    }
    public List<PointImage> getImagesForPoint(Long pointId) {
        return pointImageRepository.findByPoint_PointId(pointId);
    }
}
