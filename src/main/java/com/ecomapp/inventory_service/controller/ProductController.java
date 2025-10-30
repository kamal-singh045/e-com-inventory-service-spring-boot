package com.ecomapp.inventory_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomapp.inventory_service.annotations.AllowedRoles;
import com.ecomapp.inventory_service.constant.RoleEnum;
import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.CreateProductDto;
import com.ecomapp.inventory_service.dto.ProductDetailResponseDto;
import com.ecomapp.inventory_service.exception.CustomException;
import com.ecomapp.inventory_service.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {
  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @AllowedRoles({ RoleEnum.ADMIN })
  @PostMapping("")
  public ResponseEntity<ApiResponse<String>> createProduct(@Valid @RequestBody CreateProductDto body) {
    try {
      ApiResponse<String> response = productService.createdProduct(body);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @AllowedRoles({ RoleEnum.ADMIN })
  @PutMapping("{id}")
  public ResponseEntity<ApiResponse<?>> updateProduct(@PathVariable String id, @Valid @RequestBody CreateProductDto body) {
    try {
      ApiResponse<?> response = productService.updateProduct(id, body);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @AllowedRoles({ RoleEnum.ADMIN })
  @DeleteMapping("{id}")
  public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable String id) {
    try {
      ApiResponse<?> response = productService.deleteProduct(id);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @AllowedRoles({ RoleEnum.ADMIN, RoleEnum.USER })
  @GetMapping("{id}")
  public ResponseEntity<ApiResponse<ProductDetailResponseDto>> getProduct(@PathVariable String id) {
    try {
      ApiResponse<ProductDetailResponseDto> response = productService.getProduct(id);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
