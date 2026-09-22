<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Orders - ArthyMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<header><h1>ARTHY MART</h1><nav><a href="index.jsp">Home</a> <a href="products.jsp">Products</a></nav></header>
<div class="container">
<h2>Order History (F6)</h2>
<label><input type="checkbox" id="sellerView"> Seller view (incoming orders for my products)</label>
<div id="orders"></div>
</div>
<script>
async function load() {
  const seller = document.getElementById('sellerView').checked ? '?view=seller' : '';
  const res = await fetch('api/orders' + seller);
  const box = document.getElementById('orders'); box.innerHTML = '';
  if (!res.ok) { box.textContent = 'Please login.'; return; }
  const orders = await res.json();
  orders.forEach(o => {
    const div = document.createElement('div'); div.className = 'card';
    const t = document.createElement('p'); t.textContent = 'Order #' + o.id + ' | $' + o.totalAmount + ' | ' + o.status + ' | ' + (o.createdAt || '');
    const sel = document.createElement('select');
    ['PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED'].forEach(s => {
      const op = document.createElement('option'); op.value = s; op.textContent = s; if (o.status === s) op.selected = true; sel.appendChild(op);
    });
    const btn = document.createElement('button'); btn.textContent = 'Update status';
    btn.onclick = async () => {
      const r = await fetch('api/orders/' + o.id, {method:'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify({status: sel.value})});
      alert((await r.json()).message || 'done'); load();
    };
    div.append(t, sel, btn); box.appendChild(div);
  });
}
document.getElementById('sellerView').addEventListener('change', load);
load();
</script>
</body>
</html>
