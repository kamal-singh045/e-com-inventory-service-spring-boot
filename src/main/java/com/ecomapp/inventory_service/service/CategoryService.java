package com.ecomapp.inventory_service.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.CreateCategoryDto;
import com.ecomapp.inventory_service.exception.CustomException;
import com.ecomapp.inventory_service.model.CategoryModel;
import com.ecomapp.inventory_service.repository.CategoryRepository;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public ApiResponse<String> createCategory(CreateCategoryDto body) {
    CategoryModel category = new CategoryModel();
    category.setName(body.getName());
    category.setDescription(body.getDescription());
    category.setIsActive(true);
    categoryRepository.save(category);
    return new ApiResponse<>(true, "Category created successfully", category.getId());
  }

  public ApiResponse<?> deleteCategory(String id) {
    categoryRepository.deleteById(id);
    return new ApiResponse<>(true, "Category deleted successfully");
  }

  public ApiResponse<?> updateCategory(String id, CreateCategoryDto body) {
    CategoryModel category = categoryRepository.findById(id).orElseThrow(() -> 
    new CustomException("Category not found", HttpStatus.NOT_FOUND));
    category.setName(body.getName());
    category.setDescription(body.getDescription());
    categoryRepository.save(category);
    return new ApiResponse<>(true, "Category updated successfully");
  }

  public ApiResponse<CategoryModel> getCategory(String id) {
    CategoryModel category = categoryRepository.findById(id).orElseThrow(() -> 
      new CustomException("Category not found", HttpStatus.NOT_FOUND));
    String currentPath = category.getImageUrl();
    String imageUrl = currentPath != null ? "http://localhost:4000/uploads/" + category.getImageUrl() : null;
    category.setImageUrl(imageUrl);
    return new ApiResponse<CategoryModel>(true, "Category found", category);
  }
}
