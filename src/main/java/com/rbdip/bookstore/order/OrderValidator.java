package com.rbdip.bookstore.order;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class OrderValidator {
    public void validate(CreateOrderRequest request) {
        validateCustomerFullName(request.customerFullName());
        validateCustomerAddress(request.customerAddress());
        validateItems(request.items());
    }

    private void validateCustomerFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("customerFullName is required");
        }
    }

    private void validateCustomerAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("customerAddress is required");
        }
    }

    private void validateItems(List<CreateOrderRequest.Item> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one item");
        }
    }

    public void validateQuantity(Integer quantity) {
        if (quantity != null && quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }
}
