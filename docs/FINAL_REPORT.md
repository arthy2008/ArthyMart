# ArthyMart — Final Capstone Project Report

**Degree / Course:** Anna University R2025, Semester 3  
**Project Title:** ArthyMart — Full-Stack Multi-Vendor E-Commerce Platform with AI Assistant  
**Evaluation Window:** Jul 27 – Oct 10, 2026  
**Tech Stack:** Java 17, Java Servlets (javax.servlet), JSP/JSTL, Apache Tomcat 9, H2 Database (HikariCP), jBCrypt, JUnit 5, Mockito.

---

## 1. Executive Summary
ArthyMart is an enterprise-grade multi-vendor e-commerce web application designed to fulfill all functional requirements (F1–F8) and elective deliverables (O1 Wishlist, O4 AI Shopping Assistant) of the Anna University Capstone Specification. It features role-based access control (Buyer, Seller, Admin), catalog management, shopping cart, transactional checkout, visual order lifecycle tracking, verified buyer reviews, and an AI chat assistant with domain-specific catalog queries and fallback handling.

---

## 2. Architecture & Design Patterns

### 2.1 Layered Architecture
- **Presentation Layer:** JSP pages using JSTL with XSS-safe escaping (`fn:escapeXml`), responsive CSS3 grid/flexbox layouts, and modular client-side JavaScript.
- **Controller Layer:** HTTP Servlets (`AuthServlet`, `ProductServlet`, `CartServlet`, `OrderServlet`, `AdminServlet`, `ReviewServlet`, `WishlistServlet`, `ChatbotServlet`) mapped to RESTful `/api/*` endpoints and JSP view routes.
- **Filter Layer:** `EncodingFilter` for UTF-8 encoding; `AuthFilter` for session validation and role-based route protection.
- **Service Layer:** Pure Java business logic (`UserService`, `ProductService`, `CartService`, `OrderService`, `ReviewService`, `WishlistService`, `ChatbotService`) completely decoupled from HTTP request/response contexts.
- **Data Access Layer (DAO):** JDBC DAOs utilizing `PreparedStatement` exclusively with `try-with-resources` to guarantee zero SQL injection and prevent resource leaks.
- **Connection Pool:** HikariCP managed via `DatabaseConnectionListener` (`ServletContextListener`).

### 2.2 Design Patterns Employed
1. **DAO Pattern:** Encapsulates database queries away from services and controllers.
2. **Front Controller / Servlet Dispatcher:** Specialized servlets handling discrete domain routes (`/api/cart/*`, `/api/orders/*`).
3. **Singleton Pattern:** HikariCP connection pool lifecycle across application context.
4. **Data Transfer Object (DTO):** `UserResponseDTO`, `OrderItemDTO`, `OrderResponseDTO`, `WishlistDTO` to prevent exposing sensitive fields like password hashes.
5. **Strategy / Provider Pattern:** Pluggable AI Chatbot provider interface supporting mock local heuristics and external LLM APIs (Gemini).
6. **Builder / Static Factory:** Factory constructors on DTOs (`OrderResponseDTO.fromOrder(...)`).

---

## 3. Architecture Diagrams Reference

- **D1 — Entity-Relationship (ER) Diagram:** [`docs/ER_DIAGRAM.md`](ER_DIAGRAM.md)  
  *Covers `users`, `products`, `orders`, `order_items`, `cart_items`, `reviews`, and `wishlist_items`.*
- **D2 — Use Case Diagram:** [`docs/USE_CASE_DIAGRAM.md`](USE_CASE_DIAGRAM.md)  
  *Details interactions for Buyer, Seller, and Admin actors across all system use cases.*
- **D3 — Sequence Diagram (Place Order Flow):** [`docs/SEQUENCE_DIAGRAM.md`](SEQUENCE_DIAGRAM.md)  
  *Traces transactional checkout, inventory decrement, order item persistence, and cart cleanup.*

---

## 4. Security Implementation Summary
- **Password Security:** Salted hashes generated via `BCrypt.hashpw(password, BCrypt.gensalt(10))`; passwords are never stored or logged in plaintext.
- **Session Security:** Session ID regenerated on login to prevent session fixation attacks; HttpOnly cookies enabled; 30-minute inactivity timeout.
- **SQL Injection Prevention:** 100% of database queries utilize parameterized `PreparedStatement`.
- **Cross-Site Scripting (XSS):** User-supplied text is sanitized client-side (`escapeHtml`) and server-side via JSTL escaping.
- **Role-Based Authorization:** `AuthFilter` enforces strict access:
  - Buyer: Cart, Checkout, Wishlist, Order History.
  - Seller: Listing creation, stock updates, seller fulfillment.
  - Admin: System metrics, account deletion, listing moderation.

---

## 5. Verification & Quality Metrics
- **Automated Tests:** 60 JUnit 5 unit and integration tests passing (`0 failures, 0 errors`).
- **Static Analysis:**
  - Checkstyle: 0 violations.
  - SpotBugs: 0 bug instances.
- **Deployment Status:** Running on Apache Tomcat 9.0 (`http://localhost:8080/ArthyMart/`).
- **Health Check:** `GET /api/health` returns `{"status":"UP","db":"UP"}`.

---

## 6. Known Limitations & Future Enhancements
- Mock payment gateway can be integrated with real payment processor webhooks (Razorpay / Stripe).
- Chatbot can be augmented with vector embeddings for semantic product search.
