<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart - Arthy Mart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .cart-layout {
            display: grid;
            grid-template-columns: 1fr 340px;
            gap: 28px;
            align-items: start;
        }
        .cart-table-card {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            overflow: hidden;
            box-shadow: 0 4px 14px rgba(0,0,0,0.04);
        }
        .cart-item-row {
            display: flex;
            align-items: center;
            padding: 18px 20px;
            border-bottom: 1px solid #f1f5f9;
            gap: 16px;
        }
        .cart-item-row:last-child { border-bottom: none; }
        .cart-item-img {
            width: 70px;
            height: 70px;
            border-radius: 8px;
            object-fit: cover;
            background: #f8fafc;
        }
        .cart-item-info {
            flex: 1;
            min-width: 0;
        }
        .cart-item-title {
            font-size: 0.95rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 4px;
        }
        .cart-item-price {
            font-size: 0.85rem;
            color: #64748b;
        }
        .cart-qty-ctrl {
            display: flex;
            align-items: center;
            border: 1.5px solid #cbd5e1;
            border-radius: 8px;
            overflow: hidden;
        }
        .cart-qty-btn {
            background: #f8fafc;
            border: none;
            width: 32px;
            height: 32px;
            font-size: 1rem;
            font-weight: 700;
            cursor: pointer;
            transition: background 0.15s;
        }
        .cart-qty-btn:hover { background: #e2e8f0; }
        .cart-qty-val {
            width: 36px;
            text-align: center;
            font-size: 0.88rem;
            font-weight: 700;
        }
        .cart-item-total {
            font-size: 1.05rem;
            font-weight: 800;
            color: #0f172a;
            min-width: 80px;
            text-align: right;
        }
        .cart-remove-btn {
            background: none;
            border: none;
            color: #94a3b8;
            font-size: 1.1rem;
            cursor: pointer;
            padding: 4px;
            transition: color 0.15s;
        }
        .cart-remove-btn:hover { color: #dc2626; }

        .summary-card {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            padding: 24px;
            box-shadow: 0 4px 14px rgba(0,0,0,0.04);
            position: sticky;
            top: 84px;
        }
        .summary-title {
            font-size: 1.2rem;
            font-weight: 800;
            color: #0f172a;
            margin-bottom: 18px;
            padding-bottom: 12px;
            border-bottom: 2px solid #f1f5f9;
        }
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 12px;
            font-size: 0.9rem;
            color: #475569;
        }
        .summary-row.total {
            font-size: 1.25rem;
            font-weight: 900;
            color: #0f172a;
            margin-top: 16px;
            padding-top: 14px;
            border-top: 2px solid #f1f5f9;
        }
        .btn-checkout-now {
            width: 100%;
            padding: 13px;
            background: #1565c0;
            color: #fff;
            border: none;
            border-radius: 9px;
            font-size: 1rem;
            font-weight: 700;
            cursor: pointer;
            text-align: center;
            text-decoration: none;
            display: block;
            margin-top: 18px;
            box-shadow: 0 4px 14px rgba(21,101,192,0.3);
            transition: background 0.2s;
        }
        .btn-checkout-now:hover { background: #0d47a1; }

        @media (max-width: 860px) {
            .cart-layout { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/" class="nav-brand">
        <span class="nav-logo">🛒</span>
        <span class="nav-brand-name">ARTHY <span>MART</span></span>
    </a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
        <a href="${pageContext.request.contextPath}/products.jsp">Products</a>
        <a href="${pageContext.request.contextPath}/wishlist.jsp">❤️ Wishlist</a>
        <a href="${pageContext.request.contextPath}/cart.jsp" style="color:#80d8ff;">🛒 Cart</a>
        <a href="${pageContext.request.contextPath}/orders.jsp">Orders</a>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/api/auth/logout" class="btn-nav-outline">Logout</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-nav-outline">Login</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>

<div class="container">
    <div style="margin-bottom:24px;">
        <h1 style="font-size:1.8rem; font-weight:800; color:#0f172a;">Your Shopping Cart 🛒</h1>
        <p style="color:#64748b; font-size:0.9rem;">Review your items before proceeding to secure checkout.</p>
    </div>

    <div id="cartContent">
        <p style="color:#64748b;">Loading cart items...</p>
    </div>
</div>

<footer>
    <p>&copy; 2026 <span>Arthy Mart</span> &middot; Full Stack E-Commerce Platform</p>
</footer>

<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>
const contextPath = '${pageContext.request.contextPath}';

function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

async function load() {
    const content = document.getElementById('cartContent');
    try {
        const res = await fetch(contextPath + '/api/cart', { credentials: 'same-origin' });
        if (res.status === 401) {
            content.innerHTML =
                '<div style="text-align:center; padding:60px 20px; background:#fff; border-radius:14px; border:1px solid #e2e8f0;">' +
                    '<span style="font-size:3rem; display:block; margin-bottom:12px;">🔒</span>' +
                    '<h3>Login Required</h3>' +
                    '<p style="color:#64748b; margin:8px 0 20px;">Please sign in to view and manage your shopping cart.</p>' +
                    '<a href="' + contextPath + '/login.jsp" class="btn">Sign In Now</a>' +
                '</div>';
            return;
        }
        const data = await res.json();
        const items = data.items || [];
        const total = data.total || 0;

        if (items.length === 0) {
            content.innerHTML =
                '<div style="text-align:center; padding:60px 20px; background:#fff; border-radius:14px; border:1px dashed #cbd5e1;">' +
                    '<span style="font-size:3rem; display:block; margin-bottom:12px;">🛒</span>' +
                    '<h3>Your cart is empty</h3>' +
                    '<p style="color:#64748b; margin:8px 0 20px;">Discover exciting products in our catalog.</p>' +
                    '<a href="' + contextPath + '/products.jsp" class="btn">Start Shopping</a>' +
                '</div>';
            return;
        }

        let itemsHtml = '';
        items.forEach(it => {
            const img = it.imageUrl ? it.imageUrl : 'https://placehold.co/100x100?text=Product';
            const safeName = escapeHtml(it.productName || 'Product #' + it.productId);
            const lineTotal = (it.unitPrice * it.quantity).toFixed(2);
            itemsHtml +=
                '<div class="cart-item-row">' +
                    '<img src="' + img + '" class="cart-item-img" alt="' + safeName + '">' +
                    '<div class="cart-item-info">' +
                        '<div class="cart-item-title">' + safeName + '</div>' +
                        '<div class="cart-item-price">$' + it.unitPrice + ' per unit</div>' +
                    '</div>' +
                    '<div class="cart-qty-ctrl">' +
                        '<button class="cart-qty-btn" onclick="updateQty(' + it.productId + ', ' + (it.quantity - 1) + ')">-</button>' +
                        '<span class="cart-qty-val">' + it.quantity + '</span>' +
                        '<button class="cart-qty-btn" onclick="updateQty(' + it.productId + ', ' + (it.quantity + 1) + ')">+</button>' +
                    '</div>' +
                    '<div class="cart-item-total">$' + lineTotal + '</div>' +
                    '<button class="cart-remove-btn" onclick="removeItem(' + it.productId + ')" title="Remove item">&times;</button>' +
                '</div>';
        });

        content.innerHTML =
            '<div class="cart-layout">' +
                '<div class="cart-table-card">' +
                    '<div style="padding:16px 20px; background:#f8fafc; font-weight:700; color:#475569; font-size:0.85rem; border-bottom:1px solid #e2e8f0; display:flex; justify-content:space-between;">' +
                        '<span>Item Details</span>' +
                        '<button onclick="clearCart()" style="background:none; border:none; color:#dc2626; font-weight:700; cursor:pointer;">Clear Cart</button>' +
                    '</div>' +
                    itemsHtml +
                '</div>' +

                '<div class="summary-card">' +
                    '<div class="summary-title">Order Summary</div>' +
                    '<div class="summary-row">' +
                        '<span>Items Subtotal</span>' +
                        '<span>$' + Number(total).toFixed(2) + '</span>' +
                    '</div>' +
                    '<div class="summary-row">' +
                        '<span>Standard Delivery</span>' +
                        '<span style="color:#16a34a; font-weight:700;">FREE</span>' +
                    '</div>' +
                    '<div class="summary-row total">' +
                        '<span>Grand Total</span>' +
                        '<span>$' + Number(total).toFixed(2) + '</span>' +
                    '</div>' +
                    '<a href="' + contextPath + '/checkout.jsp" class="btn-checkout-now">Proceed to Checkout &rarr;</a>' +
                '</div>' +
            '</div>';

    } catch (e) {
        content.innerHTML = '<p style="color:#dc2626;">Error loading cart details.</p>';
    }
async function updateQty(productId, newQty) {
    if (newQty <= 0) {
        return removeItem(productId);
    }
    try {
        const res = await fetch(contextPath + '/api/cart/' + productId, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId: productId, quantity: newQty })
        });
        if (res.ok) {
            load();
        } else {
            const d = await res.json();
            alert(d.message || 'Cannot update quantity.');
        }
    } catch (e) {
        alert('Network error.');
    }
}

async function removeItem(productId) {
    try {
        const res = await fetch(contextPath + '/api/cart/' + productId, { method: 'DELETE' });
        if (res.ok) load();
    } catch (e) {
        alert('Network error.');
    }
}

async function clearCart() {
    if (!confirm('Are you sure you want to clear your shopping cart?')) return;
    try {
        const res = await fetch(contextPath + '/api/cart', { method: 'DELETE' });
        if (res.ok) load();
    } catch (e) {
        alert('Network error.');
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

load();
</script>
</body>
</html>
