package com.ecomapp.inventory_service.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.CartItemResponseDto;
import com.ecomapp.inventory_service.dto.ManageCartDto;
import com.ecomapp.inventory_service.exception.CustomException;
import com.ecomapp.inventory_service.model.CartItemModel;
import com.ecomapp.inventory_service.model.CartModel;
import com.ecomapp.inventory_service.model.ProductModel;
import com.ecomapp.inventory_service.repository.CartItemRepository;
import com.ecomapp.inventory_service.repository.CartRepository;
import com.ecomapp.inventory_service.repository.ProductRepository;

@Service
public class CartService {
  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;

  public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
  }
  
  @Transactional
  public ApiResponse<Map<String, Object>> manageCart(String userId, ManageCartDto data) {
    // Validate input
    if (userId == null || userId.isEmpty()) {
      throw new CustomException("User ID is required", HttpStatus.BAD_REQUEST);
    }
    
    if (data == null) {
      throw new CustomException("Cart data cannot be empty", HttpStatus.BAD_REQUEST);
    }

    // Get or create cart
    CartModel cart = cartRepository.findByUserId(userId)
        .orElseGet(() -> {
          CartModel newCart = CartModel.builder()
              .userId(userId)
              .build();
          return cartRepository.save(newCart);
        });

    // Fetch product
    String productId = data.getProductId();
    ProductModel product = productRepository.findById(productId)
        .orElseThrow(() -> new CustomException("Product not found", HttpStatus.NOT_FOUND));

    // Check if product is active
    if (!product.getIsActive()) {
      throw new CustomException("Product " + product.getName() + " is not available", HttpStatus.BAD_REQUEST);
    }

    // Get existing cart item if exists
    Optional<CartItemModel> existingCartItem = cartItemRepository
        .findByCartIdAndProductId(cart.getId(), productId);

    String action;
    String message;

    // Case 1: Remove item if itemCount is 0
    if (data.getItemCount() == 0) {
      if (existingCartItem.isPresent()) {
        cartItemRepository.deleteByCartIdAndProductId(cart.getId(), productId);
        action = "removed";
        message = product.getName() + " removed from cart";
      } else {
        throw new CustomException("Product not found in cart", HttpStatus.NOT_FOUND);
      }
    } 
    // Case 2: Check stock availability
    else if (product.getAvailableStock() < data.getItemCount()) {
      throw new CustomException(
          product.getName() + " - Only " + product.getAvailableStock().intValue() + 
          " items available (requested: " + data.getItemCount() + ")", 
          HttpStatus.BAD_REQUEST
      );
    }
    // Case 3: Update or add item
    else {
      if (existingCartItem.isPresent()) {
        // Update existing item
        CartItemModel cartItem = existingCartItem.get();
        cartItem.setItemCount(data.getItemCount());
        cartItemRepository.save(cartItem);
        action = "updated";
        message = product.getName() + " quantity updated to " + data.getItemCount();
      } else {
        // Add new item
        CartItemModel newCartItem = CartItemModel.builder()
            .cartId(cart.getId())
            .productId(productId)
            .itemCount(data.getItemCount())
            .build();
        cartItemRepository.save(newCartItem);
        action = "added";
        message = product.getName() + " added to cart";
      }
    }

    // Prepare response
    Map<String, Object> result = new HashMap<>();
    result.put("cartId", cart.getId());
    result.put("action", action);
    result.put("productId", productId);
    result.put("productName", product.getName());
    result.put("itemCount", data.getItemCount());

    return new ApiResponse<>(true, message, result);
  }

  public ApiResponse<Map<String, Object>> getCart(String userId) {
    // Validate input
    if (userId == null || userId.isEmpty()) {
      throw new CustomException("User ID is required", HttpStatus.BAD_REQUEST);
    }

    // Find cart by userId or create empty response
    Optional<CartModel> cartOpt = cartRepository.findByUserId(userId);
    if(cartOpt.isEmpty()) {
      Map<String, Object> emptyResult = new HashMap<>();
      emptyResult.put("totalMrp", 0.0);
      emptyResult.put("totalDiscount", 0.0);
      emptyResult.put("totalAmountToPay", 0.0);
      emptyResult.put("items", Collections.emptyList());
      return new ApiResponse<>(true, "Cart is empty", emptyResult);
    }

    // Find all cart items for the cart
    List<CartItemModel> cartItems = cartItemRepository.findByCartId(cartOpt.get().getId());
    // If cart is empty
    if (cartItems.isEmpty()) {
      Map<String, Object> emptyResult = new HashMap<>();
      emptyResult.put("totalMrp", 0.0);
      emptyResult.put("totalDiscount", 0.0);
      emptyResult.put("totalAmountToPay", 0.0);
      emptyResult.put("items", Collections.emptyList());
      return new ApiResponse<>(true, "Cart is empty", emptyResult);
    }

    // Fetch all products in one query for efficiency
    List<String> productIds = cartItems.stream()
        .map(CartItemModel::getProductId)
        .collect(Collectors.toList());
    
    List<ProductModel> products = productRepository.findAllById(productIds);
    Map<String, ProductModel> productMap = products.stream()
        .collect(Collectors.toMap(ProductModel::getId, p -> p));

    // Build response DTOs
    List<CartItemResponseDto> cartItemResponses = cartItems.stream()
        .map(cartItem -> {
          ProductModel product = productMap.get(cartItem.getProductId());
          return CartItemResponseDto.builder()
              .id(cartItem.getId())
              .product(product)
              .itemCount(cartItem.getItemCount())
              .updatedAt(cartItem.getUpdatedAt())
              .build();
        })
        .collect(Collectors.toList());

    double totalMrp = cartItemResponses.stream()
        .mapToDouble(item -> item.getProduct().getMrp() * item.getItemCount()).sum();

    double totalDiscount = cartItemResponses.stream()
        .mapToDouble(item -> {
          double mrp = item.getProduct().getMrp();
          double discount = item.getProduct().getDiscount();
          return (mrp * item.getItemCount()) * (discount / 100.0);
        }).sum();

    double totalAmountToPay = totalMrp - totalDiscount;

    Map<String, Object> result = new HashMap<>();
    result.put("totalMrp", Double.parseDouble(String.format("%.2f", totalMrp)));
    result.put("totalDiscount", Double.parseDouble(String.format("%.2f", totalDiscount)));
    result.put("totalAmountToPay", totalAmountToPay);
    result.put("items", cartItemResponses);

    return new ApiResponse<>(true, "Cart retrieved successfully", result);
  }

  @Transactional
  public ApiResponse<String> clearCart(String userId) {
    // Validate input
    if (userId == null || userId.isEmpty()) {
      throw new CustomException("User ID is required", HttpStatus.BAD_REQUEST);
    }

    // Find cart by userId
    Optional<CartModel> cartOpt = cartRepository.findByUserId(userId);

    if (cartOpt.isEmpty()) {
      return new ApiResponse<>(true, "No cart found to clear", null);
    }

    CartModel cart = cartOpt.get();
    cartItemRepository.deleteAllByCartId(cart.getId());
    return new ApiResponse<>(true, "Cart cleared successfully");
  }
}
