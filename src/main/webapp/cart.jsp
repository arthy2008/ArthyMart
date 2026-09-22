<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cart - ArthyMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <h1>ARTHY MART</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/index.jsp">Home</a>
        <a href="${pageContext.request.contextPath}/products.jsp">Products</a>
        <a href="${pageContext.request.contextPath}/orders.jsp">Orders</a>
    </nav>
</header>
<div class="container">
    <h2>Your Cart (F4)</h2>
    <div id="cart"></div>
    <p><strong>Total: $<span id="total">0.00</span></strong></p>
    <p id="checkoutSection">
        <a id="checkoutBtn" href="${pageContext.request.contextPath}/checkout.jsp" class="btn">Proceed to Checkout</a>
    </p>
</div>
<script>
async function load() {
  const res = await fetch('api/cart');
  const box = document.getElementById('cart');
  const checkoutBtn = document.getElementById('checkoutBtn');
  if (!res.ok) {
    box.innerHTML = '<p class="muted">Please <a href="login.jsp">login</a> to view your cart.</p>';
    if (checkoutBtn) checkoutBtn.style.display = 'none';
    return;
  }
  const data = await res.json();
  box.innerHTML = '';
  const items = data.items || [];
  if (items.length === 0) {
    box.innerHTML = '<p class="muted">Your cart is empty. <a href="products.jsp">Browse products</a> to get started.</p>';
    if (checkoutBtn) checkoutBtn.style.display = 'none';
    document.getElementById('total').textContent = '0.00';
    return;
  }
  if (checkoutBtn) checkoutBtn.style.display = 'inline-block';
  items.forEach(it => {
    const div = document.createElement('div');
    div.className = 'card';
    const t = document.createElement('p');
    t.textContent = (it.productName || ('Product #' + it.productId)) + ' x ' + it.quantity + ' @ $' + it.unitPrice;
    const inc = document.createElement('button');
    inc.textContent = '+';
    inc.className = 'btn secondary';
    inc.onclick = () => updateQty(it.productId, it.quantity + 1);
    const dec = document.createElement('button');
    dec.textContent = '-';
    dec.className = 'btn secondary';
    dec.onclick = () => updateQty(it.productId, it.quantity - 1);
    const rm = document.createElement('button');
    rm.textContent = 'Remove';
    rm.className = 'btn';
    rm.onclick = async () => {
      await fetch('api/cart/' + it.productId, {method:'DELETE'});
      load();
    };
    div.append(t, inc, dec, rm);
    box.appendChild(div);
  });
  document.getElementById('total').textContent = data.total != null ? data.total : '0.00';
}
async function updateQty(pid, qty) {
  if (qty <= 0) {
    await fetch('api/cart/' + pid, {method:'DELETE'});
  } else {
    const r = await fetch('api/cart', {
      method:'PUT',
      headers:{'Content-Type':'application/json'},
      body: JSON.stringify({productId: pid, quantity: qty})
    });
    if (!r.ok) {
      const err = await r.json();
      alert(err.message || 'Could not update quantity');
    }
  }
  load();
}
load();
</script>
</body>
</html>
