package com.streetconnect.service;

import com.streetconnect.dto.CategoryDto;
import com.streetconnect.entity.Category;
import com.streetconnect.exception.DuplicateResourceException;
import com.streetconnect.exception.ResourceNotFoundException;
import com.streetconnect.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllActiveCategories() {
        return getAllCategories();
    }

    @Transactional(readOnly = true)
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateResourceException("Category already exists: " + dto.getName());
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        if (dto.getIcon() != null) {
            category.setIcon(dto.getIcon());
        }
        category.setIsActive(true);

        Category saved = categoryRepository.save(category);
        return convertToDto(saved);
    }

    public CategoryDto convertToDto(Category category) {
        if (category == null) return null;
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIcon(),
                category.getIsActive()
        );
    }
}
