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
    private final CustomerRepository customerRepository;
    private final OrderValidator orderValidator;
    private final OrderPricingService orderPricingService;
    private final OrderNotificationService orderNotificationService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CustomerRepository customerRepository,
            OrderValidator orderValidator,
            OrderPricingService orderPricingService,
            OrderNotificationService orderNotificationService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.orderValidator = orderValidator;
        this.orderPricingService = orderPricingService;
        this.orderNotificationService = orderNotificationService;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        orderValidator.validate(request);

        OrderPricingResult pricingResult = orderPricingService.calculatePriceForOrder(request);

        String[] nameParts = request.customerFullName().trim().split("\\s+", 2);

        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        Customer customer = customerRepository.save(new Customer(
                firstName,
                lastName,
                request.customerAddress(),
                request.customerPhone()));

        Order order = new Order(customer, "new");
        order = orderRepository.save(order);

        for (int i = 0; i < pricingResult.products().size(); i++) {
            Product product = pricingResult.products().get(i);
            int quantity = pricingResult.lineItems().get(i).quantity();

            orderItemRepository.save(new OrderItem(order, product, quantity));
        }

        String customerName = customer.getFirstName() + " " + customer.getLastName();

        orderNotificationService.sendConfirmationEmail(
                customerName,
                order.getId(),
                pricingResult.total());

        return order;
    }

}
