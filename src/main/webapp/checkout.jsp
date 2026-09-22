<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Checkout - ArthyMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<header><h1>ARTHY MART</h1><nav><a href="index.jsp">Home</a> <a href="cart.jsp">Cart</a></nav></header>
<div class="container">
<h2>Checkout (F5 - Mock Payment)</h2>
<p>Server recomputes prices from the database. Mock payment is always approved.</p>
<button id="payBtn" class="btn">Confirm Payment &amp; Place Order</button>
<p id="msg"></p>
</div>
<script>
document.getElementById('payBtn').addEventListener('click', async () => {
  const res = await fetch('api/orders/checkout', {method:'POST'});
  const data = await res.json();
  document.getElementById('msg').textContent = res.ok ? 'Order #' + data.id + ' placed. Total $' + data.totalAmount : (data.message || 'failed');
  if (res.ok) setTimeout(() => window.location.href = 'orders.jsp', 1200);
});
</script>
</body>
</html>
