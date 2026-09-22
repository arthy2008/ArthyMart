<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Seller Dashboard - ArthyMart</title><link rel="stylesheet" href="../css/style.css"></head>
<body>
<header><h1>ARTHY MART - Seller</h1><nav><a href="../index.jsp">Home</a> <a href="../orders.jsp">Orders</a></nav></header>
<div class="container">
<h2>My Products (F2)</h2>
<form id="pForm">
<input type="hidden" id="pid">
<label>Name <input type="text" id="name" required maxlength="150"></label>
<label>Description <textarea id="desc"></textarea></label>
<label>Price <input type="number" id="price" step="0.01" min="0.01" required></label>
<label>Stock <input type="number" id="stock" min="0" required></label>
<label>Category <input type="text" id="cat" required></label>
<label>Image URL <input type="text" id="img" placeholder="https://..."></label>
<button type="submit" class="btn">Save Product</button>
<button type="button" id="resetBtn">Reset</button>
</form>
<div id="list"></div>
</div>
<script>
async function load() {
  const res = await fetch('../api/products?mine=true');
  const box = document.getElementById('list'); box.innerHTML = '';
  if (!res.ok) { box.textContent = 'Seller login required.'; return; }
  (await res.json()).forEach(p => {
    const div = document.createElement('div'); div.className = 'card';
    const t = document.createElement('p'); t.textContent = '#' + p.id + ' ' + p.name + ' $' + p.price + ' stock ' + p.stockQty;
    const e = document.createElement('button'); e.textContent = 'Edit';
    e.onclick = () => { document.getElementById('pid').value = p.id; document.getElementById('name').value = p.name;
      document.getElementById('desc').value = p.description || ''; document.getElementById('price').value = p.price;
      document.getElementById('stock').value = p.stockQty; document.getElementById('cat').value = p.category || '';
      document.getElementById('img').value = p.imageUrl || ''; };
    const d = document.createElement('button'); d.textContent = 'Delete';
    d.onclick = async () => { if (confirm('Delete?')) { await fetch('../api/products/' + p.id, {method:'DELETE'}); load(); } };
    div.append(t, e, d); box.appendChild(div);
  });
}
document.getElementById('pForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const body = {name: document.getElementById('name').value, description: document.getElementById('desc').value,
    price: document.getElementById('price').value, stockQty: parseInt(document.getElementById('stock').value),
    category: document.getElementById('cat').value, imageUrl: document.getElementById('img').value};
  const pid = document.getElementById('pid').value;
  const res = pid ? await fetch('../api/products/' + pid, {method:'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)})
                  : await fetch('../api/products', {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)});
  alert((await res.json()).message || (res.ok ? 'saved' : 'failed'));
  if (res.ok) { document.getElementById('pForm').reset(); document.getElementById('pid').value=''; load(); }
});
document.getElementById('resetBtn').onclick = () => { document.getElementById('pForm').reset(); document.getElementById('pid').value=''; };
load();
</script>
</body>
</html>
