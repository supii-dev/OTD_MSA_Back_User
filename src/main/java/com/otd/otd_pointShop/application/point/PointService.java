package com.otd.otd_pointShop.application.point;

import com.otd.otd_pointShop.application.point.model.PointPostReq;
import com.otd.otd_pointShop.entity.Point;
import com.otd.otd_pointShop.entity.PointImage;
import com.otd.otd_pointShop.repository.PointRepository;
import com.otd.otd_pointShop.repository.PointImageRepository;
import com.otd.otd_user.application.user.UserRepository;
import com.otd.otd_user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PointService {
    @Value("${upload.point-pic}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final PointRepository pointRepository;
    private final PointImageRepository pointImageRepository;

    public void createPointItem(PointPostReq dto, MultipartFile[] images, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Point point = new Point();
        point.setUser(user);
        point.setPointScore(dto.getPointScore());
        point.setPointItemName(dto.getPointItemName());
        point.setPointItemContent(dto.getPointItemContent());

        // image save logic
        List<PointImage> imagesList = new ArrayList<>();
        if (images != null && images.length > 0) {
            for (MultipartFile file : images) {
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
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
        }

        point.setImages(imagesList);
        pointRepository.save(point);
    }

    public List<Point> getPointByUser(Long userId) {
        return pointRepository.findByUser_UserId(userId);
    }
    public List<PointImage> getImagesForPoint(Long pointId) {
        return pointImageRepository.findByPoint_PointId(pointId);
    }
}
