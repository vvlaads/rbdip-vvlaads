package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.util.List;

import com.rbdip.bookstore.product.Product;

public record OrderPricingResult(
        List<Product> products,
        List<PricingCalculator.LineItem> lineItems,
        BigDecimal total) {
}
