package com.ecomapp.inventory_service.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ecomapp.inventory_service.model.CartModel;

public interface CartRepository extends MongoRepository<CartModel, String> {
  Optional<CartModel> findByUserId(String userId);
}
