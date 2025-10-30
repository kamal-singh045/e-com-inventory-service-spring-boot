package com.ecomapp.inventory_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomapp.inventory_service.annotations.AllowedRoles;
import com.ecomapp.inventory_service.constant.RoleEnum;
import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.CreateCategoryDto;
import com.ecomapp.inventory_service.exception.CustomException;
import com.ecomapp.inventory_service.model.CategoryModel;
import com.ecomapp.inventory_service.service.CategoryService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/category")
public class CategoryController {
  private final CategoryService categoryService;

  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @AllowedRoles({ RoleEnum.ADMIN, RoleEnum.USER })
  @GetMapping("{id}")
  public ResponseEntity<ApiResponse<CategoryModel>> getCategory(@PathVariable String id) {
    try {
      ApiResponse<CategoryModel> response = categoryService.getCategory(id);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @AllowedRoles({ RoleEnum.ADMIN })
  @PostMapping("")
  ResponseEntity<ApiResponse<String>> createCategory(@RequestBody CreateCategoryDto body) {
    try {
      ApiResponse<String> response = categoryService.createCategory(body);
    return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @AllowedRoles({ RoleEnum.ADMIN })
  @PutMapping("{id}")
  public ResponseEntity<ApiResponse<?>> updateCategory(@PathVariable String id, @RequestBody CreateCategoryDto body) {
    try {
      ApiResponse<?> response = categoryService.updateCategory(id, body);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @AllowedRoles({ RoleEnum.ADMIN })
  @DeleteMapping("{id}")
  public ResponseEntity<ApiResponse<?>> delereCategory(@PathVariable String id) {
    try {
      ApiResponse<?> response = categoryService.deleteCategory(id);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
