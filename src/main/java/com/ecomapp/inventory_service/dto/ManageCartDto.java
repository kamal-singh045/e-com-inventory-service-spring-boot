package com.ecomapp.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManageCartDto {
 @NotBlank(message = "Product ID is required")
  private String productId;

  @NotBlank(message = "Item count is required")
  @Min(value = 0, message = "Item count must be at least 0")
  private Integer itemCount;
}
