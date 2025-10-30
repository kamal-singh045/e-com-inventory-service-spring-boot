package com.ecomapp.inventory_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ecomapp.inventory_service.model.CategoryModel;

@Repository
public interface CategoryRepository extends MongoRepository<CategoryModel, String> { }
