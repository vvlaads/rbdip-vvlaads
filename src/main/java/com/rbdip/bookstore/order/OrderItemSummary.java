package com.rbdip.bookstore.order;

public record OrderItemSummary(
        Long orderId,
        String productName,
        Integer quantity) {
}