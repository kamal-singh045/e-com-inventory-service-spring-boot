package com.ecomapp.inventory_service.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.CreateProductDto;
import com.ecomapp.inventory_service.dto.ProductDetailResponseDto;
import com.ecomapp.inventory_service.exception.CustomException;
import com.ecomapp.inventory_service.model.CategoryModel;
import com.ecomapp.inventory_service.model.ProductModel;
import com.ecomapp.inventory_service.repository.CategoryRepository;
import com.ecomapp.inventory_service.repository.ProductRepository;

@Service
public class ProductService {
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
    this.productRepository = productRepository;
    this.categoryRepository = categoryRepository;
  }

  public ApiResponse<String> createdProduct(CreateProductDto body) {
    ProductModel product = new ProductModel();
    product.setName(body.getName());
    product.setDescription(body.getDescription());
    product.setIsActive(true);
    product.setMrp(body.getMrp());
    product.setDiscount(body.getDiscount());
    product.setQuantity(body.getQuantity());
    product.setAvailableStock(body.getQuantity());
    product.setUnit(body.getUnit());
    product.setCategoryId(body.getCategoryId());
    product.setImageUrls(new ArrayList<>());
    productRepository.save(product);
    return new ApiResponse<>(true, "Product created successfully", product.getId());
  }

  public ApiResponse<?> updateProduct(String id, CreateProductDto body) {
    ProductModel product = productRepository.findById(id).orElseThrow(() -> 
      new CustomException("Product not found", HttpStatus.NOT_FOUND));
    product.setName(body.getName());
    product.setDescription(body.getDescription());
    product.setMrp(body.getMrp());
    product.setDiscount(body.getDiscount());
    product.setQuantity(body.getQuantity());
    product.setAvailableStock(body.getQuantity());
    product.setUnit(body.getUnit());
    productRepository.save(product);
    return new ApiResponse<>(true, "Product updated successfully");
  }

  public ApiResponse<?> deleteProduct(String id) {
    productRepository.deleteById(id);
    return new ApiResponse<>(true, "Product deleted successfully");
  }

  public ApiResponse<ProductDetailResponseDto> getProduct(String id) {
    ProductModel product = productRepository.findById(id).orElseThrow(() -> 
      new CustomException("Product not found", HttpStatus.NOT_FOUND));
    Optional<CategoryModel> categoryOpt = categoryRepository.findById(product.getCategoryId());
    ProductDetailResponseDto productDetails = new ProductDetailResponseDto();
    productDetails.setId(product.getId());
    productDetails.setName(product.getName());
    productDetails.setDescription(product.getDescription());
    productDetails.setImageUrls(product.getImageUrls());
    productDetails.setMrp(product.getMrp());
    productDetails.setDiscount(product.getDiscount());
    productDetails.setQuantity(product.getQuantity());
    productDetails.setAvailableStock(product.getAvailableStock());
    productDetails.setUnit(product.getUnit());
    productDetails.setIsActive(product.getIsActive());
    // Set category details if present
    categoryOpt.ifPresent(category -> {
        ProductDetailResponseDto.CategoryDetail categoryDetail = new ProductDetailResponseDto.CategoryDetail();
        categoryDetail.setId(category.getId());
        categoryDetail.setName(category.getName());
        categoryDetail.setDescription(category.getDescription());
        categoryDetail.setImageUrl(category.getImageUrl());
        categoryDetail.setIsActive(category.getIsActive());
        productDetails.setCategory(categoryDetail);
    });
    return new ApiResponse<ProductDetailResponseDto>(true, "Product found", productDetails);
  }
}
