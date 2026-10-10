<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Orders - Arthy Mart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .order-card {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            padding: 24px;
            margin-bottom: 24px;
            box-shadow: 0 4px 14px rgba(0,0,0,0.03);
        }
        .order-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-bottom: 16px;
            border-bottom: 1px solid #f1f5f9;
            flex-wrap: wrap;
            gap: 12px;
        }
        .order-meta h3 {
            font-size: 1.1rem;
            font-weight: 800;
            color: #0f172a;
        }
        .order-date {
            font-size: 0.8rem;
            color: #64748b;
            margin-top: 2px;
        }
        .order-amount {
            font-size: 1.3rem;
            font-weight: 900;
            color: #0f172a;
        }
        .order-actions {
            display: flex;
            gap: 10px;
            margin-top: 20px;
            padding-top: 16px;
            border-top: 1px solid #f1f5f9;
            justify-content: flex-end;
            align-items: center;
            flex-wrap: wrap;
        }
        .view-toggle-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            background: #ffffff;
            border-radius: 12px;
            padding: 12px 20px;
            border: 1px solid #e2e8f0;
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
        <a href="${pageContext.request.contextPath}/cart.jsp">🛒 Cart</a>
        <a href="${pageContext.request.contextPath}/orders.jsp" style="color:#80d8ff;">Orders</a>
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
    <div style="margin-bottom:20px;">
        <h1 style="font-size:1.8rem; font-weight:800; color:#0f172a;">Order History &amp; Tracking 📦</h1>
        <p style="color:#64748b; font-size:0.9rem;">Track real-time fulfillment progression from order placement to delivery.</p>
    </div>

    <!-- Toggle for Seller mode if applicable -->
    <c:if test="${sessionScope.user.role == 'SELLER' || sessionScope.user.role == 'ADMIN'}">
        <div class="view-toggle-bar">
            <span style="font-weight:700; font-size:0.9rem; color:#1e293b;">Role View:</span>
            <label style="display:flex; align-items:center; gap:8px; margin:0; cursor:pointer;">
                <input type="checkbox" id="sellerView" style="width:auto;">
                <span>Show incoming seller orders (fulfillment view)</span>
            </label>
        </div>
    </c:if>

    <div id="ordersContainer">
        <p style="color:#64748b;">Loading orders...</p>
    </div>
</div>

<footer>
    <p>&copy; 2026 <span>Arthy Mart</span> &middot; Full Stack E-Commerce Platform</p>
</footer>

<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>
const contextPath = '${pageContext.request.contextPath}';

