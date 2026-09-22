<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Admin - ArthyMart</title><link rel="stylesheet" href="../css/style.css"></head>
<body>
<header><h1>ARTHY MART - Admin</h1><nav><a href="../index.jsp">Home</a></nav></header>
<div class="container">
<h2>Admin Panel (F7)</h2>
<h3>System Information</h3><div id="sysinfo"><p class="muted">Loading...</p></div>
<h3>Users</h3>
<p>
<button onclick="loadUsers('')">All</button>
<button onclick="loadUsers('BUYER')">Buyers</button>
<button onclick="loadUsers('SELLER')">Sellers</button>
<button onclick="loadUsers('ADMIN')">Admins</button>
</p>
<div id="users"></div>
<h3>Orders</h3><div id="orders"></div>
<h3>Listings</h3><div id="products"></div>
</div>
<script>
async function loadUsers(role) {
  let r = await fetch('../api/admin/users' + (role ? '?role=' + role : ''));
  const u = document.getElementById('users'); u.innerHTML = '';
  if (!r.ok) { u.textContent = 'Admin login required.'; return; }
  (await r.json()).forEach(x => {
    const p = document.createElement('p'); p.textContent = '#' + x.id + ' ' + x.name + ' ' + x.email + ' [' + x.role + ']';
    u.appendChild(p);
  });
}
async function load() {
  try {
    const h = await fetch('../api/health');
    const hj = await h.json();
    document.getElementById('sysinfo').textContent = 'Service: ' + (hj.status || '?') + ' | Database: ' + (hj.db || '?');
  } catch (e) {
    document.getElementById('sysinfo').textContent = 'System status unavailable.';
  }
  await loadUsers('');
  r = await fetch('../api/admin/orders');
  const o = document.getElementById('orders'); o.innerHTML = '';
  (await r.json()).forEach(x => {
    const p = document.createElement('p'); p.textContent = 'Order #' + x.id + ' buyer ' + x.buyerId + ' $' + x.totalAmount + ' ' + x.status;
    o.appendChild(p);
  });
  r = await fetch('../api/admin/products');
  const pr = document.getElementById('products'); pr.innerHTML = '';
  (await r.json()).forEach(x => {
    const div = document.createElement('div');
    const p = document.createElement('span'); p.textContent = '#' + x.id + ' ' + x.name + ' $' + x.price + ' ';
    const b = document.createElement('button'); b.textContent = 'Remove listing';
    b.onclick = async () => { if (confirm('Remove?')) { await fetch('../api/admin/products/' + x.id, {method:'DELETE'}); load(); } };
    div.append(p, b); pr.appendChild(div);
  });
}
load();
</script>
</body>
</html>
