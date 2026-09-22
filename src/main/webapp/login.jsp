<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Arthy Mart</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            min-height: 100vh;
            display: flex;
            font-family: 'Segoe UI', Arial, sans-serif;
            background: #0d47a1;
        }

        /* ═══════════════════════════
           LEFT PANEL — Blue + Art
        ═══════════════════════════ */
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

        /* Floating blobs */
        .blob {
            position: absolute;
            border-radius: 50%;
            opacity: 0.12;
            animation: drift 8s ease-in-out infinite;
        }
        .blob-1 { width: 320px; height: 320px; background: #82b1ff; top: -80px; left: -80px; animation-delay: 0s; }
        .blob-2 { width: 240px; height: 240px; background: #40c4ff; bottom: -60px; right: -60px; animation-delay: 3s; }
        .blob-3 { width: 160px; height: 160px; background: #b3e5fc; top: 50%; left: -40px; animation-delay: 1.5s; }
        .blob-4 { width: 120px; height: 120px; background: #e3f2fd; bottom: 20%; right: 10%; animation-delay: 4s; }

        @keyframes drift {
            0%, 100% { transform: translate(0, 0) scale(1); }
            33%       { transform: translate(15px, -20px) scale(1.05); }
            66%       { transform: translate(-10px, 15px) scale(0.95); }
        }

        /* Brand */
        .brand-block {
            text-align: center;
            position: relative;
            z-index: 2;
            margin-bottom: 40px;
        }

        .cart-icon {
            width: 90px;
            height: 90px;
            background: rgba(255,255,255,0.15);
            border: 2px solid rgba(255,255,255,0.3);
            border-radius: 24px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 42px;
            margin-bottom: 18px;
            backdrop-filter: blur(8px);
            box-shadow: 0 12px 40px rgba(0,0,0,0.2);
            animation: float 4s ease-in-out infinite;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0); }
            50%       { transform: translateY(-10px); }
        }

        .brand-name {
            font-size: 2.6rem;
            font-weight: 900;
            color: #ffffff;
            letter-spacing: 3px;
        }

        .brand-name em {
            font-style: normal;
            color: #80d8ff;
        }

        .brand-sub {
            color: rgba(255,255,255,0.65);
            font-size: 0.9rem;
            margin-top: 6px;
            letter-spacing: 1.5px;
            text-transform: uppercase;
        }

        /* Illustration grid */
        .illustration {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr;
            gap: 14px;
            width: 100%;
            max-width: 380px;
            position: relative;
            z-index: 2;
            margin-bottom: 36px;
        }

        .ill-card {
            background: rgba(255,255,255,0.1);
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 14px;
            padding: 16px 10px;
            text-align: center;
            backdrop-filter: blur(6px);
            transition: transform 0.3s, background 0.3s;
            cursor: default;
        }

        .ill-card:hover {
            transform: translateY(-5px);
            background: rgba(255,255,255,0.18);
        }

        .ill-card .icon { font-size: 2rem; display: block; margin-bottom: 6px; }
        .ill-card .label { font-size: 0.72rem; color: rgba(255,255,255,0.8); font-weight: 600; letter-spacing: 0.5px; }

        /* Thought / quote slider */
        .quote-box {
            position: relative;
            z-index: 2;
            max-width: 380px;
            width: 100%;
            background: rgba(255,255,255,0.1);
            border-left: 4px solid #80d8ff;
            border-radius: 0 12px 12px 0;
            padding: 18px 20px;
            backdrop-filter: blur(6px);
        }

        .quote-text {
            color: #ffffff;
            font-size: 0.95rem;
            font-style: italic;
            line-height: 1.7;
            min-height: 60px;
            transition: opacity 0.5s;
        }

        .quote-author {
            color: #80d8ff;
            font-size: 0.8rem;
            font-weight: 700;
            margin-top: 8px;
            letter-spacing: 0.5px;
        }

        /* Dots */
        .quote-dots {
            display: flex;
            gap: 6px;
            margin-top: 14px;
        }

        .dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: rgba(255,255,255,0.3);
            transition: background 0.3s, transform 0.3s;
            cursor: pointer;
        }

        .dot.active {
            background: #80d8ff;
            transform: scale(1.3);
        }

        /* Feature badges */
        .features {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            justify-content: center;
            margin-top: 24px;
            position: relative;
            z-index: 2;
        }

        .feature-badge {
            background: rgba(255,255,255,0.12);
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 99px;
            padding: 5px 14px;
            font-size: 0.75rem;
            color: rgba(255,255,255,0.85);
            font-weight: 600;
        }

        /* ═══════════════════════════
           RIGHT PANEL — Login Form
        ═══════════════════════════ */
        .right-panel {
            width: 440px;
            min-width: 380px;
            background: #ffffff;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 48px 44px;
        }

        .form-header {
            width: 100%;
            margin-bottom: 30px;
        }

        .form-header h2 {
            font-size: 1.7rem;
            font-weight: 800;
            color: #0d47a1;
        }

        .form-header p {
            color: #78909c;
            font-size: 0.9rem;
            margin-top: 4px;
        }

        /* Fields */
        .field { margin-bottom: 20px; width: 100%; }

        .field label {
            display: block;
            font-size: 0.82rem;
            font-weight: 700;
            color: #37474f;
            margin-bottom: 7px;
            letter-spacing: 0.4px;
        }

        .input-wrap { position: relative; }

        .input-icon {
            position: absolute;
            left: 13px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 1rem;
            pointer-events: none;
        }

        .field input {
            width: 100%;
            padding: 13px 13px 13px 42px;
            border: 1.8px solid #e0e0e0;
            border-radius: 10px;
            font-size: 0.95rem;
            color: #263238;
            background: #f9fbff;
            outline: none;
            transition: border-color 0.25s, box-shadow 0.25s;
        }

        .field input:focus {
            border-color: #1565c0;
            box-shadow: 0 0 0 3px rgba(21,101,192,0.12);
            background: #fff;
        }

        .toggle-pw {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            font-size: 1rem;
            cursor: pointer;
            color: #90a4ae;
        }
        .toggle-pw:hover { color: #1565c0; }

        /* Alert */
        .alert {
            display: none;
            padding: 11px 14px;
            border-radius: 8px;
            font-size: 0.85rem;
            font-weight: 600;
            margin-bottom: 16px;
            width: 100%;
        }
        .alert.error   { background: #ffebee; border: 1px solid #ef9a9a; color: #c62828; }
        .alert.success { background: #e8f5e9; border: 1px solid #a5d6a7; color: #2e7d32; }

        /* Button */
        .btn-login {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #1565c0, #0288d1);
            color: #fff;
            font-size: 1rem;
            font-weight: 700;
            border: none;
            border-radius: 10px;
            cursor: pointer;
            letter-spacing: 0.5px;
            box-shadow: 0 4px 18px rgba(13,71,161,0.35);
            transition: opacity 0.2s, transform 0.15s;
        }
        .btn-login:hover  { opacity: 0.93; transform: translateY(-1px); }
        .btn-login:active { transform: translateY(0); }
        .btn-login:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }

        /* Spinner */
        .spinner {
            display: inline-block;
            width: 15px; height: 15px;
            border: 2px solid rgba(255,255,255,0.4);
            border-top-color: #fff;
            border-radius: 50%;
            animation: spin 0.7s linear infinite;
            vertical-align: middle;
            margin-right: 7px;
        }
        @keyframes spin { to { transform: rotate(360deg); } }

        /* Divider */
        .divider {
            display: flex;
            align-items: center;
            gap: 10px;
            margin: 22px 0;
            color: #b0bec5;
            font-size: 0.8rem;
            width: 100%;
        }
        .divider::before, .divider::after {
            content: '';
            flex: 1;
            height: 1px;
            background: #eceff1;
        }

        /* Demo pills */
        .demo-row {
            display: flex;
            flex-direction: column;
            gap: 8px;
            width: 100%;
        }

        .demo-btn {
            width: 100%;
            padding: 10px 14px;
            background: #f5f7ff;
            border: 1.5px solid #e3eaff;
            border-radius: 9px;
            color: #455a64;
            font-size: 0.82rem;
            font-weight: 600;
            cursor: pointer;
            text-align: left;
            display: flex;
            align-items: center;
            gap: 10px;
            transition: background 0.2s, border-color 0.2s;
        }

        .demo-btn:hover { background: #e8efff; border-color: #1565c0; color: #1565c0; }

        .badge {
            font-size: 0.65rem;
            font-weight: 800;
            padding: 2px 8px;
            border-radius: 99px;
            letter-spacing: 0.5px;
        }
        .badge-admin  { background: #0d47a1; color: #fff; }
        .badge-seller { background: #e65100; color: #fff; }
        .badge-buyer  { background: #1b5e20; color: #fff; }

        /* Register link */
        .register-link {
            text-align: center;
            margin-top: 22px;
            font-size: 0.875rem;
            color: #78909c;
            width: 100%;
        }
        .register-link a { color: #1565c0; font-weight: 700; text-decoration: none; }
        .register-link a:hover { text-decoration: underline; }

        /* Responsive */
        @media (max-width: 768px) {
            body { flex-direction: column; }
            .left-panel { padding: 32px 20px; }
            .right-panel { width: 100%; min-width: unset; padding: 32px 24px; }
            .brand-name { font-size: 2rem; }
            .illustration { max-width: 300px; }
        }
    </style>
</head>
<body>

<!-- ══════════ LEFT PANEL ══════════ -->
<div class="left-panel">

    <!-- Background blobs -->
    <div class="blob blob-1"></div>
    <div class="blob blob-2"></div>
    <div class="blob blob-3"></div>
    <div class="blob blob-4"></div>

    <!-- Brand -->
    <div class="brand-block">
        <div class="cart-icon">🛒</div>
        <div class="brand-name">ARTHY <em>MART</em></div>
        <div class="brand-sub">Your Smart Shopping Destination</div>
    </div>

    <!-- Illustration Grid -->
    <div class="illustration">
        <div class="ill-card">
            <span class="icon">📦</span>
            <span class="label">Fast Delivery</span>
        </div>
        <div class="ill-card">
            <span class="icon">🔒</span>
            <span class="label">Secure Pay</span>
        </div>
        <div class="ill-card">
            <span class="icon">⭐</span>
            <span class="label">Top Rated</span>
        </div>
        <div class="ill-card">
            <span class="icon">🏷️</span>
            <span class="label">Best Deals</span>
        </div>
        <div class="ill-card">
            <span class="icon">🔄</span>
            <span class="label">Easy Returns</span>
        </div>
        <div class="ill-card">
            <span class="icon">💬</span>
            <span class="label">24/7 Support</span>
        </div>
    </div>

    <!-- Rotating Quote Box -->
    <div class="quote-box">
        <div class="quote-text" id="quoteText"></div>
        <div class="quote-author" id="quoteAuthor"></div>
        <div class="quote-dots" id="quoteDots"></div>
    </div>

    <!-- Feature badges -->
    <div class="features">
        <span class="feature-badge">🛍️ 1000+ Products</span>
        <span class="feature-badge">🚀 Free Shipping</span>
        <span class="feature-badge">💳 Secure Checkout</span>
    </div>
</div>

<!-- ══════════ RIGHT PANEL ══════════ -->
<div class="right-panel">

    <div class="form-header">
        <h2>Welcome Back! 👋</h2>
        <p>Sign in to continue shopping with Arthy Mart</p>
    </div>

    <div class="alert" id="alert"></div>

    <form id="loginForm" style="width:100%;" novalidate>

        <div class="field">
            <label for="email">Email Address</label>
            <div class="input-wrap">
                <span class="input-icon">✉️</span>
                <input type="email" id="email" placeholder="you@example.com"
                       required autocomplete="email">
            </div>
        </div>

        <div class="field">
            <label for="password">Password</label>
            <div class="input-wrap">
                <span class="input-icon">🔒</span>
                <input type="password" id="password" placeholder="Enter your password"
                       required autocomplete="current-password">
                <button type="button" class="toggle-pw" id="togglePw">👁️</button>
            </div>
        </div>

        <button type="submit" class="btn-login" id="loginBtn">Sign In</button>
    </form>

    <div class="divider">Quick Demo Login</div>

    <div class="demo-row">
        <button class="demo-btn" onclick="fillDemo('admin@arthymart.com','password123')">
            <span class="badge badge-admin">ADMIN</span>
            admin@arthymart.com &nbsp;·&nbsp; password123
        </button>
        <button class="demo-btn" onclick="fillDemo('seller@arthymart.com','password123')">
            <span class="badge badge-seller">SELLER</span>
            seller@arthymart.com &nbsp;·&nbsp; password123
        </button>
        <button class="demo-btn" onclick="fillDemo('buyer@arthymart.com','password123')">
            <span class="badge badge-buyer">BUYER</span>
            buyer@arthymart.com &nbsp;·&nbsp; password123
        </button>
    </div>

    <div class="register-link">
        New to Arthy Mart? <a href="${pageContext.request.contextPath}/register.jsp">Create an account</a>
    </div>
</div>

<script>
    /* ── Quotes ── */
    const quotes = [
        { text: '"Shopping is a bit of a relaxing hobby for me, which is sometimes troubling for my wallet."', author: '— Rebecca Bloomwood' },
        { text: '"The secret of getting ahead is getting started. Log in and start your journey today!"', author: '— Mark Twain (adapted)' },
        { text: '"Happiness is not in money, but in shopping! Find joy in every deal at Arthy Mart."', author: '— Marilyn Monroe (adapted)' },
        { text: '"Buy less. Choose well. Make it last — because quality matters more than quantity."', author: '— Vivienne Westwood' },
        { text: '"Every purchase is a vote for the kind of world you want to live in."', author: '— Anna Lappé' },
        { text: '"Good things come to those who shop wisely. Arthy Mart — where smart meets savings!"', author: '— Arthy Mart' }
    ];

    let current = 0;
    const textEl   = document.getElementById('quoteText');
    const authorEl = document.getElementById('quoteAuthor');
    const dotsEl   = document.getElementById('quoteDots');

    /* Build dots */
    quotes.forEach((_, i) => {
        const d = document.createElement('div');
        d.className = 'dot' + (i === 0 ? ' active' : '');
        d.onclick = () => showQuote(i);
        dotsEl.appendChild(d);
    });

    function showQuote(index) {
        textEl.style.opacity = '0';
        setTimeout(() => {
            current = index;
            textEl.textContent   = quotes[index].text;
            authorEl.textContent = quotes[index].author;
            textEl.style.opacity = '1';
            dotsEl.querySelectorAll('.dot').forEach((d, i) => {
                d.classList.toggle('active', i === index);
            });
        }, 300);
    }

    showQuote(0);
    setInterval(() => showQuote((current + 1) % quotes.length), 4000);

    /* ── Toggle password ── */
    const togglePw = document.getElementById('togglePw');
    const passEl   = document.getElementById('password');
    togglePw.addEventListener('click', () => {
        const isText = passEl.type === 'text';
        passEl.type         = isText ? 'password' : 'text';
        togglePw.textContent = isText ? '👁️' : '🙈';
    });

    /* ── Fill demo ── */
    function fillDemo(email, pass) {
        document.getElementById('email').value    = email;
        document.getElementById('password').value = pass;
    }

    /* ── Alert ── */
    const alertEl = document.getElementById('alert');
    function showAlert(msg, type) {
        alertEl.textContent      = msg;
        alertEl.className        = 'alert ' + type;
        alertEl.style.display    = 'block';
    }

    /* ── Submit ── */
    const btn = document.getElementById('loginBtn');
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        alertEl.style.display = 'none';

        const email    = document.getElementById('email').value.trim();
        const password = passEl.value;

        if (!email || !password) {
            showAlert('⚠️ Please enter your email and password.', 'error');
            return;
        }

        btn.disabled    = true;
        btn.innerHTML   = '<span class="spinner"></span>Signing in…';

        try {
            const res  = await fetch('${pageContext.request.contextPath}/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();

            if (res.ok) {
                showAlert('✅ Login successful! Redirecting…', 'success');
                btn.innerHTML = '✅ Signed In';
                setTimeout(() => {
                    const role = (data.role || '').toUpperCase();
                    if (role === 'ADMIN')       window.location.href = '${pageContext.request.contextPath}/admin/dashboard.jsp';
                    else if (role === 'SELLER') window.location.href = '${pageContext.request.contextPath}/seller/dashboard.jsp';
                    else                        window.location.href = '${pageContext.request.contextPath}/products.jsp';
                }, 900);
            } else {
                showAlert('❌ ' + (data.message || 'Invalid email or password. Please try again.'), 'error');
                btn.disabled  = false;
                btn.innerHTML = 'Sign In';
            }
        } catch (err) {
            showAlert('⚠️ Network error. Please check your connection.', 'error');
            btn.disabled  = false;
            btn.innerHTML = 'Sign In';
        }
    });
</script>
</body>
</html>
