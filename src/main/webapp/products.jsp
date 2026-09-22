<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products - ArthyMart</title>
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
        <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
    </nav>
</header>
<div class="container">
    <h2>Browse Products (F3)</h2>
    <form id="searchForm" style="display:flex; gap:8px; flex-wrap:wrap; margin-bottom:20px;">
        <input type="text" id="q" placeholder="Search name or description" style="flex:1; min-width:200px;">
        <input type="text" id="category" placeholder="Category (e.g. Electronics, Books)" style="min-width:180px;">
        <button type="submit" class="btn">Search</button>
        <button type="button" id="resetBtn" class="btn secondary">Clear</button>
    </form>
    <div id="grid" class="grid"></div>
</div>
<script>
async function load(params='') {
  const grid = document.getElementById('grid');
  grid.innerHTML = '<p class="muted">Loading products...</p>';
  try {
    const res = await fetch('api/products' + params);
    if (!res.ok) {
      grid.innerHTML = '<p class="muted">Failed to load products.</p>';
      return;
    }
    const products = await res.json();
    grid.innerHTML = '';
    if (!products || products.length === 0) {
      grid.innerHTML = '<p class="muted">No products found matching your criteria. <a href="javascript:void(0)" onclick="clearFilters()">View all products</a>.</p>';
      return;
    }
    products.forEach(p => {
      const div = document.createElement('div');
      div.className = 'card';
      if (p.imageUrl) {
        const img = document.createElement('img');
        img.src = p.imageUrl;
        img.alt = p.name;
        img.style.width = '100%';
        img.style.height = '160px';
        img.style.objectFit = 'cover';
        img.style.borderRadius = '4px';
        img.style.marginBottom = '8px';
        div.appendChild(img);
      }
      const name = document.createElement('h3');
      name.textContent = p.name;
      const price = document.createElement('p');
      price.innerHTML = '<strong>$' + p.price + '</strong> &bull; Stock: ' + p.stockQty + ' &bull; <em>' + (p.category || 'General') + '</em>';
      const desc = document.createElement('p');
      desc.textContent = p.description || '';
      const actions = document.createElement('div');
      actions.style.display = 'flex';
      actions.style.gap = '8px';
      actions.style.marginTop = '12px';
      const link = document.createElement('a');
      link.href = 'product-detail.jsp?id=' + p.id;
      link.textContent = 'View Details';
      link.className = 'btn secondary';
      const btn = document.createElement('button');
      btn.textContent = 'Add to Cart';
      btn.className = 'btn';
      btn.onclick = async () => {
        const r = await fetch('api/cart', {
          method:'POST',
          headers:{'Content-Type':'application/json'},
          body: JSON.stringify({productId: p.id, quantity: 1})
        });
        const d = await r.json();
        alert(d.message || (r.ok ? 'Added to cart!' : 'Failed to add'));
      };
      actions.append(link, btn);
      div.append(name, price, desc, actions);
      grid.appendChild(div);
    });
  } catch (err) {
    grid.innerHTML = '<p class="muted">Network error loading products.</p>';
  }
}

function clearFilters() {
  document.getElementById('q').value = '';
  document.getElementById('category').value = '';
  load();
}

document.getElementById('resetBtn').addEventListener('click', clearFilters);

document.getElementById('searchForm').addEventListener('submit', (e) => {
  e.preventDefault();
  const qVal = document.getElementById('q').value.trim();
  const cVal = document.getElementById('category').value.trim();
  const params = new URLSearchParams();
  if (qVal) params.set('q', qVal);
  if (cVal) params.set('category', cVal);
  const qs = params.toString();
  load(qs ? '?' + qs : '');
});

// Honor deep links such as products.jsp?category=Electronics from the home page.
(function initFromUrl() {
  const urlParams = new URLSearchParams(window.location.search);
  const qParam = urlParams.get('q');
  const cParam = urlParams.get('category');
  if (qParam) document.getElementById('q').value = qParam;
  if (cParam) document.getElementById('category').value = cParam;
  const qs = urlParams.toString();
  if (qs) load('?' + qs);
})();
</script>
</body>
</html>
