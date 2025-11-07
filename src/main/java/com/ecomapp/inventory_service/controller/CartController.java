package com.ecomapp.inventory_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecomapp.inventory_service.constant.AppConstants;
import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.ManageCartDto;
import com.ecomapp.inventory_service.exception.CustomException;
import com.ecomapp.inventory_service.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {
  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping("")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getCart(@RequestHeader(AppConstants.X_USER_ID) String userId) {
    try {
      ApiResponse<Map<String, Object>> response = this.cartService.getCart(userId);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/manage")
  public ResponseEntity<ApiResponse<Map<String, Object>>> manageCart(
      @RequestHeader(AppConstants.X_USER_ID) String userId,
      @RequestBody ManageCartDto data
    ) {
    try {
      ApiResponse<Map<String, Object>> response = this.cartService.manageCart(userId, data);
    return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
