package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * God-класс: валидация, расчёт цены, персистентность и "уведомление
 * клиента" смешаны в одном методе. Цель для рефакторинга по SRP в ЛР1.
 */
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderValidator orderValidator;
    private final OrderPricingService orderPricingService;
    private final OrderNotificationService orderNotificationService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            OrderValidator orderValidator,
            OrderPricingService orderPricingService,
            OrderNotificationService orderNotificationService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderValidator = orderValidator;
        this.orderPricingService = orderPricingService;
        this.orderNotificationService = orderNotificationService;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        orderValidator.validate(request);

        OrderPricingResult pricingResult = orderPricingService.calculatePriceForOrder(request);

        Order order = new Order(
                request.customerFullName(), request.customerAddress(), request.customerPhone(), "new");
        order = orderRepository.save(order);

        for (int i = 0; i < pricingResult.products().size(); i++) {
            Product product = pricingResult.products().get(i);
            int quantity = pricingResult.lineItems().get(i).quantity();
            orderItemRepository.save(new OrderItem(order.getId(), product.getName(), product.getPrice(), quantity));
        }

        orderNotificationService.sendConfirmationEmail(request.customerFullName(), order.getId(),
                pricingResult.total());

        return order;
    }

}
