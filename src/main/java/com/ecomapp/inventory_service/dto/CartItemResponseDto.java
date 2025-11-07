package com.ecomapp.inventory_service.dto;

import java.time.LocalDateTime;

import com.ecomapp.inventory_service.model.ProductModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponseDto {
  private String id;
  private ProductModel product;
  private Integer itemCount;
  private LocalDateTime updatedAt;
}
