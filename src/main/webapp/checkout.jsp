<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secure Checkout - Arthy Mart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .checkout-layout {
            display: grid;
            grid-template-columns: 1fr 360px;
            gap: 28px;
            align-items: start;
        }
        .section-box {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            padding: 24px;
            margin-bottom: 20px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.03);
        }
        .box-title {
            font-size: 1.15rem;
            font-weight: 800;
            color: #0f172a;
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .payment-option {
            border: 2px solid #1565c0;
            background: #eff6ff;
            border-radius: 10px;
            padding: 16px;
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 12px;
        }
        .summary-box {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            padding: 24px;
            box-shadow: 0 4px 14px rgba(0,0,0,0.04);
            position: sticky;
            top: 84px;
        }
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 12px;
            font-size: 0.9rem;
            color: #475569;
        }
        .summary-row.total {
            font-size: 1.3rem;
            font-weight: 900;
            color: #0f172a;
            border-top: 2px solid #f1f5f9;
            padding-top: 14px;
            margin-top: 14px;
        }
        .btn-confirm-order {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #1565c0, #0288d1);
            color: #fff;
            border: none;
            border-radius: 9px;
            font-size: 1rem;
            font-weight: 800;
            cursor: pointer;
            margin-top: 20px;
            box-shadow: 0 4px 16px rgba(21,101,192,0.35);
            transition: opacity 0.2s;
        }
        .btn-confirm-order:hover { opacity: 0.93; }
        .btn-confirm-order:disabled { background: #94a3b8; cursor: not-allowed; }
        .security-badge {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-top: 16px;
            color: #16a34a;
            font-size: 0.8rem;
            font-weight: 700;
            justify-content: center;
        }

        @media (max-width: 860px) {
            .checkout-layout { grid-template-columns: 1fr; }
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
        <a href="${pageContext.request.contextPath}/cart.jsp">🛒 Cart</a>
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
        <h1 style="font-size:1.8rem; font-weight:800; color:#0f172a;">Order Checkout 💳</h1>
        <p style="color:#64748b; font-size:0.9rem;">Review shipping details and confirm payment to place your order.</p>
    </div>

    <div class="checkout-layout">
        <div>
            <!-- Shipping Info -->
            <div class="section-box">
                <div class="box-title">📍 1. Delivery Information</div>
                <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
                    <div>
                        <label>Recipient Name</label>
                        <input type="text" value="${sessionScope.user.name}" readonly style="background:#f8fafc;">
                    </div>
                    <div>
                        <label>Email Address</label>
                        <input type="email" value="${sessionScope.user.email}" readonly style="background:#f8fafc;">
                    </div>
                </div>
                <div style="margin-top:12px;">
                    <label>Shipping Address</label>
                    <input type="text" value="123 Academic Way, Student Campus" readonly style="background:#f8fafc;">
                </div>
            </div>

            <!-- Payment Method -->
            <div class="section-box">
                <div class="box-title">💳 2. Payment Method</div>
                <div class="payment-option">
                    <input type="radio" checked id="mockPay" name="payMethod">
                    <label for="mockPay" style="margin:0; font-weight:700; cursor:pointer;">
                        Mock Payment Gateway (Instant Approval)
                        <span style="display:block; font-size:0.78rem; color:#64748b; font-weight:normal; margin-top:2px;">
                            College Capstone Demo: Safe simulation of bank authorization without real charges.
                        </span>
                    </label>
                </div>
            </div>
        </div>

        <!-- Summary -->
        <div class="summary-box">
            <h3 style="font-size:1.2rem; font-weight:800; margin-bottom:16px;">Order Summary</h3>
            <div id="checkoutItems" style="margin-bottom:14px; border-bottom:1px solid #f1f5f9; padding-bottom:10px;">
                <p style="color:#64748b; font-size:0.85rem;">Loading cart items...</p>
            </div>
            <div class="summary-row">
                <span>Items Total</span>
                <span id="subtotalVal">$0.00</span>
            </div>
            <div class="summary-row">
                <span>Shipping Fee</span>
                <span style="color:#16a34a; font-weight:700;">FREE</span>
            </div>
            <div class="summary-row total">
                <span>Total Amount</span>
                <span id="grandTotalVal">$0.00</span>
            </div>

            <button id="payBtn" class="btn-confirm-order">Confirm &amp; Place Order</button>
            <div id="orderResult" style="margin-top:12px; font-weight:700; font-size:0.88rem; text-align:center;"></div>

            <div class="security-badge">
                <span>🔒</span> Safe 256-bit Encrypted Transaction
            </div>
        </div>
    </div>
</div>

<footer>
    <p>&copy; 2026 <span>Arthy Mart</span> &middot; Full Stack E-Commerce Platform</p>
</footer>

<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>
const contextPath = '${pageContext.request.contextPath}';

async function loadCheckoutData() {
    try {
        const res = await fetch(contextPath + '/api/cart');
        if (res.status === 401) {
            window.location.href = contextPath + '/login.jsp';
            return;
        }
        const data = await res.json();
        const items = data.items || [];
        const total = data.total || 0;

        const itemsBox = document.getElementById('checkoutItems');
        if (items.length === 0) {
            itemsBox.innerHTML = '<p style="color:#dc2626;">Your cart is empty. Please add items before checking out.</p>';
            document.getElementById('payBtn').disabled = true;
            return;
        }

        let html = '';
        items.forEach(it => {
            const name = it.productName || ('Item #' + it.productId);
            const lineTotal = (it.unitPrice * it.quantity).toFixed(2);
            html +=
                '<div style="display:flex; justify-content:space-between; font-size:0.85rem; margin-bottom:8px; color:#334155;">' +
                    '<span>' + name + ' &times; ' + it.quantity + '</span>' +
                    '<span style="font-weight:700;">$' + lineTotal + '</span>' +
                '</div>';
        });
        itemsBox.innerHTML = html;
        document.getElementById('subtotalVal').textContent = '$' + Number(total).toFixed(2);
        document.getElementById('grandTotalVal').textContent = '$' + Number(total).toFixed(2);

    } catch (e) {
        document.getElementById('checkoutItems').innerHTML = '<p style="color:#dc2626;">Error loading items.</p>';
    }
}

document.getElementById('payBtn').addEventListener('click', async () => {
    const btn = document.getElementById('payBtn');
    const resultBox = document.getElementById('orderResult');
    btn.disabled = true;
    btn.textContent = 'Processing payment...';
    resultBox.textContent = '';

    try {
        const res = await fetch(contextPath + '/api/orders/checkout', { method: 'POST' });
        const data = await res.json();

        if (res.ok) {
            resultBox.textContent = '✅ Order #' + data.id + ' placed successfully! Total: $' + data.totalAmount;
            resultBox.style.color = '#16a34a';
            btn.textContent = '✅ Order Placed!';
            setTimeout(() => {
                window.location.href = contextPath + '/orders.jsp';
            }, 1200);
        } else {
            resultBox.textContent = '❌ ' + (data.message || 'Checkout failed. Please check stock availability.');
            resultBox.style.color = '#dc2626';
            btn.disabled = false;
            btn.textContent = 'Confirm & Place Order';
        }
    } catch (err) {
        resultBox.textContent = '⚠️ Network error during checkout.';
        resultBox.style.color = '#dc2626';
        btn.disabled = false;
        btn.textContent = 'Confirm & Place Order';
    }
});

loadCheckoutData();
</script>
</body>
</html>
