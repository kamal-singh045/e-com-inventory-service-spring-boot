package com.ecomapp.inventory_service.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.ecomapp.inventory_service.constant.UnitEnum;

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
@Document(collection = "product")
public class ProductModel {
  @Id
  private String id;

  // reference with category
  @Field
  @Indexed
  private String categoryId;

  @Field
  @Indexed(unique = true)
  private String name;

  @Field
  private String description;

  @Field
  private List<String> imageUrls;

  @Field
  private Boolean isActive;

  @Field
  private Double mrp;

  @Field
  private Double discount;

  @Field
  private Double quantity;

  @Field
  private Double availableStock;

  @Field
  private UnitEnum unit;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;
}
