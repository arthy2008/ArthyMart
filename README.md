# ARTHY MART

**ArthyMart** is a full-stack multi-vendor e-commerce web application built with **Java 17, Maven, Java Servlets (javax.servlet), JSP/JSTL, Apache Tomcat 9, H2 Database (HikariCP connection pool), and jBCrypt**.

Designed and implemented for Anna University R2025 Semester 3 Capstone Evaluation and Final Demonstration.

---

## 1. Features Overview

### Mandatory Core Features (F1–F8)
- **F1 — Authentication & Role-Based Access Control:**
  - Buyer and Seller self-registration with instant validation.
  - Secure login with jBCrypt password hashing (cost factor 10).
  - Seeded Admin account (`admin@arthymart.com` / `password123`).
  - Session fixation protection, 30-minute timeout, and HttpOnly cookies.
  - `AuthFilter` enforcing strict RBAC (`BUYER`, `SELLER`, `ADMIN`).
- **F2 — Seller Product Management:**
  - Create, edit, and delete product listings.
  - Manage price, stock, category, description, and image URL.
  - Isolated seller listings (`/seller/dashboard.jsp` via `?mine=true`).
- **F3 — Buyer Product Browsing & Discovery:**
  - Catalog browsing with category pills (`Electronics`, `Books`, `Home & Kitchen`, `Clothing`, `Sports`).
  - Keyword search across product name and description.
  - Client-side sorting (Price Low-to-High, Price High-to-Low, Name A-Z).
  - Stock badges and responsive product cards.
- **F4 — Shopping Cart:**
  - Add to cart, quantity steppers (`+` / `-`), remove items, and clear cart.
  - Server-enforced pricing and live subtotal calculations.
  - Empty cart handling with call-to-action button.
- **F5 — Checkout & Mock Payment:**
  - Transactional order placement with inventory verification.
  - Safe stock decrement and automated cart cleanup.
  - Instant mock payment approval for demo evaluation.
- **F6 — Orders & Visual Order Tracking:**
  - **Buyer View:** Order history with a 5-stage visual progression stepper (`PENDING → CONFIRMED → SHIPPED → DELIVERED → CANCELLED`).
  - **Cancellation:** Buyers can cancel orders while in `PENDING` status.
  - **Seller View:** Incoming order fulfillment with status updates.
- **F7 — Admin Control Center:**
  - Real-time gross platform revenue, user count, order count, and listing count.
  - Role-filtered user account management with deletion safety.
  - All orders inspection and inappropriate listing moderation.
  - Live system health status badge (Database UP / DOWN).
- **F8 — Verified Reviews & Ratings:**
  - 1 to 5 star ratings with written feedback.
  - Verified Purchase rule: only buyers who completed delivery can review.
  - Unique constraint preventing duplicate reviews.

### Value-Added Enhancements
- **🤖 AI Shopping Assistant Chatbot:**
  - Real-time catalog discovery: queries active products, prices, and categories.
  - Handles budget constraints (e.g., "products under $50"), category questions, and store policies (shipping, returns, checkout).
  - Returns clickable product cards and quick suggestion pills.
  - Pluggable external LLM adapter (via `GEMINI_API_KEY` or `AI_API_KEY`), with zero-dependency fallback.
- **❤️ Wishlist:**
  - Save favorite products directly from catalog or details view.
  - Dedicated `/wishlist.jsp` view with one-click "Move to Cart" action.
- **📊 Sales Dashboard & KPIs:**
  - Seller revenue KPIs (Gross Revenue, Total Orders, Active Listings, Pending Fulfillment).
  - Admin platform volume metrics.
- **🎨 Unified Royal Navy Blue UI/UX:**
  - Consistent branding across all JSP views with mobile-responsive design.

---

## 2. Technology Stack