async function load() {
    const box = document.getElementById('ordersContainer');
    const isSeller = document.getElementById('sellerView') && document.getElementById('sellerView').checked;
    const qs = isSeller ? '?view=seller' : '';

    try {
        const res = await fetch(contextPath + '/api/orders' + qs);
        if (res.status === 401) {
            window.location.href = contextPath + '/login.jsp';
            return;
        }
        const orders = await res.json();
        box.innerHTML = '';

        if (!orders || orders.length === 0) {
            box.innerHTML =
                '<div style="text-align:center; padding:60px 20px; background:#fff; border-radius:14px; border:1px dashed #cbd5e1;">' +
                    '<span style="font-size:3rem; display:block; margin-bottom:12px;">📦</span>' +
                    '<h3>No orders found</h3>' +
                    '<p style="color:#64748b; margin:8px 0 20px;">You haven\'t placed any orders yet.</p>' +
                    '<a href="' + contextPath + '/products.jsp" class="btn">Start Shopping</a>' +
                '</div>';
            return;
        }

        orders.forEach(o => {
            const card = document.createElement('div');
            card.className = 'order-card';
            const status = (o.status || 'PENDING').toUpperCase();
            const badgeClass = 'badge-' + status.toLowerCase();
            const dateStr = o.createdAt || 'Recent';

            // Build Stepper Timeline
            const stepperHtml = buildStepper(status);

            let actionsHtml = '';
            if (isSeller) {
                let optionsHtml = '';
                ['PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED'].forEach(s => {
                    optionsHtml += '<option value="' + s + '" ' + (s === status ? 'selected' : '') + '>' + s + '</option>';
                });
                actionsHtml =
                    '<div style="display:flex; align-items:center; gap:8px;">' +
                        '<span style="font-size:0.85rem; font-weight:700;">Update Status:</span>' +
                        '<select id="sel-' + o.id + '" style="width:160px; padding:6px 10px;">' +
                            optionsHtml +
                        '</select>' +
                        '<button class="btn" style="padding:6px 14px;" onclick="updateOrderStatus(' + o.id + ')">Save</button>' +
                    '</div>';
            } else if (status === 'PENDING') {
                actionsHtml = '<button class="btn btn-danger" style="padding:8px 16px;" onclick="cancelOrder(' + o.id + ')">Cancel Order</button>';
            } else if (status === 'DELIVERED') {
                actionsHtml = '<a href="' + contextPath + '/products.jsp" class="btn btn-outline" style="padding:8px 16px;">Write Review ⭐</a>';
            }

            card.innerHTML =
                '<div class="order-card-header">' +
                    '<div class="order-meta">' +
                        '<h3>Order #' + o.id + '</h3>' +
                        '<div class="order-date">Placed on ' + dateStr + '</div>' +
                    '</div>' +
                    '<div style="text-align:right;">' +
                        '<div class="order-amount">₹' + Number(o.totalAmount || 0).toFixed(2) + '</div>' +
                        '<span class="badge ' + badgeClass + '">' + status + '</span>' +
                    '</div>' +
                '</div>' +
                stepperHtml +
                (actionsHtml ? '<div class="order-actions">' + actionsHtml + '</div>' : '');
            box.appendChild(card);
        });

    } catch (e) {
        box.innerHTML = '<p style="color:#dc2626;">Error loading orders.</p>';
    }
}

function buildStepper(status) {
    if (status === 'CANCELLED') {
        return '<div style="margin:20px 0 10px; padding:12px; background:#fee2e2; border-radius:8px; color:#991b1b; font-size:0.88rem; font-weight:700; text-align:center;">❌ This order was cancelled.</div>';
    }

    const steps = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'];
    const currIdx = steps.indexOf(status);

    let stepsHtml = '';
    steps.forEach((s, idx) => {
        let cls = '';
        if (idx < currIdx) cls = 'completed';
        else if (idx === currIdx) cls = 'active';

        stepsHtml +=
            '<div class="stepper-step " + cls + "">' +
                '<div class="step-circle">' + (idx < currIdx ? '✓' : idx + 1) + '</div>' +
                '<div class="step-label">' + s + '</div>' +
            '</div>';
    });

    const progressPct = currIdx >= 0 ? (currIdx / (steps.length - 1)) * 100 : 0;

    return '<div class="order-stepper">' +
        '<div class="stepper-progress-bg">' +
            '<div class="stepper-progress-bar" style="width: ' + progressPct + '%;"></div>' +
        '</div>' +
        stepsHtml +
    '</div>';
}

async function updateOrderStatus(orderId) {
    const sel = document.getElementById('sel-' + orderId);
    if (!sel) return;
    try {
        const res = await fetch(contextPath + '/api/orders/' + orderId, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: sel.value })
        });
        const data = await res.json();
        alert(data.message || (res.ok ? 'Status updated!' : 'Failed to update'));
        load();
    } catch (e) {
        alert('Network error.');
    }
}

async function cancelOrder(orderId) {
    if (!confirm('Are you sure you want to cancel Order #' + orderId + '?')) return;
    try {
        const res = await fetch(contextPath + '/api/orders/' + orderId, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'CANCELLED' })
        });
        const data = await res.json();
        alert(data.message || (res.ok ? 'Order cancelled.' : 'Failed to cancel'));
        load();
    } catch (e) {
        alert('Network error.');
    }
}

if (document.getElementById('sellerView')) {
    document.getElementById('sellerView').addEventListener('change', load);
}

load();
</script>
</body>
</html>
