package com.rbdip.bookstore.order;

import com.rbdip.bookstore.review.PurchaseChecker;
import org.springframework.stereotype.Component;

@Component
public class OrderPurchaseChecker implements PurchaseChecker {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderPurchaseChecker(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public boolean hasAnyPurchase() {
        return !orderRepository.findAll().isEmpty()
                && !orderItemRepository.findAll().isEmpty();
    }
}