| Layer | Technologies |
|---|---|
| **Runtime & Language** | Java 17 LTS, Apache Tomcat 9.0.x |
| **Web Tier** | Servlet API 4.0.1 (`javax.servlet.*`), JSP 2.3, JSTL 1.2, JavaScript (`fetch` API), CSS3 |
| **Data & Persistence** | H2 Database 2.2.224, HikariCP 5.1.0, JDBC (PreparedStatements exclusively) |
| **Security & Utilities** | jBCrypt 0.4, Google Gson 2.10.1, SLF4J 2.0.9 + Logback 1.4.14 |
| **Build & Quality** | Apache Maven 3.9+, Maven Checkstyle Plugin 3.3.1, SpotBugs 4.8.3.1 |
| **Testing** | JUnit 5.10.1, Mockito 5.8.0, Embedded in-memory H2 test suite |
| **CI / CD** | GitHub Actions (`.github/workflows/build.yml`) |

---

## 3. Database Schema

The database consists of 7 relational tables:
1. `users` (id, name, email [UNIQUE], password_hash, role [BUYER|SELLER|ADMIN], created_at)
2. `products` (id, seller_id [FK], name, description, price, stock_qty, category, image_url, created_at)
3. `orders` (id, buyer_id [FK], status, total_amount, created_at)
4. `order_items` (id, order_id [FK], product_id [FK], quantity, unit_price, created_at)
5. `cart_items` (id, user_id [FK], product_id [FK], quantity, created_at, UNIQUE(user_id, product_id))
6. `wishlist` (id, user_id [FK], product_id [FK], created_at, UNIQUE(user_id, product_id))
7. `reviews` (id, product_id [FK], user_id [FK], rating [1..5], comment, created_at, UNIQUE(product_id, user_id))

All foreign keys enforce `ON DELETE CASCADE`. Indexes are configured on foreign keys and frequently queried fields.

---

## 4. Default Demo Accounts

All default accounts are pre-seeded at application startup:

| Role | Email | Password |
|---|---|---|
| **Administrator** | `admin@arthymart.com` | `password123` |
| **Seller** | `seller@arthymart.com` | `password123` |
| **Buyer** | `buyer@arthymart.com` | `password123` |

---

## 5. Localhost Setup & Execution Guide

### Prerequisites
- JDK 17 installed
- Apache Maven 3.8+ installed
- Apache Tomcat 9.0.x installed

### Step 1: Open in VS Code
Open the project directory in VS Code:
```text
C:\Users\acer\OneDrive\Documents\Default Project\ArthyMart-main
```

### Step 2: Build the WAR File
Open PowerShell in the project root and execute:
```powershell
mvn clean package
```
This produces `target/ArthyMart.war`.

### Step 3: Run on Apache Tomcat 9 (Port 8080)
Copy the WAR file into your Tomcat `webapps` directory:
```powershell
Copy-Item "target\ArthyMart.war" "C:\Program Files\Apache Software Foundation\Tomcat 9.0_Tomcat9_Arthy\webapps\ArthyMart.war" -Force
```
*Note: Apache Tomcat automatically expands the WAR file into `/ArthyMart/`.*

