package com.ecomapp.inventory_service.dto;

import java.util.List;

import com.ecomapp.inventory_service.constant.UnitEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailResponseDto {
  @Getter
  @Setter
  public static class CategoryDetail {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean isActive;
  }
  private String id;
  private String name;
  private CategoryDetail category;
  private String description;
  private List<String> imageUrls;
  private Double mrp;
  private Double discount;
  private Double quantity;
  private Double availableStock;
  private UnitEnum unit;
  private Boolean isActive;
}
