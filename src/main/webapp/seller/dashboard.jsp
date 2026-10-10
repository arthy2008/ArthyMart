<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seller Center - Arthy Mart</title>
    <link rel="stylesheet" href="../css/style.css">
    <style>
        .seller-header {
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
        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
        }
        .form-grid .full-span {
            grid-column: 1 / -1;
        }
        .prod-table-img {
            width: 44px;
            height: 44px;
            border-radius: 6px;
            object-fit: cover;
            background: #f1f5f9;
        }
    </style>
</head>
<body>

<!-- Navbar -->
<nav class="navbar">
    <a href="../" class="nav-brand">
        <span class="nav-logo">🛒</span>
        <span class="nav-brand-name">ARTHY <span>MART</span></span>
    </a>
    <div class="nav-links">
        <a href="../index.jsp">Storefront</a>
        <a href="../products.jsp">Products</a>
        <a href="../orders.jsp">Orders</a>
        <a href="dashboard.jsp" style="color:#80d8ff;">🏪 Seller Dashboard</a>
        <a href="../api/auth/logout" class="btn-nav-outline">Logout</a>
    </div>
</nav>

<div class="container">
    <div class="seller-header">
        <div>
            <h1 style="font-size:1.8rem; font-weight:800; margin-bottom:4px;">Merchant Sales Dashboard 🏪</h1>
            <p style="opacity:0.85; font-size:0.9rem;">Manage inventory, monitor revenues, and fulfill customer orders.</p>
        </div>
        <a href="../orders.jsp?view=seller" class="btn btn-accent" style="color:#fff;">Fulfill Orders &rarr;</a>
    </div>

    <!-- KPI Analytics Cards -->
    <div class="kpi-grid">
        <div class="kpi-card">
            <div class="kpi-icon">💰</div>
            <div class="kpi-info">
                <h4>Total Sales Revenue</h4>
                <div class="kpi-value" id="kpiRev">$0.00</div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon">📦</div>
            <div class="kpi-info">
                <h4>Store Orders</h4>
                <div class="kpi-value" id="kpiOrders">0</div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon">🏷️</div>
            <div class="kpi-info">
                <h4>Active Listings</h4>
                <div class="kpi-value" id="kpiListings">0</div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon">⏳</div>
            <div class="kpi-info">
                <h4>Pending Fulfillment</h4>
                <div class="kpi-value" id="kpiPending" style="color:#d97706;">0</div>
            </div>
        </div>
    </div>

    <!-- Product Creation Form -->
    <div class="card">
        <h2 id="formTitle" style="font-size:1.25rem; font-weight:800; color:#0f172a; margin-bottom:16px;">Add New Product ➕</h2>
        <form id="pForm">
            <input type="hidden" id="pid">
            <div class="form-grid">
                <div>
                    <label for="name">Product Name *</label>
                    <input type="text" id="name" placeholder="e.g. Ergonomic Wireless Mouse" required maxlength="150">
                </div>
                <div>
                    <label for="cat">Category *</label>
                    <select id="cat" required>
                        <option value="">Select Category...</option>
                        <option value="Electronics">Electronics</option>
                        <option value="Books">Books</option>
                        <option value="Home & Kitchen">Home & Kitchen</option>
                        <option value="Clothing">Clothing</option>
                        <option value="Sports">Sports</option>
                    </select>
                </div>
                <div>
                    <label for="price">Price ($) *</label>
                    <input type="number" id="price" step="0.01" min="0.01" placeholder="29.99" required>
                </div>
                <div>
                    <label for="stock">Stock Quantity *</label>
                    <input type="number" id="stock" min="0" placeholder="50" required>
                </div>
                <div class="full-span">
                    <label for="img">Image URL</label>
                    <input type="text" id="img" placeholder="https://images.unsplash.com/...">
                </div>
                <div class="full-span">
                    <label for="desc">Product Description</label>
                    <textarea id="desc" rows="3" placeholder="Key specifications and highlights..."></textarea>
                </div>
            </div>
            <div style="margin-top:16px; display:flex; gap:10px;">
                <button type="submit" class="btn" id="saveBtn">Save Product</button>
                <button type="button" id="resetBtn" class="btn btn-secondary">Reset Form</button>
            </div>
        </form>
    </div>

    <!-- Current Listings Table -->
    <div class="card">
        <h2 style="font-size:1.25rem; font-weight:800; color:#0f172a; margin-bottom:16px;">My Product Listings 📋</h2>
        <div id="tableWrapper">
            <p style="color:#64748b;">Loading listings...</p>
        </div>
    </div>
</div>

<footer>
    <p>&copy; 2026 <span>Arthy Mart</span> &middot; Full Stack E-Commerce Platform</p>
</footer>

