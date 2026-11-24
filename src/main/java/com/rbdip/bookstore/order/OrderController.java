package com.rbdip.bookstore.order;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderController(
            OrderService orderService, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return Map.of("id", order.getId(), "status", order.getStatus());
    }

    @GetMapping("/orders")
    public List<Map<String, Object>> listOrders() {
        List<Order> orders = orderRepository.findAllWithCustomer();

        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .toList();

        List<OrderItemSummary> items = orderItemRepository.findAllWithProductByOrderIds(orderIds);

        Map<Long, List<OrderItemSummary>> itemsByOrder = items.stream()
                .collect(java.util.stream.Collectors.groupingBy(OrderItemSummary::orderId));

        return orders.stream()
                .map(order -> {
                    List<Map<String, Object>> orderItems = itemsByOrder
                            .getOrDefault(order.getId(), List.of())
                            .stream()
                            .map(item -> Map.<String, Object>of(
                                    "productName", item.productName(),
                                    "quantity", item.quantity()))
                            .toList();

                    return Map.<String, Object>of(
                            "id", order.getId(),
                            "customerFullName",
                            order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                            "status", order.getStatus(),
                            "items", orderItems);
                })
                .toList();
    }
}
