<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products - Arthy Mart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .catalog-header {
            background: linear-gradient(135deg, #0d47a1, #1565c0);
            color: #ffffff;
            padding: 36px 20px;
            text-align: center;
            margin-bottom: 30px;
        }
        .catalog-header h1 {
            font-size: 2.2rem;
            font-weight: 800;
            margin-bottom: 8px;
        }
        .catalog-header p {
            color: rgba(255,255,255,0.85);
            font-size: 1rem;
        }
        .filter-panel {
            background: #ffffff;
            border-radius: 12px;
            padding: 20px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 2px 10px rgba(0,0,0,0.03);
            margin-bottom: 28px;
            display: flex;
            flex-direction: column;
            gap: 16px;
        }
        .search-row {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
        }
        .search-input {
            flex: 1;
            min-width: 240px;
        }
        .category-pills {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
        }
        .cat-pill {
            padding: 6px 14px;
            border-radius: 99px;
            background: #f1f5f9;
            color: #475569;
            font-size: 0.82rem;
            font-weight: 700;
            border: 1.5px solid #cbd5e1;
            cursor: pointer;
            transition: all 0.2s;
        }
        .cat-pill.active {
            background: #1565c0;
            color: #ffffff;
            border-color: #1565c0;
        }
        .product-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
            gap: 24px;
        }
        .prod-card {
            background: #ffffff;
            border-radius: 14px;
            border: 1px solid #e2e8f0;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            box-shadow: 0 4px 12px rgba(0,0,0,0.04);
            transition: transform 0.2s, box-shadow 0.2s;
            position: relative;
        }
        .prod-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 24px rgba(0,0,0,0.1);
        }
        .prod-img-wrap {
            position: relative;
            width: 100%;
            height: 190px;
            background: #f8fafc;
            overflow: hidden;
        }
        .prod-img {
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.3s;
        }
        .prod-card:hover .prod-img {
            transform: scale(1.04);
        }
        .btn-wish-toggle {
            position: absolute;
            top: 10px;
            right: 10px;
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: rgba(255,255,255,0.9);
            border: 1px solid #e2e8f0;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            font-size: 16px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
            transition: transform 0.15s, background 0.15s;
        }
        .btn-wish-toggle:hover {
            transform: scale(1.1);
            background: #ffffff;
        }
        .prod-body {
            padding: 16px;
            flex: 1;
            display: flex;
            flex-direction: column;
        }
        .prod-cat {
            font-size: 0.72rem;
            color: #1565c0;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 4px;
        }
        .prod-name {
            font-size: 1rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 6px;
            line-height: 1.35;
        }
        .prod-desc {
            font-size: 0.8rem;
            color: #64748b;
            margin-bottom: 12px;
            line-height: 1.45;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .prod-footer {
            margin-top: auto;
            padding-top: 10px;
            border-top: 1px solid #f1f5f9;
        }
        .prod-price-row {
            display: flex;
            justify-content: space-between;
            align-items: baseline;
            margin-bottom: 10px;
        }
        .prod-price {
            font-size: 1.3rem;
            font-weight: 800;
            color: #0f172a;
        }
        .prod-stock {
            font-size: 0.75rem;
            font-weight: 700;
            color: #16a34a;
        }
        .prod-stock.out {
            color: #dc2626;
        }
        .prod-actions {
            display: flex;
            gap: 8px;
        }
        .btn-cart {
            flex: 1;
            background: #1565c0;
            color: #fff;
            padding: 9px;
            font-size: 0.85rem;
            border-radius: 8px;
            border: none;
            font-weight: 700;
            cursor: pointer;
            transition: background 0.2s;
        }
        .btn-cart:hover {
            background: #0d47a1;
        }
        .btn-cart:disabled {
            background: #94a3b8;
            cursor: not-allowed;
        }
        .btn-details {
            padding: 9px 12px;
            background: #f1f5f9;
            color: #334155;
            text-decoration: none;
            border-radius: 8px;
            font-size: 0.85rem;
            font-weight: 700;
            transition: background 0.2s;
        }
        .btn-details:hover {
            background: #e2e8f0;
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
        <a href="${pageContext.request.contextPath}/products.jsp" style="color:#80d8ff;">Products</a>
        <a href="${pageContext.request.contextPath}/wishlist.jsp">❤️ Wishlist</a>
        <a href="${pageContext.request.contextPath}/cart.jsp">🛒 Cart</a>
        <a href="${pageContext.request.contextPath}/orders.jsp">Orders</a>
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

<div class="catalog-header">
    <h1>Explore All Products 🛍️</h1>
    <p>Find top-rated electronics, books, kitchen appliances, and more.</p>
</div>

<div class="container">
    <div class="filter-panel">
        <form id="searchForm" class="search-row">
            <input type="text" id="q" class="search-input" placeholder="Search by product name or description...">
            <select id="sortSelect" style="width:180px;">
                <option value="default">Sort: Default</option>
                <option value="price-low">Price: Low to High</option>
                <option value="price-high">Price: High to Low</option>
                <option value="name">Name (A-Z)</option>
            </select>
            <button type="submit" class="btn">Search</button>
            <button type="button" id="resetBtn" class="btn btn-secondary">Clear</button>
        </form>

        <div class="category-pills" id="categoryPills">
            <div class="cat-pill active" onclick="setCategoryFilter('')">All Categories</div>
            <div class="cat-pill" onclick="setCategoryFilter('Electronics')">📱 Electronics</div>
            <div class="cat-pill" onclick="setCategoryFilter('Books')">📚 Books</div>
            <div class="cat-pill" onclick="setCategoryFilter('Home & Kitchen')">🏠 Home &amp; Kitchen</div>
            <div class="cat-pill" onclick="setCategoryFilter('Clothing')">👕 Clothing</div>
            <div class="cat-pill" onclick="setCategoryFilter('Sports')">⚽ Sports</div>
        </div>
        <input type="hidden" id="category" value="">
    </div>

    <div id="grid" class="product-grid"></div>
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

let currentProducts = [];

async function load(params = '') {
    const grid = document.getElementById('grid');
    grid.innerHTML = '<p style="color:#64748b; grid-column:1/-1; text-align:center;">Loading products...</p>';
    try {
        const res = await fetch(contextPath + '/api/products' + params);
        if (!res.ok) {
            grid.innerHTML = '<p style="color:#dc2626; grid-column:1/-1; text-align:center;">Failed to load products.</p>';
            return;
        }
        currentProducts = await res.json();
        renderProducts();
    } catch (err) {
        grid.innerHTML = '<p style="color:#dc2626; grid-column:1/-1; text-align:center;">Network error loading products.</p>';
    }
}

function renderProducts() {
    const grid = document.getElementById('grid');
    grid.innerHTML = '';

    if (!currentProducts || currentProducts.length === 0) {
        grid.innerHTML =
            '<div style="grid-column: 1 / -1; text-align: center; padding: 60px 20px; background: #fff; border-radius: 12px; border: 1px dashed #cbd5e1;">' +
                '<span style="font-size: 3rem; display: block; margin-bottom: 12px;">🔍</span>' +
                '<h3>No products found</h3>' +
                '<p style="color: #64748b; margin-top: 6px;">Try adjusting your search terms or filters.</p>' +
                '<button onclick="clearFilters()" class="btn" style="margin-top: 16px;">View All Products</button>' +
            '</div>';
        return;
    }

    // Apply client-side sorting
    const sortVal = document.getElementById('sortSelect').value;
    let sorted = [...currentProducts];
    if (sortVal === 'price-low') {
        sorted.sort((a, b) => a.price - b.price);
    } else if (sortVal === 'price-high') {
        sorted.sort((a, b) => b.price - a.price);
    } else if (sortVal === 'name') {
        sorted.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
    }

    sorted.forEach(p => {
        const card = document.createElement('div');
        card.className = 'prod-card';
        const img = p.imageUrl ? p.imageUrl : 'https://placehold.co/400x300?text=Product';
        const rawStock = (p.stockQty !== undefined && p.stockQty !== null) ? p.stockQty : p.stock_qty;
        const stock = (rawStock !== undefined && rawStock !== null) ? parseInt(rawStock, 10) : 0;
        const inStock = !isNaN(stock) && stock > 0;
        const safeName = escapeHtml(p.name || 'Product');
        const safeDesc = escapeHtml(p.description || 'No description available.');
        const safeCat = escapeHtml(p.category || 'General');
        const priceStr = Number(p.price || 0).toFixed(2);

        card.innerHTML =
            '<div class="prod-img-wrap">' +
                '<img src="' + img + '" class="prod-img" alt="' + safeName + '">' +
                '<button class="btn-wish-toggle" onclick="toggleWishlist(' + p.id + ', this)" title="Save to wishlist">🤍</button>' +
            '</div>' +
            '<div class="prod-body">' +
                '<div class="prod-cat">' + safeCat + '</div>' +
                '<h3 class="prod-name">' + safeName + '</h3>' +
                '<p class="prod-desc">' + safeDesc + '</p>' +
                '<div class="prod-footer">' +
                    '<div class="prod-price-row">' +
                        '<span class="prod-price">$' + priceStr + '</span>' +
                        '<span class="prod-stock ' + (inStock ? '' : 'out') + '">' +
                            (inStock ? '✓ ' + stock + ' in stock' : 'Out of stock') +
                        '</span>' +
                    '</div>' +
                    '<div class="prod-actions">' +
                        '<button class="btn-cart" onclick="addToCart(' + p.id + ')" ' + (inStock ? '' : 'disabled') + '>' +
                            (inStock ? '🛒 Add to Cart' : 'Unavailable') +
                        '</button>' +
                        '<a href="' + contextPath + '/product-detail.jsp?id=' + p.id + '" class="btn-details">Details</a>' +
                    '</div>' +
                '</div>' +
            '</div>';
        grid.appendChild(card);
    });
}

async function addToCart(productId) {
    try {
        const res = await fetch(contextPath + '/api/cart', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId: productId, quantity: 1 })
        });
        const data = await res.json();
        if (res.ok) {
            alert('✅ Added to cart!');
        } else if (res.status === 401) {
            alert('Please login to add items to your cart.');
            window.location.href = contextPath + '/login.jsp';
        } else {
            alert(data.message || 'Could not add to cart.');
        }
    } catch (e) {
        alert('Network error.');
    }
}

