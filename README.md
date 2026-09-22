# ARTHY MART

ArthyMart is a multi-seller e-commerce marketplace web application built with Java Servlets, JDBC, Apache Tomcat 9, and H2 Database for Anna University R2025 Semester 3 Capstone.

---

## 1. Features (F1–F8)

- **F1 — Authentication & Roles:**
  - Buyer and Seller self-registration
  - Login & Logout
  - Seeded Admin account (public Admin registration strictly blocked)
  - Password hashing with jBCrypt (cost factor 12 for registration, 10 for seeds)
  - `HttpSession` management with session fixation protection (old session invalidated and regenerated on login)
  - Explicit 30-minute session timeout and HttpOnly cookies
  - `AuthFilter` enforcing session authentication and role-based authorization

- **F2 — Seller Product Management:**
  - Create new product listings with complete validation (name, description, price, stock, category, image URL)
  - Edit existing listings owned by the seller
  - Delete own listings
  - Seller dashboard listing view (`/seller/dashboard.jsp`)

- **F3 — Buyer Product Browsing & Discovery:**
  - Browse all products with category badges and stock indicators
  - Keyword search across name and description
  - Category filtering
  - Combined search and filter queries
  - Product details view with review history
  - Friendly empty-result handling with quick-reset

- **F4 — Shopping Cart:**
  - Add items to cart with requested quantity
  - Update quantity (+ / -) with automatic stock validation
  - Remove individual items or clear cart
  - Dynamic running total computed server-side
  - Empty cart UX handling with checkout guard

- **F5 — Checkout & Mock Payment:**
  - Single-click mock payment confirmation (always approved in MVP)
  - Atomic database transaction across 4 operations:
    1. Create order record (`orders`)
    2. Insert order items (`order_items`) with server-enforced price
    3. Decrement product inventory (`stock_qty >= requested`)
    4. Clear buyer's cart (`cart_items`)
  - Prevention of empty-cart checkout and out-of-stock purchase

- **F6 — Orders:**
  - **Buyer:** Order history view with total amount, status, date, and items list
  - **Seller:** Incoming order view for orders containing the seller's products
  - Order status workflow: `PENDING → CONFIRMED → SHIPPED → DELIVERED → CANCELLED`
  - Strict authorization: buyers cannot view orders belonging to other buyers

- **F7 — Admin Panel:**
  - View all users (with `passwordHash` strictly omitted via `UserResponseDTO`)
  - View all orders across all buyers and sellers
  - View all active listings
  - Moderate/delete inappropriate listings and accounts
  - Admin-only authorization via `AuthFilter` and `/api/admin/*`

- **F8 — Reviews & Ratings:**
  - 1–5 star ratings on completed/delivered orders
  - Purchase verification: only buyers who actually purchased the product can review
  - One review per user per product constraint
  - Rating validation (1 to 5) and XSS-safe output rendering

---

## 2. Tech Stack

| Layer | Technologies |
|---|---|
| **Language & Runtime** | Java 17 (JDK 17 LTS), Apache Tomcat 9.0.x |
| **Web Tier** | Servlet API 4.0.1 (`javax.servlet.*`), JSP 2.3, JSTL 1.2, Vanilla JavaScript (`fetch`), CSS3 |
| **Data & Persistence** | H2 Database 2.2.224, HikariCP 5.1.0, JDBC (PreparedStatement only) |
| **Security & Utilities** | jBCrypt 0.4, Gson 2.10.1, SLF4J 2.0.9 + Logback 1.4.14 |
| **Build & Quality** | Apache Maven 3.8+, Maven Checkstyle Plugin 3.3.1, SpotBugs Maven Plugin 4.8.3.1 |
| **CI / CD** | GitHub Actions (`.github/workflows/build.yml`) on Ubuntu with Temurin JDK 17 |
| **Testing** | JUnit 5.10.1, Mockito 5.8.0, Embedded in-memory H2 |

---

## 3. Architecture

```
Browser Client (JSP + Vanilla JS fetch)
   │
   ▼
[EncodingFilter] (UTF-8 request & response)
   │
   ▼
[SecurityHeadersFilter] (X-Content-Type-Options: nosniff, X-Frame-Options: DENY, Referrer-Policy)
   │
   ▼
[AuthFilter] (Session validation, Role-based access: BUYER / SELLER / ADMIN)
   │
   ▼
Servlets / Front Controllers:
├── AuthServlet (/api/auth/*)
├── ProductServlet (/api/products/*)
├── CartServlet (/api/cart/*)
├── OrderServlet (/api/orders/*)
├── ReviewServlet (/api/reviews/*)
├── AdminServlet (/api/admin/*)
└── HealthServlet (/api/v1/health, /api/health)
   │
   ▼
Service Layer (Validation, Pricing, Authorization rules)
├── UserService
├── ProductService
├── CartService
├── OrderService
└── ReviewService
   │
   ▼
DAO Layer (Pure JDBC with PreparedStatement and try-with-resources)
├── UserDAO
├── ProductDAO
├── CartDAO
├── OrderDAO
└── ReviewDAO
   │
   ▼
HikariCP Connection Pool (Single pool managed by DatabaseConnectionListener)
   │
   ▼
H2 Database (Schema DDL + Seed Data + Transactions)
```