### Step 4: Open in Your Browser
- **Storefront:** [http://localhost:8080/ArthyMart/](http://localhost:8080/ArthyMart/)
- **Product Catalog:** [http://localhost:8080/ArthyMart/products.jsp](http://localhost:8080/ArthyMart/products.jsp)
- **Login:** [http://localhost:8080/ArthyMart/login.jsp](http://localhost:8080/ArthyMart/login.jsp)
- **Seller Center:** [http://localhost:8080/ArthyMart/seller/dashboard.jsp](http://localhost:8080/ArthyMart/seller/dashboard.jsp)
- **Admin Center:** [http://localhost:8080/ArthyMart/admin/dashboard.jsp](http://localhost:8080/ArthyMart/admin/dashboard.jsp)
- **System Health:** [http://localhost:8080/ArthyMart/api/v1/health](http://localhost:8080/ArthyMart/api/v1/health)

### Step 5: Inspect Database via H2 Web Console
1. Open [http://localhost:8080/ArthyMart/h2-console/](http://localhost:8080/ArthyMart/h2-console/)
2. Set **Driver Class:** `org.h2.Driver`
3. Set **JDBC URL:** `jdbc:h2:mem:arthymartdb;DB_CLOSE_DELAY=-1;MODE=MySQL`
4. Set **User Name:** `sa` (Leave Password blank)
5. Click **Connect** to query tables directly.

---

## 6. REST API Reference

| Endpoint | Method | Role | Description |
|---|---|---|---|
| `/api/auth/register` | POST | Public | User self-registration |
| `/api/auth/login` | POST | Public | User login & session creation |
| `/api/auth/logout` | GET/POST | Auth | Session invalidation |
| `/api/chat` | GET/POST | Public | AI Shopping Assistant query |
| `/api/products` | GET | Public | List products / search / filter |
| `/api/products?mine=true` | GET | Seller/Admin | List seller-owned products |
| `/api/products` | POST | Seller/Admin | Create product |
| `/api/products/{id}` | GET/PUT/DELETE | Varies | Single product CRUD |
| `/api/cart` | GET/POST/DELETE | Buyer/Auth | View cart, add item, clear cart |
| `/api/cart/{id}` | PUT/DELETE | Buyer/Auth | Update quantity, remove item |
| `/api/wishlist` | GET/POST/DELETE | Buyer/Auth | View wishlist, add item, clear |
| `/api/wishlist/{id}` | DELETE | Buyer/Auth | Remove specific wishlisted product |
| `/api/orders` | GET | Buyer/Auth | Get buyer order history |
| `/api/orders?view=seller` | GET | Seller/Admin | Get seller incoming orders |
| `/api/orders?view=seller-stats` | GET | Seller/Admin | Get seller revenue KPIs |
| `/api/orders/checkout` | POST | Buyer/Auth | Transactional checkout cart |
| `/api/orders/{id}` | GET/PUT | Buyer/Seller | View order / update fulfillment status |
| `/api/reviews` | GET/POST | Public / Buyer | View reviews / submit verified review |
| `/api/admin/stats` | GET | Admin | Platform volume KPIs |
| `/api/admin/users` | GET/DELETE | Admin | Manage user accounts |
| `/api/admin/orders` | GET | Admin | View all platform orders |
| `/api/admin/products` | GET/DELETE | Admin | Moderate product listings |
| `/api/v1/health` | GET | Public | Database and service health check |

---

## 7. College Final Viva Demonstration Checklist

1. **Architecture & Startup:** Show running app on `http://localhost:8080/ArthyMart/` and verified health check `/api/v1/health`.
2. **AI Shopping Chatbot:** Click floating assistant bubble, ask *"show me products under 50"*, demonstrate clickable product recommendation card.
3. **Buyer Journey:**
   - Sign in as `buyer@arthymart.com` / `password123`.
   - Browse products, filter by category, sort by price.
   - Save item to Wishlist (`❤️`), visit `/wishlist.jsp`, move to cart.
   - Adjust cart quantities, proceed to `/checkout.jsp`, confirm order.
   - Open `/orders.jsp`, verify order shows in 5-step visual tracking timeline.
4. **Seller Journey:**
   - Sign in as `seller@arthymart.com` / `password123`.
   - Open `/seller/dashboard.jsp`, observe real-time Revenue and Order KPIs.
   - Add a new product listing, see it appear in table.
   - Open Orders fulfillment, update status to `SHIPPED` / `DELIVERED`.
5. **Verified Review Verification:**
   - Return to Buyer account, open delivered product details, submit review with 5-star rating.
   - Demonstrate that non-purchased products prevent review submission.
6. **Administrator Control:**
   - Sign in as `admin@arthymart.com` / `password123`.
   - Open `/admin/dashboard.jsp`, inspect gross volume stats, user roles table, and listing moderation.
7. **Database Inspection:** Open `/h2-console/`, run `SELECT * FROM orders;` to demonstrate relational integrity.
8. **Automated Testing:** Run `mvn test` in terminal to prove 100% test pass rate.
