package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.rbdip.bookstore.product.Product;
import com.rbdip.bookstore.product.ProductRepository;

@Component
public class OrderPricingService {
    private final ProductRepository productRepository;
    private final PricingCalculator pricingCalculator = new PricingCalculator();
    private final OrderValidator orderValidator;

    public OrderPricingService(
            ProductRepository productRepository,
            OrderValidator orderValidator) {
        this.productRepository = productRepository;
        this.orderValidator = orderValidator;
    }

    public OrderPricingResult calculatePriceForOrder(CreateOrderRequest request) {
        List<Product> products = new ArrayList<>();
        List<PricingCalculator.LineItem> lineItems = new ArrayList<>();

        for (CreateOrderRequest.Item raw : request.items()) {
            Product product = productRepository.findById(raw.productId())
                    .orElseThrow(() -> new IllegalArgumentException("product " + raw.productId() + " not found"));
            orderValidator.validateQuantity(raw.quantity());
            int quantity = raw.quantity() == null ? 1 : raw.quantity();
            products.add(product);
            lineItems.add(new PricingCalculator.LineItem(product.getPrice(), quantity));
        }

        BigDecimal total = pricingCalculator.calculateOrderTotal(
                lineItems, request.customerType() == null ? "regular" : request.customerType(), request.couponCode());

        return new OrderPricingResult(
                products,
                lineItems,
                total);
    }
}
