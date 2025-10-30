package com.ecomapp.inventory_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private PaginationResponse pagination;

  public ApiResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  public ApiResponse(boolean success, String message, T data, PaginationResponse pagination) {
    this.success = success;
    this.message = message;
    this.data = data;
    this.pagination = pagination;
  }
}
