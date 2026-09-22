<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Product Details - ArthyMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>ARTHY MART</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
        <a href="${pageContext.request.contextPath}/products.jsp">Products</a>
        <a href="${pageContext.request.contextPath}/cart.jsp">Cart</a>
        <a href="${pageContext.request.contextPath}/orders.jsp">Orders</a>
    </nav>
</header>
<div class="container">
    <h2>Product Details</h2>
    <div id="detail"></div>

    <hr style="margin: 24px 0; border:0; border-top:1px solid #ddd;">

    <h3>Customer Reviews (F8)</h3>
    <div id="reviewsSummary" style="margin-bottom: 12px; font-weight: bold;"></div>
    <div id="reviewsList"></div>

    <div style="margin-top: 24px; padding: 16px; background: #fdfdfd; border: 1px solid #eee; border-radius: 6px;">
        <h4>Write a Review</h4>
        <p class="muted" style="font-size: 0.9em; margin-bottom: 12px;">Only verified buyers who completed a purchase of this product can submit a review.</p>
        <form id="reviewForm">
            <label>Rating (1–5 Stars)
                <select id="rating" required style="display:block; margin-top:4px; padding:6px; width:100%; max-width:200px;">
                    <option value="5">⭐⭐⭐⭐⭐ (5 - Excellent)</option>
                    <option value="4">⭐⭐⭐⭐ (4 - Good)</option>
                    <option value="3">⭐⭐⭐ (3 - Average)</option>
                    <option value="2">⭐⭐ (2 - Below Average)</option>
                    <option value="1">⭐ (1 - Poor)</option>
                </select>
            </label>
            <label style="display:block; margin-top:12px;">Comment
                <textarea id="comment" maxlength="2000" rows="3" placeholder="Share your experience..." style="display:block; width:100%; margin-top:4px; padding:6px;"></textarea>
            </label>
            <button type="submit" class="btn" style="margin-top:12px;">Submit Review</button>
        </form>
        <p id="msg" style="margin-top: 10px; font-weight: bold;"></p>
    </div>
</div>
<script>
const id = new URLSearchParams(window.location.search).get('id');

async function load() {
  if (!id) {
    document.getElementById('detail').innerHTML = '<p class="muted">No product selected.</p>';
    return;
  }
  try {
    const res = await fetch('api/products/' + id);
    if (!res.ok) {
      document.getElementById('detail').innerHTML = '<p class="muted">Product not found.</p>';
      return;
    }
    const p = await res.json();
    const d = document.getElementById('detail');
    d.innerHTML = '';

    if (p.imageUrl) {
      const img = document.createElement('img');
      img.src = p.imageUrl;
      img.alt = p.name;
      img.style.maxHeight = '240px';
      img.style.objectFit = 'contain';
      img.style.borderRadius = '4px';
      img.style.marginBottom = '12px';
      d.appendChild(img);
    }

    const h = document.createElement('h3');
    h.textContent = p.name;
    const pr = document.createElement('p');
    pr.innerHTML = '<strong>$' + p.price + '</strong> &bull; Stock: ' + p.stockQty + ' &bull; Category: ' + (p.category || 'General');
    const ds = document.createElement('p');
    ds.textContent = p.description || '';
    const btn = document.createElement('button');
    btn.textContent = 'Add to Cart';
    btn.className = 'btn';
    btn.onclick = async () => {
      const r = await fetch('api/cart', {
        method:'POST',
        headers:{'Content-Type':'application/json'},
        body: JSON.stringify({productId: p.id, quantity: 1})
      });
      const data = await r.json();
      alert(data.message || (r.ok ? 'Added to cart!' : 'Failed to add'));
    };
    d.append(h, pr, ds, btn);

    // Reviews
    const rr = await fetch('api/reviews?productId=' + id);
    const revData = await rr.json();
    const sum = document.getElementById('reviewsSummary');
    const rlist = document.getElementById('reviewsList');
    rlist.innerHTML = '';
    const reviews = revData.reviews || [];
    const avg = revData.averageRating || 0;

    if (reviews.length === 0) {
      sum.textContent = 'No reviews yet for this product.';
    } else {
      sum.textContent = 'Average Rating: ' + avg.toFixed(1) + ' / 5 (' + reviews.length + ' ' + (reviews.length === 1 ? 'review' : 'reviews') + ')';
      reviews.forEach(r => {
        const div = document.createElement('div');
        div.className = 'card';
        const stars = '★'.repeat(r.rating) + '☆'.repeat(5 - r.rating);
        const nameText = r.userName || 'Verified Buyer';
        div.innerHTML = '<p><strong>' + escapeHtml(nameText) + '</strong> &bull; <span style="color:#f59e0b;">' + stars + '</span></p>' +
                        '<p>' + escapeHtml(r.comment || '') + '</p>';
        rlist.appendChild(div);
      });
    }
  } catch (err) {
    document.getElementById('detail').innerHTML = '<p class="muted">Error loading product.</p>';
  }
}

function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}

document.getElementById('reviewForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const msgEl = document.getElementById('msg');
  msgEl.textContent = 'Submitting review...';
  msgEl.style.color = '#555';
  try {
    const res = await fetch('api/reviews', {
      method:'POST',
      headers:{'Content-Type':'application/json'},
      body: JSON.stringify({
        productId: parseInt(id),
        rating: parseInt(document.getElementById('rating').value),
        comment: document.getElementById('comment').value
      })
    });
    const respData = await res.json();
    if (res.ok) {
      msgEl.textContent = 'Review submitted successfully!';
      msgEl.style.color = '#10b981';
      document.getElementById('comment').value = '';
      load();
    } else {
      msgEl.textContent = respData.message || respData.error || 'Failed to submit review.';
      msgEl.style.color = '#ef4444';
    }
  } catch (err) {
    msgEl.textContent = 'Network error submitting review.';
    msgEl.style.color = '#ef4444';
  }
});

load();
</script>
</body>
</html>
