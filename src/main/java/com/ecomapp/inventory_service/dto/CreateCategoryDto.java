package com.ecomapp.inventory_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryDto {
  @NotBlank(message = "Category name is required")
  @Size(min = 2, message = "Category name must be at least 3 characters long")
  private String name;

  @Size(max = 500, message = "Description can be up to 500 characters")
  private String description;
}
