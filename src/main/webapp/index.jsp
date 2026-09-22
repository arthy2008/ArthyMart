<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Arthy Mart - Your Smart Shopping Destination</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        :root {
            --blue:      #1565c0;
            --blue-dark: #0d47a1;
            --blue-light:#e3f2fd;
            --accent:    #ff6d00;
            --text:      #263238;
            --muted:     #607d8b;
            --white:     #ffffff;
        }

        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background: #f0f4f8;
            color: var(--text);
        }

        /* ── NAVBAR ── */
        .navbar {
            background: linear-gradient(135deg, var(--blue-dark) 0%, var(--blue) 100%);
            padding: 0 32px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: 64px;
            position: sticky;
            top: 0;
            z-index: 100;
            box-shadow: 0 2px 16px rgba(0,0,0,0.25);
        }

        .nav-brand {
            display: flex;
            align-items: center;
            gap: 10px;
            text-decoration: none;
        }

        .nav-logo {
            font-size: 1.6rem;
        }

        .nav-brand-name {
            font-size: 1.3rem;
            font-weight: 900;
            color: #fff;
            letter-spacing: 2px;
        }

        .nav-brand-name span { color: #80d8ff; }

        .nav-links {
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .nav-links a {
            color: rgba(255,255,255,0.85);
            text-decoration: none;
            padding: 7px 14px;
            border-radius: 8px;
            font-size: 0.88rem;
            font-weight: 600;
            transition: background 0.2s, color 0.2s;
        }

        .nav-links a:hover { background: rgba(255,255,255,0.15); color: #fff; }

        .nav-links a.btn-nav {
            background: var(--accent);
            color: #fff;
            margin-left: 4px;
        }

        .nav-links a.btn-nav:hover { background: #e65100; }

        .nav-links a.btn-nav-outline {
            border: 1.5px solid rgba(255,255,255,0.5);
        }

        .nav-links a.btn-nav-outline:hover { background: rgba(255,255,255,0.15); }

        /* ── HERO ── */
        .hero {
            background: linear-gradient(135deg, var(--blue-dark) 0%, #0277bd 60%, #01579b 100%);
            color: #fff;
            padding: 80px 32px;
            text-align: center;
            position: relative;
            overflow: hidden;
        }

        .hero::before {
            content: '';
            position: absolute;
            inset: 0;
            background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.04'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
        }

        .hero-content { position: relative; z-index: 1; max-width: 700px; margin: 0 auto; }

        .hero-badge {
            display: inline-block;
            background: rgba(255,255,255,0.15);
            border: 1px solid rgba(255,255,255,0.3);
            border-radius: 99px;
            padding: 5px 16px;
            font-size: 0.8rem;
            font-weight: 700;
            letter-spacing: 1px;
            margin-bottom: 20px;
        }

        .hero h1 {
            font-size: 3rem;
            font-weight: 900;
            line-height: 1.15;
            margin-bottom: 16px;
        }

        .hero h1 span { color: #80d8ff; }

        .hero p {
            font-size: 1.1rem;
            color: rgba(255,255,255,0.78);
            max-width: 520px;
            margin: 0 auto 32px;
            line-height: 1.7;
        }

        .hero-buttons { display: flex; gap: 14px; justify-content: center; flex-wrap: wrap; }

        .btn-hero {
            display: inline-block;
            padding: 14px 32px;
            border-radius: 10px;
            font-size: 1rem;
            font-weight: 700;
            text-decoration: none;
            transition: transform 0.15s, opacity 0.15s;
        }

        .btn-hero:hover { transform: translateY(-2px); opacity: 0.93; }
        .btn-hero-primary { background: var(--accent); color: #fff; box-shadow: 0 4px 18px rgba(255,109,0,0.45); }
        .btn-hero-outline { background: rgba(255,255,255,0.12); border: 2px solid rgba(255,255,255,0.5); color: #fff; }

        /* ── STATS BAR ── */
        .stats-bar {
            background: #fff;
            display: flex;
            justify-content: center;
            gap: 0;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
        }

        .stat {
            flex: 1;
            max-width: 220px;
            text-align: center;
            padding: 20px 16px;
            border-right: 1px solid #f0f0f0;
        }

        .stat:last-child { border-right: none; }
        .stat-num { font-size: 1.6rem; font-weight: 900; color: var(--blue); }
        .stat-label { font-size: 0.78rem; color: var(--muted); margin-top: 2px; font-weight: 600; letter-spacing: 0.5px; }

        /* ── SECTION TITLE ── */
        .section { padding: 56px 32px; max-width: 1100px; margin: 0 auto; }
        .section-title { text-align: center; margin-bottom: 36px; }
        .section-title h2 { font-size: 1.8rem; font-weight: 800; color: var(--text); }
        .section-title p  { color: var(--muted); margin-top: 6px; font-size: 0.95rem; }

        /* ── FEATURE CARDS ── */
        .features-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
        }

        .feat-card {
            background: #fff;
            border-radius: 16px;
            padding: 28px 20px;
            text-align: center;
            box-shadow: 0 2px 12px rgba(0,0,0,0.06);
            transition: transform 0.25s, box-shadow 0.25s;
        }

        .feat-card:hover { transform: translateY(-6px); box-shadow: 0 12px 28px rgba(0,0,0,0.12); }
        .feat-icon { font-size: 2.4rem; margin-bottom: 12px; }
        .feat-title { font-size: 0.95rem; font-weight: 700; color: var(--text); margin-bottom: 6px; }
        .feat-desc  { font-size: 0.8rem; color: var(--muted); line-height: 1.55; }

        /* ── CATEGORIES ── */
        .cat-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
            gap: 16px;
        }

        .cat-card {
            background: #fff;
            border-radius: 14px;
            padding: 24px 16px;
            text-align: center;
            text-decoration: none;
            color: var(--text);
            box-shadow: 0 2px 10px rgba(0,0,0,0.06);
            border: 2px solid transparent;
            transition: all 0.2s;
        }

        .cat-card:hover {
            border-color: var(--blue);
            transform: translateY(-4px);
            box-shadow: 0 8px 24px rgba(21,101,192,0.15);
        }

        .cat-icon  { font-size: 2.2rem; margin-bottom: 8px; }
        .cat-name  { font-weight: 700; font-size: 0.88rem; }
        .cat-count { font-size: 0.75rem; color: var(--muted); margin-top: 3px; }

        /* ── HOW IT WORKS ── */
        .steps { display: flex; gap: 0; position: relative; }

        .steps::before {
            content: '';
            position: absolute;
            top: 36px;
            left: calc(16.66% + 36px);
            right: calc(16.66% + 36px);
            height: 2px;
            background: linear-gradient(90deg, var(--blue), #80d8ff);
        }

        .step {
            flex: 1;
            text-align: center;
            padding: 0 16px;
            position: relative;
        }

        .step-num {
            width: 72px; height: 72px;
            background: linear-gradient(135deg, var(--blue-dark), var(--blue));
            color: #fff;
            font-size: 1.5rem;
            font-weight: 900;
            border-radius: 50%;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 14px;
            box-shadow: 0 4px 16px rgba(21,101,192,0.35);
            position: relative;
            z-index: 1;
        }

        .step-title { font-weight: 800; font-size: 0.95rem; margin-bottom: 6px; color: var(--text); }
        .step-desc  { font-size: 0.8rem; color: var(--muted); line-height: 1.5; }

        /* ── ROLES SECTION ── */
        .roles-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
        }

        .role-card {
            border-radius: 16px;
            padding: 32px 24px;
            color: #fff;
            position: relative;
            overflow: hidden;
        }

        .role-card::after {
            content: attr(data-icon);
            position: absolute;
            right: -10px;
            bottom: -10px;
            font-size: 5rem;
            opacity: 0.15;
        }

        .role-admin  { background: linear-gradient(135deg, #0d47a1, #1565c0); }
        .role-seller { background: linear-gradient(135deg, #e65100, #ff6d00); }
        .role-buyer  { background: linear-gradient(135deg, #1b5e20, #2e7d32); }

        .role-badge {
            display: inline-block;
            background: rgba(255,255,255,0.2);
            padding: 3px 12px;
            border-radius: 99px;
            font-size: 0.72rem;
            font-weight: 800;
            letter-spacing: 1px;
            margin-bottom: 12px;
        }

        .role-card h3   { font-size: 1.2rem; font-weight: 800; margin-bottom: 12px; }
        .role-card ul   { list-style: none; }
        .role-card li   { font-size: 0.83rem; opacity: 0.85; padding: 3px 0; }
        .role-card li::before { content: '✓ '; font-weight: 700; opacity: 1; }

        .role-login {
            display: inline-block;
            margin-top: 18px;
            background: rgba(255,255,255,0.2);
            border: 1px solid rgba(255,255,255,0.35);
            border-radius: 8px;
            padding: 8px 18px;
            font-size: 0.82rem;
            font-weight: 700;
            color: #fff;
            text-decoration: none;
            transition: background 0.2s;
        }

        .role-login:hover { background: rgba(255,255,255,0.3); }

        /* ── CTA BANNER ── */
        .cta-banner {
            background: linear-gradient(135deg, var(--blue-dark), #0277bd);
            color: #fff;
            text-align: center;
            padding: 64px 32px;
            margin: 0;
        }

        .cta-banner h2 { font-size: 2rem; font-weight: 900; margin-bottom: 10px; }
        .cta-banner p  { opacity: 0.75; margin-bottom: 28px; font-size: 1rem; }

        .btn-cta {
            display: inline-block;
            padding: 14px 36px;
            background: #fff;
            color: var(--blue-dark);
            border-radius: 10px;
            font-weight: 800;
            font-size: 1rem;
            text-decoration: none;
            box-shadow: 0 4px 20px rgba(0,0,0,0.2);
            transition: transform 0.15s;
        }

        .btn-cta:hover { transform: translateY(-2px); }

        /* ── FOOTER ── */
        footer {
            background: #1a1a2e;
            color: rgba(255,255,255,0.5);
            text-align: center;
            padding: 24px 32px;
            font-size: 0.82rem;
        }

        footer span { color: #80d8ff; font-weight: 700; }

        /* ── RESPONSIVE ── */
        @media (max-width: 768px) {
            .hero h1 { font-size: 2rem; }
            .roles-grid { grid-template-columns: 1fr; }
            .steps { flex-direction: column; }
            .steps::before { display: none; }
            .stats-bar { flex-wrap: wrap; }
            .nav-links a:not(.btn-nav):not(.btn-nav-outline) { display: none; }
        }
    </style>
</head>
<body>

<!-- ══ NAVBAR ══ -->
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="nav-brand">
        <span class="nav-logo">🛒</span>
        <span class="nav-brand-name">ARTHY <span>MART</span></span>
    </a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
        <a href="${pageContext.request.contextPath}/products.jsp">Products</a>
        <a href="${pageContext.request.contextPath}/cart.jsp">🛒 Cart</a>
        <a href="${pageContext.request.contextPath}/orders.jsp">My Orders</a>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/api/auth/logout" class="btn-nav-outline">Logout</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-nav-outline">Login</a>
                <a href="${pageContext.request.contextPath}/register.jsp" class="btn-nav">Register</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>

<!-- ══ HERO ══ -->
<section class="hero">
    <div class="hero-content">
        <div class="hero-badge">🎉 Welcome to Arthy Mart</div>
        <h1>Shop Smart,<br>Live <span>Better</span></h1>
        <p>Discover thousands of products from trusted sellers. Fast delivery, secure payments, and an amazing shopping experience — all in one place.</p>
        <div class="hero-buttons">
            <a href="${pageContext.request.contextPath}/products.jsp" class="btn-hero btn-hero-primary">🛍️ Shop Now</a>
            <a href="${pageContext.request.contextPath}/register.jsp" class="btn-hero btn-hero-outline">Create Account</a>
        </div>
    </div>
</section>

<!-- ══ STATS ══ -->
<div class="stats-bar">
    <div class="stat">
        <div class="stat-num">1,000+</div>
        <div class="stat-label">Products Listed</div>
    </div>
    <div class="stat">
        <div class="stat-num">500+</div>
        <div class="stat-label">Happy Buyers</div>
    </div>
    <div class="stat">
        <div class="stat-num">100+</div>
        <div class="stat-label">Verified Sellers</div>
    </div>
    <div class="stat">
        <div class="stat-num">4.8 ⭐</div>
        <div class="stat-label">Average Rating</div>
    </div>
</div>

<!-- ══ FEATURES ══ -->
<div class="section">
    <div class="section-title">
        <h2>Why Choose Arthy Mart?</h2>
        <p>Everything you need for a great shopping experience</p>
    </div>
    <div class="features-grid">
        <div class="feat-card">
            <div class="feat-icon">📦</div>
            <div class="feat-title">Fast Delivery</div>
            <div class="feat-desc">Get your orders delivered quickly to your doorstep</div>
        </div>
        <div class="feat-card">
            <div class="feat-icon">🔒</div>
            <div class="feat-title">Secure Payments</div>
            <div class="feat-desc">Your transactions are always safe and encrypted</div>
        </div>
        <div class="feat-card">
            <div class="feat-icon">⭐</div>
            <div class="feat-title">Verified Reviews</div>
            <div class="feat-desc">Only buyers who purchased can leave reviews</div>
        </div>
        <div class="feat-card">
            <div class="feat-icon">🏷️</div>
            <div class="feat-title">Best Deals</div>
            <div class="feat-desc">Find the best prices from multiple sellers</div>
        </div>
        <div class="feat-card">
            <div class="feat-icon">🔄</div>
            <div class="feat-title">Easy Returns</div>
            <div class="feat-desc">Hassle-free return and refund process</div>
        </div>
        <div class="feat-card">
            <div class="feat-icon">💬</div>
            <div class="feat-title">24/7 Support</div>
            <div class="feat-desc">We're here to help you anytime you need</div>
        </div>
    </div>
</div>

<!-- ══ CATEGORIES ══ -->
<div style="background:#fff; padding: 20px 0;">
<div class="section" style="padding-top: 40px; padding-bottom: 40px;">
    <div class="section-title">
        <h2>Shop by Category</h2>
        <p>Find exactly what you're looking for</p>
    </div>
    <div class="cat-grid">
        <a href="${pageContext.request.contextPath}/products.jsp?category=Electronics" class="cat-card">
            <div class="cat-icon">📱</div>
            <div class="cat-name">Electronics</div>
            <div class="cat-count">Phones, Laptops & more</div>
        </a>
        <a href="${pageContext.request.contextPath}/products.jsp?category=Books" class="cat-card">
            <div class="cat-icon">📚</div>
            <div class="cat-name">Books</div>
            <div class="cat-count">Fiction, Education & more</div>
        </a>
        <a href="${pageContext.request.contextPath}/products.jsp?category=Home+%26+Kitchen" class="cat-card">
            <div class="cat-icon">🏠</div>
            <div class="cat-name">Home & Kitchen</div>
            <div class="cat-count">Appliances, Decor & more</div>
        </a>
        <a href="${pageContext.request.contextPath}/products.jsp?category=Clothing" class="cat-card">
            <div class="cat-icon">👕</div>
            <div class="cat-name">Clothing</div>
            <div class="cat-count">Men, Women & Kids</div>
        </a>
        <a href="${pageContext.request.contextPath}/products.jsp?category=Sports" class="cat-card">
            <div class="cat-icon">⚽</div>
            <div class="cat-name">Sports</div>
            <div class="cat-count">Equipment & Accessories</div>
        </a>
        <a href="${pageContext.request.contextPath}/products.jsp" class="cat-card">
            <div class="cat-icon">🛍️</div>
            <div class="cat-name">All Products</div>
            <div class="cat-count">Browse everything</div>
        </a>
    </div>
</div>
</div>

<!-- ══ HOW IT WORKS ══ -->
<div class="section">
    <div class="section-title">
        <h2>How It Works</h2>
        <p>Start shopping in just 3 simple steps</p>
    </div>
    <div class="steps">
        <div class="step">
            <div class="step-num">1</div>
            <div class="step-title">Create Account</div>
            <div class="step-desc">Register as a Buyer or Seller in under a minute</div>
        </div>
        <div class="step">
            <div class="step-num">2</div>
            <div class="step-title">Browse & Add to Cart</div>
            <div class="step-desc">Find products, filter by category, and add to your cart</div>
        </div>
        <div class="step">
            <div class="step-num">3</div>
            <div class="step-title">Checkout & Enjoy</div>
            <div class="step-desc">Secure checkout and track your order status</div>
        </div>
    </div>
</div>

<!-- ══ ROLES ══ -->
<div style="background: #f0f4f8; padding: 20px 0;">
<div class="section" style="padding-top: 40px; padding-bottom: 40px;">
    <div class="section-title">
        <h2>Choose Your Role</h2>
        <p>Arthy Mart supports three user roles with different capabilities</p>
    </div>
    <div class="roles-grid">
        <div class="role-card role-admin" data-icon="⚙️">
            <div class="role-badge">ADMIN</div>
            <h3>🛡️ Administrator</h3>
            <ul>
                <li>Manage all users</li>
                <li>Moderate product listings</li>
                <li>View all orders</li>
                <li>Full platform control</li>
            </ul>
            <a href="${pageContext.request.contextPath}/login.jsp" class="role-login">Login as Admin →</a>
        </div>
        <div class="role-card role-seller" data-icon="🏪">
            <div class="role-badge">SELLER</div>
            <h3>🏪 Seller</h3>
            <ul>
                <li>List & manage products</li>
                <li>Set prices and stock</li>
                <li>View your orders</li>
                <li>Grow your business</li>
            </ul>
            <a href="${pageContext.request.contextPath}/login.jsp" class="role-login">Login as Seller →</a>
        </div>
        <div class="role-card role-buyer" data-icon="🛒">
            <div class="role-badge">BUYER</div>
            <h3>🛒 Buyer</h3>
            <ul>
                <li>Browse all products</li>
                <li>Add to cart & checkout</li>
                <li>Track your orders</li>
                <li>Leave verified reviews</li>
            </ul>
            <a href="${pageContext.request.contextPath}/login.jsp" class="role-login">Login as Buyer →</a>
        </div>
    </div>
</div>
</div>

<!-- ══ CTA BANNER ══ -->
<div class="cta-banner">
    <h2>Ready to Start Shopping? 🛍️</h2>
    <p>Join thousands of happy customers on Arthy Mart today</p>
    <a href="${pageContext.request.contextPath}/register.jsp" class="btn-cta">Create Your Free Account</a>
</div>

<!-- ══ FOOTER ══ -->
<footer>
    <p>© 2026 <span>Arthy Mart</span> · Built with Java Servlets, JDBC, Tomcat 9 & H2 · All rights reserved</p>
</footer>

</body>
</html>
