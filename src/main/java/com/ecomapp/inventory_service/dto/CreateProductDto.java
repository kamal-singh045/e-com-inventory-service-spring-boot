package com.ecomapp.inventory_service.dto;

import com.ecomapp.inventory_service.constant.UnitEnum;
import jakarta.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductDto {
  @NotBlank(message = "Product name is required")
  @Size(min = 2, message = "Product name must be at least 3 characters long")
  private String name;

  @Size(max = 500, message = "Description can be up to 500 characters")
  private String description;

  @NotBlank(message = "Category ID is required")
  private String categoryId;

  @NotNull(message = "MRP is required")
  @Positive(message = "MRP must be greater than 0")
  private Double mrp;

  @NotNull(message = "Discount is required")
  @Min(value = 0, message = "Discount cannot be negative")
  @Max(value = 100, message = "Discount cannot exceed 100%")
  private Double discount;

  @NotNull(message = "Quantity is required")
  @PositiveOrZero(message = "Quantity cannot be negative")
  private Double quantity;

  @NotNull(message = "Available stock is required")
  @PositiveOrZero(message = "Available stock cannot be negative")
  private Double availableStock;

  @NotNull(message = "Unit is required")
  private UnitEnum unit;
}
