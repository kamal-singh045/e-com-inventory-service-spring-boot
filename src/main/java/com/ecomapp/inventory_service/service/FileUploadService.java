package com.ecomapp.inventory_service.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.UploadFileDto;
import com.ecomapp.inventory_service.exception.CustomException;
import com.ecomapp.inventory_service.model.CategoryModel;
import com.ecomapp.inventory_service.model.ProductModel;
import com.ecomapp.inventory_service.repository.CategoryRepository;
import com.ecomapp.inventory_service.repository.ProductRepository;

@Service
public class FileUploadService {
  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  public FileUploadService(CategoryRepository categoryRepository, ProductRepository productRepository) {
    this.categoryRepository = categoryRepository;
    this.productRepository = productRepository;
  }

  public ApiResponse<List<String>> uploadImages(UploadFileDto body) throws IOException {
    String uploadDir = System.getProperty("user.dir") + "/uploads/" + body.getType() + "/";

    List<String> savedPaths = new ArrayList<>();

    File directory = new File(uploadDir);
    if (!directory.exists()) {
        directory.mkdirs();
    }
    // Save files to disk
    for(MultipartFile file : body.getFiles()) {
      String originalFileName = file.getOriginalFilename();
      String baseName = originalFileName;
      String extension = "";
      int lastDotIndex = originalFileName.lastIndexOf('.');
      if(lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
        baseName = originalFileName.substring(0, lastDotIndex);
        extension = originalFileName.substring(lastDotIndex);
      }
      String sanitizedBaseName = baseName.replaceAll("\\W", "_");
      String fileName = System.currentTimeMillis() + "_" + sanitizedBaseName + extension;
      File destinationFile = new File(uploadDir + fileName);
      file.transferTo(destinationFile);
      // Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
      savedPaths.add(body.getType() + "/" + fileName);
    }

    // update db record
    if("product".equals(body.getType())) {
      ProductModel productModel = productRepository.findById(body.getId())
        .orElseThrow(() -> new CustomException("Product not found", HttpStatus.NOT_FOUND));

      List<String> existingUrls = productModel.getImageUrls();
      List<String> finalUrls = new ArrayList<>(existingUrls);
      finalUrls.addAll(savedPaths);
      productModel.setImageUrls(finalUrls);
      productRepository.save(productModel);
    } else if("category".equals(body.getType())) {
      CategoryModel categoryModel = categoryRepository.findById(body.getId())
        .orElseThrow(() -> new CustomException("Category not found", HttpStatus.NOT_FOUND));
      categoryModel.setImageUrl(savedPaths.get(0));
      categoryRepository.save(categoryModel);
    } else {
      throw new CustomException("Invalid type", HttpStatus.BAD_REQUEST);
    }
    return new ApiResponse<>(true, "Files uploaded successfully", savedPaths);
  }
}
