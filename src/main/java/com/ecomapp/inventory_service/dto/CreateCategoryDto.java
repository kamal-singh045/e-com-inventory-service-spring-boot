package com.ecomapp.inventory_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryDto {
  private String name;
  private String description;
}
