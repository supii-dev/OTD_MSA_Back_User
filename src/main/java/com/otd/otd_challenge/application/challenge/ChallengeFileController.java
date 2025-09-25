package com.otd.otd_challenge.application.challenge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
//@RequestMapping("/api/otd/file")
@RequiredArgsConstructor
public class ChallengeFileController {

//    private final ChallengeFileService fileService;
//
//    @GetMapping("/{fileName}")
//    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
//        // DB에서 이미지 조회
//        byte[] imageBytes = fileService.loadImage(fileName);
//
//        if (imageBytes == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // Content-Type은 실제 이미지 확장자에 맞춰 설정 (PNG, JPEG 등)
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_PNG);
//
//        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//    }
}
