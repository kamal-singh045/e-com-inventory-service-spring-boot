package com.ecomapp.inventory_service.grpc;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ecomapp.inventory_service.dto.ApiResponse;
import com.ecomapp.inventory_service.dto.CartItemResponseDto;
import com.ecomapp.inventory_service.model.ProductModel;
import com.ecomapp.inventory_service.service.CartService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {
  private final CartService cartService;

  public InventoryGrpcService(CartService cartService) {
    this.cartService = cartService;
  }

  @Override
  public void getUserCart(GetUserCartRequest request, StreamObserver<GetUserCartResponse> responseObserver) {
    try {
      log.info("Grpc connection is successful.....................");
      log.info("gRPC getUserCart called for userId: {}", request.getUserId());
      ApiResponse<Map<String, Object>> cartResponse = cartService.getCart(request.getUserId());

      if(!cartResponse.isSuccess()) {
        responseObserver.onError(Status.NOT_FOUND
            .withDescription(cartResponse.getMessage())
            .asRuntimeException());
        return;
      }

      Map<String, Object> data = cartResponse.getData();
      double totalMrp = ((Number) data.get("totalMrp")).doubleValue();
      double totalPrice = ((Number) data.get("totalAmountToPay")).doubleValue();

      // Convert DTO items to gRPC messages
      @SuppressWarnings("unchecked")
      List<CartItemResponseDto> items = (List<CartItemResponseDto>) data.get("items");
      
      List<CartItemResponse> grpcItems = items.stream()
          .map(this::convertToGrpcCartItem)
          .collect(Collectors.toList());

      // build Response
      GetUserCartResponse response = GetUserCartResponse.newBuilder()
          .setTotalMrp((float) totalMrp)
          .setTotalPrice((float) totalPrice)
          .addAllItems(grpcItems)
          .build();
      log.info("Returning cart - totalMrp: {}, totalPrice: {}", totalMrp, totalPrice);

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("Error getting user cart: {}", e.getMessage(), e);
      responseObserver.onError(Status.INTERNAL
          .withDescription("Error retrieving cart: " + e.getMessage())
          .asRuntimeException());
    }
  }

  /**
   * Convert CartItemResponseDto to gRPC CartItemResponse
   */
  private CartItemResponse convertToGrpcCartItem(CartItemResponseDto dto) {
    ProductModel product = dto.getProduct();
    
    CartProduct grpcProduct = CartProduct.newBuilder()
      .setId(product.getId())
      .setCategoryId(product.getCategoryId() != null ? product.getCategoryId() : "")
      .setName(product.getName() != null ? product.getName() : "")
      .setDescription(product.getDescription() != null ? product.getDescription() : "")
      .addAllImageUrls(product.getImageUrls() != null ? product.getImageUrls() : List.of())
      .setIsActive(product.getIsActive() != null ? product.getIsActive() : false)
      .setMrp(product.getMrp() != null ? product.getMrp().floatValue() : 0.0f)
      .setDiscount(product.getDiscount() != null ? product.getDiscount().floatValue() : 0.0f)
      .setQuantity(product.getQuantity() != null ? product.getQuantity().floatValue() : 0.0f)
      .setAvailableStock(product.getAvailableStock() != null ? product.getAvailableStock().floatValue() : 0.0f)
      .setUnit(product.getUnit() != null ? product.getUnit().toString() : "")
      .build();
    
    return CartItemResponse.newBuilder()
      .setId(dto.getId())
      .setItemCount(dto.getItemCount())
      .setProduct(grpcProduct)
      .build();
  }

  @Override
  public void clearUserCart(ClearUserCartRequest request, StreamObserver<ClearUserCartResponse> responseObserver) {
    try {
      log.info("gRPC clearUserCart called for userId: {}", request.getUserId());
      
      // Clear cart
      ApiResponse<String> clearResponse = cartService.clearCart(request.getUserId());

      // Build response
      ClearUserCartResponse response = ClearUserCartResponse.newBuilder()
          .setSuccess(clearResponse.isSuccess())
          .setMessage(clearResponse.getMessage())
          .build();

      log.info("Cart cleared successfully for userId: {}", request.getUserId());
      
      responseObserver.onNext(response);
      responseObserver.onCompleted();

    } catch (Exception e) {
      log.error("Error clearing user cart: {}", e.getMessage(), e);
      
      ClearUserCartResponse response = ClearUserCartResponse.newBuilder()
          .setSuccess(false)
          .setMessage("Error clearing cart: " + e.getMessage())
          .build();
      
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
  }
}
