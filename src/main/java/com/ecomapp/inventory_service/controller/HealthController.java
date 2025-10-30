package com.ecomapp.inventory_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
  @GetMapping("/health")
  public String InventoryServiceHealth() {
    String ans = "Inventory service is healthy";
    return ans;
  }
}
