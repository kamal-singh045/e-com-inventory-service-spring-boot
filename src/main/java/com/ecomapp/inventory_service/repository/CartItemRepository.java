package com.ecomapp.inventory_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ecomapp.inventory_service.model.CartItemModel;

public interface CartItemRepository extends MongoRepository<CartItemModel, String> {
  Optional<CartItemModel> findByCartIdAndProductId(String cartId, String productId);
  List<CartItemModel> findByCartId(String cartId);
  void deleteByCartIdAndProductId(String cartId, String productId);
  void deleteAllByCartId(String cartId);
}
