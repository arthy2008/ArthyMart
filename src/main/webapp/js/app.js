// ArthyMart shared helpers (vanilla JS + fetch).
async function api(path, options) {
  const res = await fetch(path, options);
  let data = null;
  try { data = await res.json(); } catch (e) { data = null; }
  return { ok: res.ok, status: res.status, data };
}

async function logout() {
  await fetch('api/auth/logout', { method: 'POST' });
  window.location.href = 'index.jsp';
}
