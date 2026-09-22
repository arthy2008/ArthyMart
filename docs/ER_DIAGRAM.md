# Database — ER Diagram (ARTHY MART)

```mermaid
erDiagram
    users ||--o{ products : "sells (seller_id)"
    users ||--o{ orders : "places (buyer_id)"
    users ||--o{ cart_items : "owns (user_id)"
    users ||--o{ reviews : "writes (user_id)"
    products ||--o{ cart_items : "in (product_id)"
    products ||--o{ order_items : "ordered as (product_id)"
    products ||--o{ reviews : "rated (product_id)"
    orders ||--o{ order_items : "contains (order_id)"

    users {
        INT id PK
        VARCHAR name
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR role "BUYER|SELLER|ADMIN"
        TIMESTAMP created_at
    }
    products {
        INT id PK
        INT seller_id FK
        VARCHAR name
        TEXT description
        DECIMAL price "10,2"
        INT stock_qty
        VARCHAR category
        VARCHAR image_url
        TIMESTAMP created_at
    }
    orders {
        INT id PK
        INT buyer_id FK
        VARCHAR status "PENDING|CONFIRMED|SHIPPED|DELIVERED|CANCELLED"
        DECIMAL total_amount "10,2"
        TIMESTAMP created_at
    }
    order_items {
        INT id PK
        INT order_id FK
        INT product_id FK
        INT quantity
        DECIMAL unit_price "10,2"
    }
    cart_items {
        INT id PK
        INT user_id FK
        INT product_id FK
        INT quantity
    }
    reviews {
        INT id PK
        INT product_id FK
        INT user_id FK
        INT rating "1..5"
        TEXT comment
        TIMESTAMP created_at
    }
```

## Notes
- Money uses `DECIMAL(10,2)` (`price`, `unit_price`, `total_amount`).
- `users.email` is UNIQUE.
- `cart_items(user_id, product_id)` is UNIQUE (one row per product per user).
- `reviews(product_id, user_id)` is UNIQUE (one review per purchase per product).
- All FKs use `ON DELETE CASCADE`.
- Indexes on `products(seller_id, category)`, `orders(buyer_id)`, `order_items(order_id, product_id)`, `cart_items(user_id)`, `reviews(product_id)`.
- Source of truth: `src/main/resources/db/schema.sql` (runtime) and `db/migrations/V1_init_schema.sql` (reference).