async function toggleWishlist(productId, btn) {
    try {
        const res = await fetch(contextPath + '/api/wishlist', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId: productId })
        });
        if (res.status === 401) {
            alert('Please login to save products to your wishlist.');
            window.location.href = contextPath + '/login.jsp';
            return;
        }
        const data = await res.json();
        if (res.ok) {
            btn.textContent = '❤️';
            alert('❤️ ' + (data.message || 'Saved to wishlist!'));
        } else {
            alert(data.message || 'Failed to update wishlist.');
        }
    } catch (e) {
        alert('Network error.');
    }
}

function setCategoryFilter(cat) {
    document.getElementById('category').value = cat;
    document.querySelectorAll('.cat-pill').forEach(pill => {
        pill.classList.remove('active');
        if ((!cat && pill.textContent.includes('All')) || (cat && pill.textContent.includes(cat))) {
            pill.classList.add('active');
        }
    });
    triggerSearch();
}

function clearFilters() {
    document.getElementById('q').value = '';
    document.getElementById('sortSelect').value = 'default';
    setCategoryFilter('');
}

document.getElementById('resetBtn').addEventListener('click', clearFilters);
document.getElementById('sortSelect').addEventListener('change', renderProducts);

document.getElementById('searchForm').addEventListener('submit', (e) => {
    e.preventDefault();
    triggerSearch();
});

function triggerSearch() {
    const qVal = document.getElementById('q').value.trim();
    const cVal = document.getElementById('category').value.trim();
    const params = new URLSearchParams();
    if (qVal) params.set('q', qVal);
    if (cVal) params.set('category', cVal);
    const qs = params.toString();
    load(qs ? '?' + qs : '');
}

// Deep linking support
(function init() {
    const urlParams = new URLSearchParams(window.location.search);
    const q = urlParams.get('q');
    const cat = urlParams.get('category');
    if (q) document.getElementById('q').value = q;
    if (cat) setCategoryFilter(cat);
    else load();
})();
</script>
</body>
</html>
