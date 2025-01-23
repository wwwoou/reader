package com.example.cursorlibrary.service;

import com.example.cursorlibrary.dto.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    CategoryDTO getCategory(Long id);
    Page<CategoryDTO> getAllCategories(Pageable pageable);
} 