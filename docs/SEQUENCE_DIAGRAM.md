# Sequence Diagram (ARTHY MART)

Layered flow: Browser → Filter → Servlet / Front Controller → Service → DAO → HikariCP → H2 → Response.

## 1. Login (F1)

```mermaid
sequenceDiagram
    participant B as Browser (login.jsp / fetch)
    participant F as SecurityHeadersFilter
    participant S as AuthServlet (/api/auth/login)
    participant US as UserService
    participant UD as UserDAO
    participant H as HikariCP
    participant DB as H2 Database
    B->>F: POST /api/auth/login {email, password}
    F->>S: doPost (add security headers)
    S->>US: authenticateUser(email, password)
    US->>UD: findByEmail(email)
    UD->>H: getConnection()
    H->>DB: SELECT * FROM users WHERE email = ?
    DB-->>H: user row
    H-->>UD: connection (closed)
    UD-->>US: Optional(User)
    US->>US: BCrypt.checkpw(password, hash)
    US-->>S: Optional(User)
    S->>S: invalidate old session, create new session, setMaxInactiveInterval(30m)
    S-->>B: 200 {status, userId, role}
```

## 2. Checkout (F4 → F5)

```mermaid
sequenceDiagram
    participant B as Browser (cart.jsp / checkout.jsp)
    participant F as SecurityHeadersFilter
    participant CS as CartServlet / OrderServlet
    participant OS as OrderService
    participant PD as ProductDAO
    participant OD as OrderDAO
    participant H as HikariCP
    participant DB as H2 Database
    B->>F: POST /api/orders/checkout (session cookie)
    F->>CS: doPost (auth check: buyer role)
    CS->>OS: checkoutCart(buyerId)
    OS->>H: getConnection (cart read)
    H->>DB: SELECT cart + product prices
    DB-->>OS: cart items (server-side prices)
    OS->>PD: findById(productId) per item
    PD->>H: getConnection
    H->>DB: SELECT * FROM products WHERE id = ?
    DB-->>OS: product (price, stock)
    OS->>OS: validate qty > 0, stock OK, compute total from DB prices
    OS->>OD: createOrderWithItems(order, items)
    OD->>H: getConnection, setAutoCommit(false)
    H->>DB: BEGIN
    OD->>DB: INSERT INTO orders ...
    OD->>DB: INSERT INTO order_items ... (unit_price from DB)
    OD->>DB: UPDATE products SET stock_qty = stock_qty - ? (guard stock >= qty)
    OD->>DB: DELETE FROM cart_items WHERE user_id = ?
    H->>DB: COMMIT (or ROLLBACK on error)
    OD-->>OS: Order with id
    OS-->>CS: Order
    CS-->>B: 201 Order JSON {id, totalAmount, PENDING}
```

## 3. Review (F8)

```mermaid
sequenceDiagram
    participant B as Browser (product-detail.jsp)
    participant RS as ReviewServlet
    participant RVS as ReviewService
    participant OD as OrderDAO
    participant RD as ReviewDAO
    participant H as HikariCP
    participant DB as H2 Database
    B->>RS: POST /api/reviews {productId, rating, comment}
    RS->>RVS: addReview(userId, productId, rating, comment)
    RVS->>RVS: validate rating 1..5
    RVS->>OD: hasPurchasedProduct(userId, productId)
    OD->>H: getConnection
    H->>DB: SELECT COUNT(*) FROM orders JOIN order_items WHERE buyer + product + status IN (DELIVERED, CONFIRMED, SHIPPED)
    DB-->>RVS: true/false
    RVS->>RD: existsByUserAndProduct (duplicate check)
    RD->>H: getConnection
    H->>DB: SELECT COUNT(*) FROM reviews WHERE user + product
    DB-->>RVS: false (ok)
    RVS->>RD: create(review)
    RD->>H: getConnection
    H->>DB: INSERT INTO reviews ...
    DB-->>B: 201 Review JSON
```
