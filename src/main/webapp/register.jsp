<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create an Account - Arthy Mart</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            min-height: 100vh;
            display: flex;
            font-family: 'Segoe UI', Arial, sans-serif;
            background: #0d47a1;
        }

        .left-panel {
            flex: 1;
            background: linear-gradient(160deg, #0d47a1 0%, #1565c0 30%, #0277bd 65%, #01579b 100%);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 48px 40px;
            position: relative;
            overflow: hidden;
        }

        .brand-block {
            text-align: center;
            position: relative;
            z-index: 2;
            margin-bottom: 32px;
        }

        .cart-icon {
            width: 86px;
            height: 86px;
            background: rgba(255,255,255,0.15);
            border: 2px solid rgba(255,255,255,0.3);
            border-radius: 22px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 40px;
            margin-bottom: 16px;
            backdrop-filter: blur(8px);
            box-shadow: 0 12px 36px rgba(0,0,0,0.2);
        }

        .brand-name {
            font-size: 2.5rem;
            font-weight: 900;
            color: #ffffff;
            letter-spacing: 2px;
        }

        .brand-name em {
            font-style: normal;
            color: #80d8ff;
        }

        .brand-sub {
            color: rgba(255,255,255,0.7);
            font-size: 0.9rem;
            margin-top: 6px;
            letter-spacing: 1.5px;
            text-transform: uppercase;
        }

        .perks {
            display: flex;
            flex-direction: column;
            gap: 12px;
            width: 100%;
            max-width: 360px;
            position: relative;
            z-index: 2;
        }

        .perk-card {
            background: rgba(255,255,255,0.1);
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 12px;
            padding: 14px 18px;
            display: flex;
            align-items: center;
            gap: 14px;
            backdrop-filter: blur(6px);
            color: #ffffff;
        }

        .perk-card span { font-size: 1.8rem; }
        .perk-card h4 { font-size: 0.92rem; font-weight: 700; }
        .perk-card p { font-size: 0.78rem; opacity: 0.8; }

        .right-panel {
            width: 480px;
            min-width: 400px;
            background: #ffffff;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 48px 44px;
        }

        .form-header {
            width: 100%;
            margin-bottom: 24px;
        }

        .form-header h2 {
            font-size: 1.7rem;
            font-weight: 800;
            color: #0d47a1;
        }

        .form-header p {
            color: #64748b;
            font-size: 0.88rem;
            margin-top: 4px;
        }

        .field { margin-bottom: 18px; width: 100%; }

        .field label {
            display: block;
            font-size: 0.82rem;
            font-weight: 700;
            color: #334155;
            margin-bottom: 6px;
        }

        .field input, .field select {
            width: 100%;
            padding: 12px 14px;
            border: 1.8px solid #cbd5e1;
            border-radius: 9px;
            font-size: 0.95rem;
            color: #0f172a;
            outline: none;
            transition: border-color 0.2s;
        }

        .field input:focus, .field select:focus {
            border-color: #1565c0;
            box-shadow: 0 0 0 3px rgba(21, 101, 192, 0.12);
        }

        .role-selector {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            margin-top: 4px;
        }

        .role-btn {
            border: 2px solid #cbd5e1;
            border-radius: 10px;
            padding: 12px;
            text-align: center;
            background: #f8fafc;
            cursor: pointer;
            transition: all 0.2s;
            font-weight: 700;
            font-size: 0.85rem;
            color: #475569;
        }

        .role-btn.active {
            border-color: #1565c0;
            background: #eff6ff;
            color: #1565c0;
        }

        .alert {
            display: none;
            padding: 11px 14px;
            border-radius: 8px;
            font-size: 0.85rem;
            font-weight: 600;
            margin-bottom: 16px;
            width: 100%;
        }
        .alert.error   { background: #fee2e2; border: 1px solid #fca5a5; color: #dc2626; }
        .alert.success { background: #dcfce7; border: 1px solid #86efac; color: #16a34a; }

        .btn-submit {
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #1565c0, #0288d1);
            color: #fff;
            font-size: 1rem;
            font-weight: 700;
            border: none;
            border-radius: 9px;
            cursor: pointer;
            box-shadow: 0 4px 16px rgba(21, 101, 192, 0.35);
            transition: opacity 0.2s, transform 0.1s;
        }

        .btn-submit:hover { opacity: 0.93; transform: translateY(-1px); }

        .footer-note {
            text-align: center;
            margin-top: 22px;
            font-size: 0.875rem;
            color: #64748b;
        }

        .footer-note a { color: #1565c0; font-weight: 700; text-decoration: none; }
        .footer-note a:hover { text-decoration: underline; }

        @media (max-width: 768px) {
            body { flex-direction: column; }
            .left-panel { padding: 32px 20px; }
            .right-panel { width: 100%; min-width: unset; padding: 32px 24px; }
            .brand-name { font-size: 2rem; }
        }
    </style>
</head>
<body>

<div class="left-panel">
    <div class="brand-block">
        <div class="cart-icon">🛒</div>
        <div class="brand-name">ARTHY <em>MART</em></div>
        <div class="brand-sub">Join Our Shopping Community</div>
    </div>

    <div class="perks">
        <div class="perk-card">
            <span>🛍️</span>
            <div>
                <h4>Shop 1,000+ Products</h4>
                <p>Browse top electronics, books, and home appliances.</p>
            </div>
        </div>
        <div class="perk-card">
            <span>🏪</span>
            <div>
                <h4>Start Selling Today</h4>
                <p>Register as a Seller and manage your product listings.</p>
            </div>
        </div>
        <div class="perk-card">
            <span>🔒</span>
            <div>
                <h4>Safe &amp; Secure</h4>
                <p>Protected transactions, verified reviews, and instant tracking.</p>
            </div>
        </div>
    </div>
</div>

<div class="right-panel">
    <div class="form-header">
        <h2>Create Account 🚀</h2>
        <p>Sign up to start buying or selling with Arthy Mart</p>
    </div>

    <div class="alert" id="alertBox"></div>

    <form id="regForm" style="width:100%;" novalidate>
        <div class="field">
            <label for="name">Full Name</label>
            <input type="text" id="name" placeholder="John Doe" required maxlength="100">
        </div>

        <div class="field">
            <label for="email">Email Address</label>
            <input type="email" id="email" placeholder="you@example.com" required autocomplete="email">
        </div>

        <div class="field">
            <label for="password">Password (Minimum 8 characters)</label>
            <input type="password" id="password" placeholder="••••••••" required minlength="8">
        </div>

        <div class="field">
            <label>Select Your Account Type</label>
            <div class="role-selector">
                <div class="role-btn active" id="btnBuyer" onclick="selectRole('BUYER')">🛒 Buyer</div>
                <div class="role-btn" id="btnSeller" onclick="selectRole('SELLER')">🏪 Seller</div>
            </div>
            <input type="hidden" id="role" value="BUYER">
        </div>

        <button type="submit" class="btn-submit" id="submitBtn">Create Account</button>
    </form>

    <div class="footer-note">
        Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Sign in here</a>
    </div>
</div>

<script>
let selectedRole = 'BUYER';

function selectRole(role) {
    selectedRole = role;
    document.getElementById('role').value = role;
    document.getElementById('btnBuyer').classList.toggle('active', role === 'BUYER');
    document.getElementById('btnSeller').classList.toggle('active', role === 'SELLER');
}

document.getElementById('regForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertBox = document.getElementById('alertBox');
    alertBox.style.display = 'none';

    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const role = document.getElementById('role').value;

    if (!name || !email || !password) {
        showAlert('Please fill in all required fields.', 'error');
        return;
    }

    if (password.length < 8) {
        showAlert('Password must be at least 8 characters long.', 'error');
        return;
    }

    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Creating account...';

    try {
        const res = await fetch('${pageContext.request.contextPath}/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password, role })
        });
        const data = await res.json();

        if (res.ok) {
            showAlert('✅ Account created successfully! Redirecting to login...', 'success');
            setTimeout(() => {
                window.location.href = '${pageContext.request.contextPath}/login.jsp';
            }, 1200);
        } else {
            showAlert(data.message || 'Registration failed. Email might already be in use.', 'error');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Create Account';
        }
    } catch (err) {
        showAlert('Network error. Please try again.', 'error');
        submitBtn.disabled = false;
        submitBtn.textContent = 'Create Account';
    }
});

function showAlert(msg, type) {
    const alertBox = document.getElementById('alertBox');
    alertBox.textContent = msg;
    alertBox.className = 'alert ' + type;
    alertBox.style.display = 'block';
}
</script>

</body>
</html>
