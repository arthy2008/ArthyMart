<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Register - ArthyMart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<header><h1>ARTHY MART</h1><nav><a href="index.jsp">Home</a> <a href="login.jsp">Login</a></nav></header>
<div class="container">
<h2>Register</h2>
<form id="regForm">
<label>Name <input type="text" id="name" required maxlength="100"></label>
<label>Email <input type="email" id="email" required></label>
<label>Password (min 8 chars) <input type="password" id="password" required minlength="8"></label>
<label>Role <select id="role"><option value="BUYER">Buyer</option><option value="SELLER">Seller</option></select></label>
<button type="submit" class="btn">Register</button>
</form>
<p id="msg"></p>
</div>
<script>
document.getElementById('regForm').addEventListener('submit', async (e) => {
  e.preventDefault();
  const res = await fetch('api/auth/register', {method:'POST', headers:{'Content-Type':'application/json'},
    body: JSON.stringify({name: document.getElementById('name').value, email: document.getElementById('email').value,
      password: document.getElementById('password').value, role: document.getElementById('role').value})});
  const data = await res.json();
  document.getElementById('msg').textContent = data.message || data.status;
  if (res.ok) { window.location.href = 'login.jsp'; }
});
</script>
</body>
</html>
