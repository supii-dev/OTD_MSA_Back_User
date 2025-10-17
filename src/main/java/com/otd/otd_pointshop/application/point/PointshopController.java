package com.otd.otd_pointshop.application.point;

import com.otd.configuration.model.UserPrincipal;
import com.otd.otd_pointshop.application.point.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/OTD/pointshop")
public class PointshopController {

    private final PointshopService pointshopService;

    // [POST] 포인트 아이템 등록 (관리자 전용)
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPointItem(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestPart("data") PointPostReq req,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        validateAdmin(user);
        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        pointshopService.createPointItem(req, images);
        log.info("[관리자 등록 완료] admin={}, itemName={}", user.getUsername(), req.getPointItemName());
        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 아이템 등록 완료"));
    }

    // [PUT] 포인트 아이템 수정 (관리자 전용)
    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editPointItem(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestPart("data") PointPutReq req,
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        validateAdmin(user);
        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        pointshopService.updatePointItem(req, images);
        log.info("[관리자 수정 완료] admin={}, pointId={}", user.getUsername(), req.getPointId());
        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 아이템 수정 완료"));
    }

    // [DELETE] 포인트 아이템 삭제 (관리자 전용)
    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePointItem(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam Long pointId
    ) {
        validateAdmin(user);
        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        pointshopService.deletePointItem(pointId);
        log.info("[관리자 삭제 완료] admin={}, pointId={}", user.getUsername(), pointId);
        return ResponseEntity.ok(new PointApiResponse<>(true, "포인트 아이템 삭제 완료"));
    }

    // [GET] 전체 포인트 아이템 목록 조회 (모든 유저 가능)
    @GetMapping("/list")
    public ResponseEntity<?> getAllPointItems(
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var page = pointshopService.getAllPointItems(pageable);
        log.info("[전체 아이템 목록 조회] total={}", page.getTotalElements());
        return ResponseEntity.ok(new PointApiResponse<>(true, "전체 아이템 목록 조회 성공", page));
    }

    // [GET] 키워드 기반 검색 (모든 유저 가능)
    @GetMapping("/keyword")
    public ResponseEntity<?> searchKeyword(
            @RequestParam(defaultValue = "") String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Set<String> result = pointshopService.searchPointKeyword(keyword, pageable);
        log.info("[키워드 검색] keyword='{}', resultSize={}", keyword, result.size());
        return ResponseEntity.ok(new PointApiResponse<>(true, "키워드 검색 성공", result));
    }

    // 관리자 권한 검증
    private void validateAdmin(UserPrincipal user) {
        if (user == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        if (!user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
    }
}