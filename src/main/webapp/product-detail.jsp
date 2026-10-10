<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Product Details - Arthy Mart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .product-layout {
            display: grid;
            grid-template-columns: 1fr 1.2fr;
            gap: 36px;
            background: #ffffff;
            border-radius: 16px;
            padding: 32px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
            margin-bottom: 36px;
        }
        .gallery-wrap {
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f8fafc;
            border-radius: 12px;
            overflow: hidden;
            min-height: 340px;
            border: 1px solid #e2e8f0;
        }
        .main-img {
            max-width: 100%;
            max-height: 360px;
            object-fit: contain;
        }
        .info-wrap {
            display: flex;
            flex-direction: column;
        }
        .detail-cat {
            font-size: 0.8rem;
            color: #1565c0;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 6px;
        }
        .detail-title {
            font-size: 1.8rem;
            font-weight: 800;
            color: #0f172a;
            margin-bottom: 12px;
            line-height: 1.3;
        }
        .detail-price {
            font-size: 2rem;
            font-weight: 900;
            color: #0f172a;
            margin-bottom: 12px;
        }
        .detail-stock {
            display: inline-block;
            font-size: 0.82rem;
            font-weight: 700;
            padding: 4px 10px;
            border-radius: 99px;
            margin-bottom: 18px;
            width: fit-content;
        }
        .stock-available { background: #dcfce7; color: #166534; }
        .stock-none      { background: #fee2e2; color: #991b1b; }
        .detail-desc {
            font-size: 0.95rem;
            color: #475569;
            line-height: 1.6;
            margin-bottom: 24px;
        }
        .btn-group {
            display: flex;
            gap: 12px;
            margin-top: auto;
            flex-wrap: wrap;
        }
        .btn-add-cart {
            flex: 1;
            min-width: 180px;
            padding: 12px 24px;
            background: #1565c0;
            color: #fff;
            font-size: 1rem;
            font-weight: 700;
            border: none;
            border-radius: 9px;
            cursor: pointer;
            transition: background 0.2s;
        }
        .btn-add-cart:hover { background: #0d47a1; }
        .btn-add-cart:disabled { background: #94a3b8; cursor: not-allowed; }
        .btn-add-wish {
            padding: 12px 20px;
            background: #fff;
            color: #dc2626;
            border: 2px solid #fecaca;
            font-size: 0.95rem;
            font-weight: 700;
            border-radius: 9px;
            cursor: pointer;
            transition: all 0.2s;
        }
        .btn-add-wish:hover { background: #fee2e2; }

        .reviews-section {
            background: #ffffff;
            border-radius: 16px;
            padding: 32px;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 16px rgba(0,0,0,0.04);
        }
        .rev-header {
            display: flex;
            justify-content: space-between;
            align-items: baseline;
            margin-bottom: 20px;
            padding-bottom: 12px;
            border-bottom: 2px solid #f1f5f9;
        }
        .rev-card {
            background: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            padding: 16px;
            margin-bottom: 12px;
        }
        .rev-author {
            font-weight: 700;
            font-size: 0.9rem;
            color: #0f172a;
        }
        .rev-stars { color: #f59e0b; margin-left: 8px; }
        .rev-comment { color: #475569; font-size: 0.88rem; margin-top: 6px; }

        @media (max-width: 768px) {
            .product-layout { grid-template-columns: 1fr; }
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
    <div id="productDetail" class="product-layout">
        <p style="color:#64748b; grid-column:1/-1;">Loading product details...</p>
    </div>

    <!-- Reviews Section -->
    <div class="reviews-section">
        <div class="rev-header">
            <div>
                <h2 style="font-size:1.4rem; font-weight:800; color:#0f172a;">Customer Reviews &amp; Ratings ⭐</h2>
                <div id="reviewsSummary" style="color:#64748b; font-size:0.9rem; margin-top:4px;"></div>
            </div>
        </div>

        <div id="reviewsList"></div>

        <!-- Write a Review Form -->
        <div class="card" style="margin-top:24px; background:#f8fafc; border:1px solid #cbd5e1;">
            <h3 style="font-size:1.05rem; font-weight:700; color:#0f172a; margin-bottom:4px;">Write a Verified Review</h3>
            <p style="font-size:0.82rem; color:#64748b; margin-bottom:14px;">Only customers who purchased this product and received delivery can submit a review.</p>
            <form id="reviewForm">
                <label for="rating">Rating</label>
                <select id="rating" style="max-width:240px; margin-bottom:12px;">
                    <option value="5">⭐⭐⭐⭐⭐ (5 - Excellent)</option>
                    <option value="4">⭐⭐⭐⭐ (4 - Good)</option>
                    <option value="3">⭐⭐⭐ (3 - Average)</option>
                    <option value="2">⭐⭐ (2 - Below Average)</option>
                    <option value="1">⭐ (1 - Poor)</option>
                </select>

                <label for="comment">Your Feedback</label>
                <textarea id="comment" rows="3" placeholder="Share your experience with this product..." style="margin-bottom:12px;"></textarea>

                <button type="submit" class="btn" id="revSubmitBtn">Submit Review</button>
            </form>
            <p id="revMsg" style="margin-top:10px; font-weight:700; font-size:0.88rem;"></p>
        </div>
    </div>
</div>

<footer>
    <p>&copy; 2026 <span>Arthy Mart</span> &middot; Full Stack E-Commerce Platform</p>
</footer>

<script src="${pageContext.request.contextPath}/js/chat-widget.js"></script>
<script>
const contextPath = '${pageContext.request.contextPath}';
const id = new URLSearchParams(window.location.search).get('id');

async function load() {
    if (!id) {
        document.getElementById('productDetail').innerHTML = '<p style="color:#dc2626;">No product selected.</p>';
        return;
    }
    try {
        const res = await fetch(contextPath + '/api/products/' + id);
        if (!res.ok) {
            document.getElementById('productDetail').innerHTML = '<p style="color:#dc2626;">Product not found.</p>';
            return;
        }
        const p = await res.json();
        const d = document.getElementById('productDetail');
        const img = p.imageUrl ? p.imageUrl : 'https://placehold.co/400x300?text=Product';
        const rawStock = (p.stockQty !== undefined && p.stockQty !== null) ? p.stockQty : p.stock_qty;
        const stock = (rawStock !== undefined && rawStock !== null) ? parseInt(rawStock, 10) : 0;
        const inStock = !isNaN(stock) && stock > 0;
        const safeName = escapeHtml(p.name || 'Product');
        const safeDesc = escapeHtml(p.description || 'No description provided for this product.');
        const safeCat = escapeHtml(p.category || 'General');
        const priceStr = Number(p.price || 0).toFixed(2);

        d.innerHTML =
            '<div class="gallery-wrap">' +
                '<img src="' + img + '" class="main-img" alt="' + safeName + '">' +
            '</div>' +
            '<div class="info-wrap">' +
                '<div class="detail-cat">' + safeCat + '</div>' +
                '<h1 class="detail-title">' + safeName + '</h1>' +
                '<div class="detail-price">$' + priceStr + '</div>' +
                '<div class="detail-stock ' + (inStock ? 'stock-available' : 'stock-none') + '">' +
                    (inStock ? '✓ In Stock (' + stock + ' units available)' : '✗ Out of Stock') +
                '</div>' +
                '<p class="detail-desc">' + safeDesc + '</p>' +
                '<div class="btn-group">' +
                    '<button class="btn-add-cart" onclick="addToCart(' + p.id + ')" ' + (inStock ? '' : 'disabled') + '>' +
                        (inStock ? '🛒 Add to Cart' : 'Currently Unavailable') +
                    '</button>' +
                    '<button class="btn-add-wish" onclick="addToWishlist(' + p.id + ')">❤️ Save to Wishlist</button>' +
                '</div>' +
            '</div>';

        // Load Reviews
        loadReviews();

    } catch (e) {
        document.getElementById('productDetail').innerHTML = '<p style="color:#dc2626;">Error loading product.</p>';
    }
}

async function loadReviews() {
    try {
        const rr = await fetch(contextPath + '/api/reviews?productId=' + id);
        const revData = await rr.json();
        const sum = document.getElementById('reviewsSummary');
        const rlist = document.getElementById('reviewsList');
        rlist.innerHTML = '';
        const reviews = revData.reviews || [];
        const avg = revData.averageRating || 0;

        if (reviews.length === 0) {
            sum.textContent = 'No reviews yet for this product.';
            rlist.innerHTML = '<p style="color:#64748b; font-size:0.88rem;">Be the first verified customer to share your thoughts!</p>';
        } else {
            sum.textContent = 'Average Rating: ' + avg.toFixed(1) + ' / 5 (' + reviews.length + ' ' + (reviews.length === 1 ? 'review' : 'reviews') + ')';
            reviews.forEach(r => {
                const div = document.createElement('div');
                div.className = 'rev-card';
                const stars = '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating);
                div.innerHTML =
                    '<div style="display:flex; align-items:center;">' +
                        '<span class="rev-author">' + escapeHtml(r.userName || 'Verified Buyer') + '</span>' +
                        '<span class="rev-stars">' + stars + '</span>' +
                    '</div>' +
                    '<div class="rev-comment">' + escapeHtml(r.comment || '') + '</div>';
                rlist.appendChild(div);
            });
        }
    } catch (err) {
        document.getElementById('reviewsSummary').textContent = 'Unable to load reviews.';
    }
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

async function addToWishlist(productId) {
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
        alert(data.message || 'Saved to wishlist!');
    } catch (e) {
        alert('Network error.');
    }
}

document.getElementById('reviewForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const msgEl = document.getElementById('revMsg');
    msgEl.textContent = 'Submitting review...';
    msgEl.style.color = '#1565c0';
    try {
        const res = await fetch(contextPath + '/api/reviews', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                productId: parseInt(id),
                rating: parseInt(document.getElementById('rating').value),
                comment: document.getElementById('comment').value
            })
        });
        const respData = await res.json();
        if (res.ok) {
            msgEl.textContent = '✅ Review submitted successfully!';
            msgEl.style.color = '#16a34a';
            document.getElementById('comment').value = '';
            loadReviews();
        } else {
            msgEl.textContent = '❌ ' + (respData.message || respData.error || 'Failed to submit review.');
            msgEl.style.color = '#dc2626';
        }
    } catch (err) {
        msgEl.textContent = '⚠️ Network error submitting review.';
        msgEl.style.color = '#dc2626';
    }
});

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

load();
</script>
</body>
</html>
