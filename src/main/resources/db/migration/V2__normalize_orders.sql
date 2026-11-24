CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(50)
);

INSERT INTO customers (full_name, address, phone)
SELECT DISTINCT customer_full_name, customer_address, customer_phone
FROM orders;

ALTER TABLE orders ADD COLUMN customer_id BIGINT;

UPDATE orders o
SET customer_id = c.id
FROM customers c
WHERE o.customer_full_name = c.full_name
  AND o.customer_address IS NOT DISTINCT FROM c.address
  AND o.customer_phone IS NOT DISTINCT FROM c.phone;


ALTER TABLE order_items ADD COLUMN product_id BIGINT;

UPDATE order_items o
SET product_id = p.id
FROM products p
WHERE o.product_name = p.name
  AND o.product_price = p.price;

ALTER TABLE orders DROP COLUMN customer_full_name;
ALTER TABLE orders DROP COLUMN customer_address;
ALTER TABLE orders DROP COLUMN customer_phone;

ALTER TABLE order_items DROP COLUMN product_name;
ALTER TABLE order_items DROP COLUMN product_price;

ALTER TABLE orders
    ALTER COLUMN customer_id SET NOT NULL,
    ADD CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id);

ALTER TABLE order_items
    ALTER COLUMN product_id SET NOT NULL,
    ADD CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products(id);