### Architectural Boundaries
- **No SQL in Controllers/Servlets:** All persistence operations go through Services and DAOs.
- **No business logic in DAOs:** DAOs execute parameterized queries and map result sets.
- **No direct DB access from JSP:** JSPs call REST endpoints or render server-provided attributes.
- **DTO Separation:** `UserResponseDTO` never exposes `passwordHash`.
- **PreparedStatement Everywhere:** Zero string concatenation in SQL queries.

---

## 4. Diagrams

The architectural and design diagrams are documented in detail:
- **D1 — ER Diagram:** [docs/ER_DIAGRAM.md](docs/ER_DIAGRAM.md) (All tables, keys, FK indexes, and constraints)
- **D2 — Use Case Diagram:** [docs/USE_CASE_DIAGRAM.md](docs/USE_CASE_DIAGRAM.md) (Guest, Buyer, Seller, Admin use cases & authorization matrix)
- **D3 — Sequence Diagram:** [docs/SEQUENCE_DIAGRAM.md](docs/SEQUENCE_DIAGRAM.md) (Login, Checkout, and Review flows)

---

## 5. Database & Seed Data

### Required Tables
- `users`: User identity, roles (`BUYER`, `SELLER`, `ADMIN`), BCrypt hash, timestamps
- `products`: Product catalog, seller FK, price (`DECIMAL(10,2)`), stock, category, image URL
- `orders`: Order header, buyer FK, status, total amount (`DECIMAL(10,2)`)
- `order_items`: Order line items, product FK, unit price, quantity, timestamp
- `cart_items`: Buyer shopping carts, unique per `(user_id, product_id)`, timestamp
- `reviews`: Product reviews, rating (1–5), unique per `(product_id, user_id)`

### Seed Accounts (Password: `password123`)
| Role | Email | Password |
|---|---|---|
| **ADMIN** | `admin@arthymart.com` | `password123` |
| **SELLER** | `seller@arthymart.com` | `password123` |
| **BUYER** | `buyer@arthymart.com` | `password123` |

---

## 6. Health API

- **Endpoint:** `GET /api/v1/health` (also accessible at `GET /api/health`)
- **Status:** Public (bypasses `AuthFilter`)
- **Verification:** Actively verifies database connectivity via `DatabaseConnectionListener.checkHealth()`.
- **Response (DB Healthy):** `200 OK`
  ```json
  {
    "status": "UP",
    "db": "UP"
  }
  ```
- **Response (DB Unreachable):** `503 Service Unavailable`
  ```json
  {
    "status": "DOWN",
    "db": "DOWN"
  }
  ```

---

## 7. Build, Test & CI

### Prerequisites
- JDK 17
- Apache Maven 3.8+

### Build & Verification Commands
```bash
# Clean, compile, run all 47 tests, execute Checkstyle, run SpotBugs, and package WAR
mvn -B clean verify

# Run unit and integration tests only
mvn test

# Run Checkstyle audit only
mvn checkstyle:check

# Run SpotBugs static analysis only
mvn spotbugs:check
```

### GitHub Actions CI
The workflow file `.github/workflows/build.yml` executes `mvn -B clean verify` on push and pull requests on `ubuntu-latest` with Temurin JDK 17.

---

## 8. Tomcat 9 Deployment

1. Build the WAR artifact:
   ```bash
   mvn clean package
   ```
   Artifact produced: `target/arthymart-1.0-SNAPSHOT.war`
2. Copy `target/arthymart-1.0-SNAPSHOT.war` to `$CATALINA_HOME/webapps/arthymart.war`.
3. Start Tomcat 9:
   - Linux/macOS: `./bin/startup.sh`
   - Windows: `.\bin\startup.bat`
4. Access the application:
   - Home: `http://localhost:8080/arthymart/`
   - Health: `http://localhost:8080/arthymart/api/v1/health`

---

## 9. Live URL & Demo

- **Local Deployment URL:** `http://localhost:8080/arthymart/`
- **Health Check URL:** `http://localhost:8080/arthymart/api/v1/health`
- **Screenshots:** Previews available for Home, Catalog, Seller Dashboard, Cart, Orders, and Admin views.

---

## 10. Known Limitations & Out-of-Scope Items

- **Payment Gateway:** Mock payment is used (transactions auto-approved upon checkout verification).
- **AI Chatbot (Week 9+):** Strictly out of scope for the September 21, 2026 Full Build + Deploy checkpoint and will be implemented in subsequent milestones.
