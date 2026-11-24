package com.rbdip.bookstore.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Намеренно нет @EntityGraph/JOIN FETCH-варианта - используется в цикле
    // контроллером, что и создаёт N+1 (см. OrderController#listOrders).
    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
            SELECT new com.rbdip.bookstore.order.OrderItemSummary(
                i.order.id, i.product.name, i.quantity
            )
            FROM OrderItem i
            WHERE i.order.id IN :orderIds
            """)
    List<OrderItemSummary> findAllWithProductByOrderIds(List<Long> orderIds);
}
