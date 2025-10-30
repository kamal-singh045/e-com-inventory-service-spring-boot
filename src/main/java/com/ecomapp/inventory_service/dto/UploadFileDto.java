package com.ecomapp.inventory_service.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadFileDto {
  private String id;
  private String type; // category or product
  private List<MultipartFile> files;
}