<script src="../js/chat-widget.js"></script>
<script>
async function load() {
    // 1. Load Seller Stats
    try {
        const statsRes = await fetch('../api/orders?view=seller-stats');
        if (statsRes.ok) {
            const stats = await statsRes.json();
            document.getElementById('kpiRev').textContent = '$' + Number(stats.totalRevenue || 0).toFixed(2);
            document.getElementById('kpiOrders').textContent = stats.totalOrders || 0;
            document.getElementById('kpiPending').textContent = stats.pendingOrders || 0;
        }
    } catch (e) {}

    // 2. Load Seller Products
    const wrap = document.getElementById('tableWrapper');
    try {
        const res = await fetch('../api/products?mine=true');
        if (res.status === 401 || res.status === 403) {
            wrap.innerHTML = '<p style="color:#dc2626;">Seller authentication required. Please sign in with a SELLER account.</p>';
            return;
        }
        const products = await res.json();
        document.getElementById('kpiListings').textContent = products.length;

        if (products.length === 0) {
            wrap.innerHTML = '<p style="color:#64748b; padding:20px; text-align:center;">You have not listed any products yet. Fill the form above to add your first product.</p>';
            return;
        }

        sellerProductsList = products;
        let tableHtml =
            '<table class="data-table">' +
                '<thead>' +
                    '<tr>' +
                        '<th>Product</th>' +
                        '<th>Category</th>' +
                        '<th>Price</th>' +
                        '<th>Stock</th>' +
                        '<th>Actions</th>' +
                    '</tr>' +
                '</thead>' +
                '<tbody>';

        products.forEach(p => {
            const img = p.imageUrl ? p.imageUrl : 'https://placehold.co/60x60?text=Product';
            const safeName = escapeHtml(p.name || 'Product');
            const safeCat = escapeHtml(p.category || 'General');
            const rawStock = (p.stockQty !== undefined && p.stockQty !== null) ? p.stockQty : p.stock_qty;
            const stock = (rawStock !== undefined && rawStock !== null) ? parseInt(rawStock, 10) : 0;
            const badgeClass = stock > 0 ? 'badge-delivered' : 'badge-cancelled';
            const priceStr = Number(p.price || 0).toFixed(2);

            tableHtml +=
                '<tr>' +
                    '<td>' +
                        '<div style="display:flex; align-items:center; gap:10px;">' +
                            '<img src="' + img + '" class="prod-table-img" alt="' + safeName + '">' +
                            '<div>' +
                                '<strong style="color:#0f172a;">' + safeName + '</strong>' +
                                '<div style="font-size:0.75rem; color:#64748b;">ID #' + p.id + '</div>' +
                            '</div>' +
                        '</div>' +
                    '</td>' +
                    '<td><span class="badge" style="background:#e0f2fe; color:#0369a1;">' + safeCat + '</span></td>' +
                    '<td><strong>$' + priceStr + '</strong></td>' +
                    '<td><span class="badge ' + badgeClass + '">' + stock + ' in stock</span></td>' +
                    '<td>' +
                        '<button class="btn btn-secondary" style="padding:4px 10px; font-size:0.8rem;" onclick="editProductById(' + p.id + ')">Edit</button> ' +
                        '<button class="btn btn-danger" style="padding:4px 10px; font-size:0.8rem;" onclick="deleteProduct(' + p.id + ')">Delete</button>' +
                    '</td>' +
                '</tr>';
        });

        tableHtml += '</tbody></table>';
        wrap.innerHTML = tableHtml;

    } catch (e) {
        wrap.innerHTML = '<p style="color:#dc2626;">Error loading products.</p>';
    }
}

let sellerProductsList = [];

function editProductById(id) {
    const p = sellerProductsList.find(item => item.id === id);
    if (!p) return;
    document.getElementById('formTitle').textContent = 'Edit Product #' + p.id + ' ✏️';
    document.getElementById('pid').value = p.id;
    document.getElementById('name').value = p.name;
    document.getElementById('desc').value = p.description || '';
    document.getElementById('price').value = p.price;
    document.getElementById('stock').value = (p.stockQty !== undefined ? p.stockQty : (p.stock_qty || 0));
    document.getElementById('cat').value = p.category || '';
    document.getElementById('img').value = p.imageUrl || '';
    document.getElementById('saveBtn').textContent = 'Update Product';
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function deleteProduct(productId) {
    if (!confirm('Are you sure you want to permanently delete this listing?')) return;
    try {
        const res = await fetch('../api/products/' + productId, { method: 'DELETE' });
        if (res.ok) {
            alert('Listing deleted successfully.');
            load();
        } else {
            const d = await res.json();
            alert(d.message || 'Cannot delete listing.');
        }
    } catch (e) {
        alert('Network error.');
    }
}

document.getElementById('pForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const pid = document.getElementById('pid').value;
    const body = {
        name: document.getElementById('name').value.trim(),
        description: document.getElementById('desc').value.trim(),
        price: document.getElementById('price').value,
        stockQty: parseInt(document.getElementById('stock').value),
        category: document.getElementById('cat').value,
        imageUrl: document.getElementById('img').value.trim()
    };

    try {
        const res = pid ? await fetch('../api/products/' + pid, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        }) : await fetch('../api/products', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body)
        });

        const data = await res.json();
        if (res.ok) {
            alert('✅ Product saved successfully!');
            resetForm();
            load();
        } else {
            alert('❌ ' + (data.message || 'Failed to save product.'));
        }
    } catch (err) {
        alert('⚠️ Network error.');
    }
});

function resetForm() {
    document.getElementById('pForm').reset();
    document.getElementById('pid').value = '';
    document.getElementById('formTitle').textContent = 'Add New Product ➕';
    document.getElementById('saveBtn').textContent = 'Save Product';
}

document.getElementById('resetBtn').addEventListener('click', resetForm);

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

load();
</script>
</body>
</html>
