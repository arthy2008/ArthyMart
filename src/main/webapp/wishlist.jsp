<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Wishlist - Arthy Mart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .wishlist-container {
            max-width: 1000px;
            margin: 36px auto;
            padding: 0 20px;
        }
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
            padding-bottom: 14px;
            border-bottom: 2px solid #e2e8f0;
        }
        .page-title {
            font-size: 1.8rem;
            font-weight: 800;
            color: #0f172a;
        }
        .wishlist-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 20px;
        }
        .wish-card {
            background: #ffffff;
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 4px 14px rgba(0,0,0,0.06);
            display: flex;
            flex-direction: column;
            border: 1px solid #e2e8f0;
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .wish-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 10px 24px rgba(0,0,0,0.1);
        }
        .wish-img {
            width: 100%;
            height: 180px;
            object-fit: cover;
            background: #f1f5f9;
        }
        .wish-body {
            padding: 16px;
            flex: 1;
            display: flex;
            flex-direction: column;
        }
        .wish-cat {
            font-size: 0.75rem;
            color: #1565c0;
            font-weight: 700;
            text-transform: uppercase;
            margin-bottom: 4px;
        }
        .wish-name {
            font-size: 1rem;
            font-weight: 700;
            color: #1e293b;
            margin-bottom: 8px;
        }
        .wish-price {
            font-size: 1.25rem;
            font-weight: 800;
            color: #0f172a;
            margin-bottom: 12px;
        }
        .wish-stock {
            font-size: 0.8rem;
            color: #16a34a;
            font-weight: 600;
            margin-bottom: 16px;
        }
        .wish-stock.out {
            color: #dc2626;
        }
        .wish-actions {
            margin-top: auto;
            display: flex;
            gap: 8px;
        }
        .btn-move-cart {
            flex: 1;
            padding: 10px;
            background: #1565c0;
            color: #fff;
            border: none;
            border-radius: 8px;
            font-weight: 700;
            cursor: pointer;
            transition: background 0.2s;
        }
        .btn-move-cart:hover {
            background: #0d47a1;
        }
        .btn-remove-wish {
            padding: 10px 14px;
            background: #fee2e2;
            color: #dc2626;
            border: none;
            border-radius: 8px;
            font-weight: 700;
            cursor: pointer;
            transition: background 0.2s;
        }
        .btn-remove-wish:hover {
            background: #fecaca;
        }
        .empty-wish {
            text-align: center;
            padding: 60px 20px;
            background: #fff;
            border-radius: 16px;
            border: 1px dashed #cbd5e1;
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
        <a href="${pageContext.request.contextPath}/wishlist.jsp" style="color:#80d8ff;">❤️ Wishlist</a>
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

<div class="wishlist-container">
    <div class="page-header">
        <div>
            <h1 class="page-title">❤️ My Saved Wishlist</h1>
            <p style="color:#64748b; font-size:0.9rem; margin-top:4px;">Products you've saved for later.</p>
        </div>
        <button id="clearAllBtn" class="btn-remove-wish" style="display:none;" onclick="clearAllWishlist()">Clear All</button>
    </div>

    <div id="wishlistGrid" class="wishlist-grid">
        <p style="color:#64748b;">Loading your wishlist...</p>
    </div>
</div>

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

async function loadWishlist() {
    const grid = document.getElementById('wishlistGrid');
    const clearBtn = document.getElementById('clearAllBtn');
    try {
        const res = await fetch(contextPath + '/api/wishlist');
        if (res.status === 401) {
            window.location.href = contextPath + '/login.jsp';
            return;
        }
        const data = await res.json();
        const items = data.items || [];
        grid.innerHTML = '';

        if (items.length === 0) {
            clearBtn.style.display = 'none';
            grid.innerHTML =
                '<div class="empty-wish" style="grid-column: 1 / -1;">' +
                    '<span style="font-size: 3rem; display: block; margin-bottom: 12px;">💔</span>' +
                    '<h3>Your wishlist is currently empty</h3>' +
                    '<p style="color: #64748b; margin: 8px 0 20px;">Explore our catalog and click the heart icon on any product to save it here.</p>' +
                    '<a href="' + contextPath + '/products.jsp" class="btn" style="background: #1565c0; color: #fff; padding: 10px 24px; border-radius: 8px; text-decoration: none; font-weight: 700;">Explore Products</a>' +
                '</div>';
            return;
        }

        clearBtn.style.display = 'inline-block';

        items.forEach(item => {
            const card = document.createElement('div');
            card.className = 'wish-card';
            const img = item.imageUrl ? item.imageUrl : 'https://placehold.co/400x300?text=Product';
            const rawStock = (item.stockQty !== undefined && item.stockQty !== null) ? item.stockQty : item.stock_qty;
            const stock = (rawStock !== undefined && rawStock !== null) ? parseInt(rawStock, 10) : 0;
            const inStock = !isNaN(stock) && stock > 0;
            const safeName = escapeHtml(item.productName || 'Product');
            const safeCat = escapeHtml(item.category || 'General');
            const priceStr = Number(item.price || 0).toFixed(2);

            card.innerHTML =
                '<img src="' + img + '" class="wish-img" alt="' + safeName + '">' +
                '<div class="wish-body">' +
                    '<div class="wish-cat">' + safeCat + '</div>' +
                    '<a href="' + contextPath + '/product-detail.jsp?id=' + item.productId + '" style="text-decoration:none;">' +
                        '<h3 class="wish-name">' + safeName + '</h3>' +
                    '</a>' +
                    '<div class="wish-price">₹' + priceStr + '</div>' +
                    '<div class="wish-stock ' + (inStock ? '' : 'out') + '">' +
                        (inStock ? '✓ In Stock (' + stock + ' available)' : '✗ Out of Stock') +
                    '</div>' +
                    '<div class="wish-actions">' +
                        '<button class="btn-move-cart" onclick="moveToCart(' + item.productId + ')" ' + (inStock ? '' : 'disabled') + '>🛒 Move to Cart</button>' +
                        '<button class="btn-remove-wish" onclick="removeFromWishlist(' + item.productId + ')">🗑️</button>' +
                    '</div>' +
                '</div>';
            grid.appendChild(card);
        });

    } catch (err) {
        grid.innerHTML = '<p style="color:#ef4444;">Failed to load wishlist. Please refresh.</p>';
    }
}

async function removeFromWishlist(productId) {
    try {
        const res = await fetch(contextPath + '/api/wishlist/' + productId, { method: 'DELETE' });
        if (res.ok) {
            loadWishlist();
        }
    } catch (e) {
        alert('Failed to remove item.');
    }
}

async function clearAllWishlist() {
    if (!confirm('Clear all items from your wishlist?')) return;
    try {
        const res = await fetch(contextPath + '/api/wishlist', { method: 'DELETE' });
        if (res.ok) loadWishlist();
    } catch (e) {
        alert('Network error.');
    }
}

async function moveToCart(productId) {
    try {
        const res = await fetch(contextPath + '/api/cart', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId: productId, quantity: 1 })
        });
        if (res.ok) {
            alert('Added to cart!');
            await removeFromWishlist(productId);
        } else {
            const d = await res.json();
            alert(d.message || 'Cannot add to cart.');
        }
    } catch (e) {
        alert('Network error.');
    }
}

loadWishlist();
</script>
</body>
</html>
