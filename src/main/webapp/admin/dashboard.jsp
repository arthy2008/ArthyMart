<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Control Center - Arthy Mart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .admin-header {
            background: linear-gradient(135deg, #0d47a1, #1565c0);
            color: #ffffff;
            padding: 32px 24px;
            border-radius: 14px;
            margin-bottom: 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 16px;
        }
        .tab-bar {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            border-bottom: 2px solid #e2e8f0;
            padding-bottom: 8px;
        }
        .tab-btn {
            background: none;
            border: none;
            padding: 10px 18px;
            font-size: 0.95rem;
            font-weight: 700;
            color: #64748b;
            cursor: pointer;
            border-radius: 8px;
            transition: all 0.2s;
        }
        .tab-btn.active {
            background: #1565c0;
            color: #ffffff;
        }
        .tab-content {
            display: none;
        }
        .tab-content.active {
            display: block;
        }
        .health-badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: rgba(255,255,255,0.2);
            padding: 6px 14px;
            border-radius: 99px;
            font-size: 0.82rem;
            font-weight: 700;
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
        <a href="${pageContext.request.contextPath}/index.jsp">Storefront</a>
        <a href="${pageContext.request.contextPath}/products.jsp">Products</a>
        <a href="${pageContext.request.contextPath}/orders.jsp">Orders</a>
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" style="color:#80d8ff;">🛡️ Admin Panel</a>
        <a href="${pageContext.request.contextPath}/api/auth/logout" class="btn-nav-outline">Logout</a>
    </div>
</nav>

<div class="container">
    <div class="admin-header">
        <div>
            <h1 style="font-size:1.8rem; font-weight:800; margin-bottom:4px;">Platform Administration 🛡️</h1>
            <p style="opacity:0.85; font-size:0.9rem;">Moderate marketplace listings, manage user accounts, and oversee store operations.</p>
        </div>
        <div class="health-badge" id="healthPill">
            <span>●</span> Checking system health...
        </div>
    </div>

    <!-- Platform Analytics KPIs -->
    <div class="kpi-grid">
        <div class="kpi-card">
            <div class="kpi-icon">💵</div>
            <div class="kpi-info">
                <h4>Gross Platform Volume</h4>
                <div class="kpi-value" id="kpiPlatformRev">₹0.00</div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon">🛍️</div>
            <div class="kpi-info">
                <h4>Total Orders</h4>
                <div class="kpi-value" id="kpiPlatformOrders">0</div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon">👥</div>
            <div class="kpi-info">
                <h4>Registered Users</h4>
                <div class="kpi-value" id="kpiPlatformUsers">0</div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon">🏷️</div>
            <div class="kpi-info">
                <h4>Catalog Listings</h4>
                <div class="kpi-value" id="kpiPlatformProducts">0</div>
            </div>
        </div>
    </div>

    <!-- Management Tabs -->
    <div class="tab-bar">
        <button class="tab-btn active" onclick="switchTab('usersTab', this)">👥 User Management</button>
        <button class="tab-btn" onclick="switchTab('ordersTab', this)">📦 All Orders</button>
        <button class="tab-btn" onclick="switchTab('listingsTab', this)">📋 Listing Moderation</button>
    </div>

    <!-- Tab 1: Users -->
    <div id="usersTab" class="tab-content active">
        <div class="card">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; flex-wrap:wrap; gap:10px;">
                <h3 style="font-size:1.15rem; font-weight:800;">Platform Accounts</h3>
                <div style="display:flex; gap:6px;">
                    <button class="btn btn-secondary" style="padding:6px 12px; font-size:0.82rem;" onclick="loadUsers('')">All</button>
                    <button class="btn btn-secondary" style="padding:6px 12px; font-size:0.82rem;" onclick="loadUsers('BUYER')">Buyers</button>
                    <button class="btn btn-secondary" style="padding:6px 12px; font-size:0.82rem;" onclick="loadUsers('SELLER')">Sellers</button>
                    <button class="btn btn-secondary" style="padding:6px 12px; font-size:0.82rem;" onclick="loadUsers('ADMIN')">Admins</button>
                </div>
            </div>
            <div id="usersList">
                <p style="color:#64748b;">Loading accounts...</p>
            </div>
        </div>
    </div>

    <!-- Tab 2: Orders -->
    <div id="ordersTab" class="tab-content">
        <div class="card">
            <h3 style="font-size:1.15rem; font-weight:800; margin-bottom:16px;">Platform Orders Overview</h3>
            <div id="adminOrdersList">
                <p style="color:#64748b;">Loading orders...</p>
            </div>
        </div>
    </div>

    <!-- Tab 3: Listings -->
    <div id="listingsTab" class="tab-content">
        <div class="card">
            <h3 style="font-size:1.15rem; font-weight:800; margin-bottom:16px;">Product Listings Moderation</h3>
            <div id="adminProductsList">
                <p style="color:#64748b;">Loading listings...</p>
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

function switchTab(tabId, btn) {
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
    btn.classList.add('active');
    document.getElementById(tabId).classList.add('active');
}

async function load() {
    // 1. Health check
    try {
        const h = await fetch(contextPath + '/api/health', { credentials: 'same-origin' });
        const hj = await h.json();
        const pill = document.getElementById('healthPill');
        if (hj.status === 'UP' && hj.db === 'UP') {
            pill.innerHTML = '<span style="color:#4ade80;">●</span> System Healthy (DB: UP)';
        } else {
            pill.innerHTML = '<span style="color:#f87171;">●</span> System Degraded';
        }
    } catch (e) {
        document.getElementById('healthPill').textContent = '● Health Check Offline';
    }

    // 2. Stats
    try {
        const statsRes = await fetch(contextPath + '/api/admin/stats', { credentials: 'same-origin' });
        if (statsRes.ok) {
            const stats = await statsRes.json();
            document.getElementById('kpiPlatformRev').textContent = '₹' + Number(stats.totalRevenue || 0).toFixed(2);
            document.getElementById('kpiPlatformOrders').textContent = stats.totalOrders || 0;
            document.getElementById('kpiPlatformUsers').textContent = stats.totalUsers || 0;
            document.getElementById('kpiPlatformProducts').textContent = stats.totalProducts || 0;
        }
    } catch (e) {}

    // 3. Load Users
    loadUsers('');

    // 4. Load Orders
    try {
        const oRes = await fetch(contextPath + '/api/admin/orders', { credentials: 'same-origin' });
        if (oRes.ok) {
            const orders = await oRes.json();
            const oBox = document.getElementById('adminOrdersList');
            if (orders.length === 0) {
                oBox.innerHTML = '<p style="color:#64748b;">No orders in the system.</p>';
            } else {
                let html = '<table class="data-table"><thead><tr><th>Order ID</th><th>Buyer ID</th><th>Amount</th><th>Status</th><th>Date</th></tr></thead><tbody>';
                orders.forEach(o => {
                    const statusClass = 'badge-' + (o.status || 'PENDING').toLowerCase();
                    const dateStr = o.createdAt || 'Recent';
                    html +=
                        '<tr>' +
                            '<td><strong>Order #' + o.id + '</strong></td>' +
                            '<td>Buyer #' + o.buyerId + '</td>' +
                            '<td><strong>₹' + o.totalAmount + '</strong></td>' +
                            '<td><span class="badge ' + statusClass + '">' + o.status + '</span></td>' +
                            '<td>' + dateStr + '</td>' +
                        '</tr>';
                });
                html += '</tbody></table>';
                oBox.innerHTML = html;
            }
        }
    } catch (e) {}

    // 5. Load Listings
    try {
        const pRes = await fetch(contextPath + '/api/admin/products', { credentials: 'same-origin' });
        if (pRes.ok) {
            const products = await pRes.json();
            const pBox = document.getElementById('adminProductsList');
            if (products.length === 0) {
                pBox.innerHTML = '<p style="color:#64748b;">No products in the catalog.</p>';
            } else {
                let html = '<table class="data-table"><thead><tr><th>ID</th><th>Product Name</th><th>Price</th><th>Stock</th><th>Category</th><th>Action</th></tr></thead><tbody>';
                products.forEach(p => {
                    const safeName = escapeHtml(p.name || 'Product');
                    const safeCat = escapeHtml(p.category || 'General');
                    const stock = (p.stockQty !== undefined ? p.stockQty : (p.stock_qty || 0));
                    html +=
                        '<tr>' +
                            '<td>#' + p.id + '</td>' +
                            '<td><strong>' + safeName + '</strong></td>' +
                            '<td>₹' + p.price + '</td>' +
                            '<td>' + stock + '</td>' +
                            '<td>' + safeCat + '</td>' +
                            '<td>' +
                                '<button class="btn btn-danger" style="padding:4px 10px; font-size:0.8rem;" onclick="adminDeleteProduct(' + p.id + ')">Remove Listing</button>' +
                            '</td>' +
                        '</tr>';
                });
                html += '</tbody></table>';
                pBox.innerHTML = html;
            }
        }
    } catch (e) {}
}

async function loadUsers(role) {
    const uBox = document.getElementById('usersList');
    try {
        const r = await fetch(contextPath + '/api/admin/users' + (role ? '?role=' + role : ''), { credentials: 'same-origin' });
        if (r.status === 401 || r.status === 403) {
            uBox.innerHTML = '<p style="color:#dc2626;">Administrator credentials required.</p>';
            return;
        }
        const users = await r.json();
        if (users.length === 0) {
            uBox.innerHTML = '<p style="color:#64748b;">No accounts found.</p>';
            return;
        }

        let html = '<table class="data-table"><thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Created</th><th>Actions</th></tr></thead><tbody>';
        users.forEach(u => {
            const roleBadge = u.role === 'ADMIN' ? 'badge-cancelled' : (u.role === 'SELLER' ? 'badge-confirmed' : 'badge-delivered');
            html += `
                <tr>
                    <td>#${u.id}</td>
                    <td><strong>${escapeHtml(u.name)}</strong></td>
                    <td>${escapeHtml(u.email)}</td>
                    <td><span class="badge ${roleBadge}">${u.role}</span></td>
                    <td>${u.createdAt || 'Standard'}</td>
                    <td>
                        ${u.role !== 'ADMIN' ? `<button class="btn btn-danger" style="padding:3px 8px; font-size:0.75rem;" onclick="deleteUser(${u.id})">Delete</button>` : '<span style="color:#94a3b8; font-size:0.75rem;">Protected</span>'}
                    </td>
                </tr>
            `;
        });
        html += '</tbody></table>';
        uBox.innerHTML = html;

    } catch (e) {
        uBox.innerHTML = '<p style="color:#dc2626;">Error loading user accounts.</p>';
    }
}

async function adminDeleteProduct(id) {
    if (!confirm('Forcefully remove product listing #' + id + '?')) return;
    try {
        const res = await fetch(contextPath + '/api/admin/products/' + id, { method: 'DELETE', credentials: 'same-origin' });
        if (res.ok) {
            alert('Listing removed by administrator.');
            load();
        } else {
            alert('Failed to remove listing.');
        }
    } catch (e) {
        alert('Network error.');
    }
}

async function deleteUser(userId) {
    if (!confirm('Delete user #' + userId + '? This will cascade remove their associated records.')) return;
    try {
        const res = await fetch(contextPath + '/api/admin/users/' + userId, { method: 'DELETE', credentials: 'same-origin' });
        if (res.ok) {
            alert('User deleted.');
            loadUsers('');
        } else {
            alert('Cannot delete user.');
        }
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
