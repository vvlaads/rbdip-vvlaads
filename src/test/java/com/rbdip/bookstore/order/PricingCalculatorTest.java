package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PricingCalculatorTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void appliesVipDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)), "vip", null);

        assertThat(total).isEqualByComparingTo("90.00");
    }

    @Test
    void appliesSave10Coupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("90.00");
    }

    @Test
    void returnsZeroForNegativePrice() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("-100.00"), 1)), "regular", null);

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void returnsZeroForZeroPrice() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("0.00"), 1)), "regular", null);

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void doesNotApplyExpensiveOrderDiscountAtThreshold() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("500.00"), 2)), "regular", null);

        assertThat(total).isEqualByComparingTo("1000.00");
    }

    @Test
    void appliesWholesaleDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)),
                "wholesale", null);

        assertThat(total).isEqualByComparingTo("85.00");
    }

    @Test
    void appliesSave20PercentCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)),
                "regular", "SAVE20PERCENT");

        assertThat(total).isEqualByComparingTo("80.00");
    }

    @Test
    void ignoresUnknownCustomerType() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)),
                "unknown", null);

        assertThat(total).isEqualByComparingTo("100.00");
    }

    @Test
    void ignoresUnknownCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)),
                "regular", "UNKNOWN");

        assertThat(total).isEqualByComparingTo("100.00");
    }

    @Test
    void doesNotApplyQuantityDiscountForTenItems() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 10)),
                "regular", null);

        assertThat(total).isEqualByComparingTo("100.00");
    }

    @Test
    void appliesQuantityDiscountForElevenItems() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 11)),
                "regular", null);

        assertThat(total).isEqualByComparingTo("104.50");
    }

    @Test
    void doesNotApplyExpensiveOrderDiscountAtExactlyOneThousand() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("1000.00"), 1)),
                "regular", null);

        assertThat(total).isEqualByComparingTo("1000.00");
    }

    @Test
    void appliesExpensiveOrderDiscountAboveOneThousand() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("1001.00"), 1)),
                "regular", null);

        assertThat(total).isEqualByComparingTo("980.98");
    }

    @Test
    void returnsZeroForEmptyOrder() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(), "regular", null);

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void doesNotReturnNegativeTotalAfterCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("5.00"), 1)),
                "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void roundsTotalToTwoDecimalPlaces() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("10.005"), 1)),
                "regular", null);

        assertThat(total).isEqualByComparingTo("10.01");
    }
}
