package com.otd.otd_pointshop.application.point;

import com.otd.otd_pointshop.entity.PointCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/OTD/pointshop/category")
public class PointCategoryController {

    private final PointCategoryService pointCategoryService;

    // [POST] 카테고리 등록
    @PostMapping("/add")
    public ResponseEntity<PointCategory> addCategory(@RequestParam String categoryName) {
        PointCategory category = pointCategoryService.createCategory(categoryName);
        return ResponseEntity.ok(category);
    }

    // [GET] 전체 카테고리 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<PointCategory>> getAllCategories() {
        return ResponseEntity.ok(pointCategoryService.getAllCategories());
    }

    // [GET] 특정 카테고리 조회
    @GetMapping("/{id}")
    public ResponseEntity<PointCategory> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(pointCategoryService.getCategoryById(id));
    }

    // [PUT] 카테고리 이름 수정
    @PutMapping("/{id}")
    public ResponseEntity<PointCategory> updateCategory(
            @PathVariable Long id,
            @RequestParam String newName) {
        PointCategory updated = pointCategoryService.updateCategory(id, newName);
        return ResponseEntity.ok(updated);
    }

    // [DELETE] 카테고리 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        pointCategoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
