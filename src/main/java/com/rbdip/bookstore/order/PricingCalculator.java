package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Модуль расчёта цены заказа. Намеренно почти не покрыт тестами и
 * содержит magic numbers / нечитаемые ветвления скидок - цель для
 * характеризационных тестов (ЛР2) и mutation-testing гейта PIT (ЛР5).
 */
public class PricingCalculator {
    private static final Integer ITEM_QUANTITY_FOR_DISCOUNT = 10;
    private static final BigDecimal DISCOUNT_FOR_LARGE_ITEM_QUANTITY = BigDecimal.valueOf(0.95);

    private static final BigDecimal VIP_DISCOUNT = BigDecimal.valueOf(0.9);
    private static final BigDecimal WHOLESALE_DISCOUNT = BigDecimal.valueOf(0.85);
    private static final BigDecimal SAVE20_DISCOUNT = BigDecimal.valueOf(0.8);

    private static final BigDecimal DISCOUNT_FOR_EXPENSIVE_ORDER = BigDecimal.valueOf(0.98);
    private static final BigDecimal EXPENSIVE_ORDER_THRESHOLD = BigDecimal.valueOf(1000);
    private static final BigDecimal SAVE10_DISCOUNT_AMOUNT = BigDecimal.TEN;

    private static final int MONEY_SCALE = 2;

    public record LineItem(BigDecimal price, int quantity) {
    }

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = BigDecimal.ZERO;

        for (LineItem item : items) {
            total = total.add(calculateLinePrice(item));
        }

        total = applyCustomerDiscount(total, customerType);
        total = applyCouponDiscount(total, couponCode);
        total = ensureNonNegative(total);
        total = applyExpensiveOrderDiscount(total);

        return total.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLinePrice(LineItem item) {
        BigDecimal price = item.price().multiply(BigDecimal.valueOf(item.quantity()));

        if (item.quantity() > ITEM_QUANTITY_FOR_DISCOUNT) {
            price = price.multiply(DISCOUNT_FOR_LARGE_ITEM_QUANTITY);
        }

        return price;
    }

    private BigDecimal applyCustomerDiscount(BigDecimal total, String customerType) {
        if ("vip".equals(customerType)) {
            return total.multiply(VIP_DISCOUNT);
        } else if ("wholesale".equals(customerType)) {
            return total.multiply(WHOLESALE_DISCOUNT);
        }

        return total;
    }

    private BigDecimal applyCouponDiscount(BigDecimal total, String couponCode) {
        if ("SAVE10".equals(couponCode)) {
            return total.subtract(SAVE10_DISCOUNT_AMOUNT);
        } else if ("SAVE20PERCENT".equals(couponCode)) {
            return total.multiply(SAVE20_DISCOUNT);
        }

        return total;
    }

    private BigDecimal applyExpensiveOrderDiscount(BigDecimal total) {
        if (total.compareTo(EXPENSIVE_ORDER_THRESHOLD) > 0) {
            return total.multiply(DISCOUNT_FOR_EXPENSIVE_ORDER);
        }

        return total;
    }

    private BigDecimal ensureNonNegative(BigDecimal total) {
        return total.max(BigDecimal.ZERO);
    }
}
