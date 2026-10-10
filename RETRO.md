# Sprint Retrospective Log (RETRO.md)

| Sprint / Week | Window | What Worked | What Didn't | One Change for Next Sprint |
|:---|:---|:---|:---|:---|
| **Kickoff** | Jul 24 – Jul 27 | Architecture package skeleton locked (`controller`, `dao`, `model`, `service`, `util`). | Schema draft lacked foreign key index performance tuning. | Standardized DECIMAL(10,2) and created_at timestamps across all tables. |
| **Week 1** | Jul 27 – Aug 2 | jBCrypt password hashing and `AuthFilter` session verification working cleanly. | Handling unauthenticated AJAX requests gracefully without full page redirect. | Return 401 JSON envelopes for `/api/*` endpoints instead of HTML redirect. |
| **Week 2** | Aug 3 – Aug 9 | Core shopping flow (browse $\rightarrow$ cart $\rightarrow$ checkout) established with embedded H2. | Stock decrement concurrency on edge cases. | Enforced database transactional checks during checkout. |
| **Week 3** | Aug 10 – Aug 16 | MVP Review passed; DTO layer separated from entity domain models. | Exposing sensitive user fields in responses. | Introduced strict `UserResponseDTO` omitting password hash. |
| **Week 4** | Aug 17 – Aug 23 | Seller dashboard and Admin platform control center operational. | Order viewing logic needed strict role separation. | Scoped seller queries so sellers only see orders containing their listings. |
| **Week 5** | Aug 24 – Aug 30 | Keyword search and category filtering refined. | Order status transition validation needed server enforcement. | Restricted buyer order cancellation to `PENDING` status only. |
| **Week 6** | Aug 31 – Sep 6 | Verified review system (F8) and Wishlist (O1) added. | Duplicate reviews possible without unique constraint. | Added database UNIQUE index on `(user_id, product_id)` in reviews. |
| **Week 7** | Sep 7 – Sep 13 | Security hardening complete: PreparedStatement audit and Checkstyle/SpotBugs in CI. | Minor left-curly formatting and unused imports. | Configured Checkstyle and SpotBugs rules with zero allowable warnings. |
| **Week 8** | Sep 14 – Sep 20 | Tomcat 9 local deployment verified; health check endpoint added (`/api/health`). | Session persistence across server restarts. | Added H2 server/in-memory configuration fallback in listener. |
| **Week 9** | Sep 21 – Sep 27 | AI Chatbot backend service created with modular provider interface. | Handling external network timeouts without crashing chat UI. | Implemented degraded fallback and local catalog keyword matching. |
| **Week 10** | Sep 28 – Oct 4 | Chat widget UI embedded with product cards and quick suggestion pills. | Duplicate script inclusion on JSP pages. | Modularized chat widget into `chat-widget.js` self-contained IIFE. |
| **Week 11** | Oct 5 – Oct 10 | Full regression suite green (60 tests passed); cart session cookie credentials fixed. | Browser fetch was omitting session cookie on cart endpoint. | Added `credentials: 'same-origin'` in fetch calls; deployment live. |
