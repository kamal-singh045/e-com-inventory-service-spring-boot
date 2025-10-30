package com.ecomapp.inventory_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ecomapp.inventory_service.annotations.AllowedRoles;
import com.ecomapp.inventory_service.constant.RoleEnum;
import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.UploadFileDto;
import com.ecomapp.inventory_service.exception.CustomException;
import com.ecomapp.inventory_service.service.FileUploadService;

@RestController
@RequestMapping("/upload")
public class FileUploadController {
  private final FileUploadService fileUploadService;

  public FileUploadController(FileUploadService fileUploadService) {
    this.fileUploadService = fileUploadService;
  }

  @AllowedRoles({ RoleEnum.ADMIN })
  @PostMapping(value = "/images", consumes = "multipart/form-data")
  public ResponseEntity<ApiResponse<List<String>>> uploadImages(
    @RequestParam() String id,
    @RequestParam() String type,
    @RequestParam("files") List<MultipartFile> files
  ) {
    try {
      System.out.println("Received files: " + files.size());
      List<MultipartFile> validFiles = files.stream()
        .filter(file -> file != null && !file.isEmpty() && file.getOriginalFilename() != null && !file.getOriginalFilename().isBlank())
        .toList();
      if(files.isEmpty() || validFiles.isEmpty()) {
        throw new CustomException("No files found", HttpStatus.BAD_REQUEST);
      }
      UploadFileDto body = new UploadFileDto();
      body.setId(id);
      body.setType(type);
      body.setFiles(files);
      ApiResponse<List<String>> response = fileUploadService.uploadImages(body);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      System.out.println(e);
      throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
