package com.streetconnect.controller;

import com.streetconnect.dto.ApiResponse;
import com.streetconnect.dto.CategoryDto;
import com.streetconnect.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto dto) {
        CategoryDto created = categoryService.createCategory(dto);
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", created));
    }
}
