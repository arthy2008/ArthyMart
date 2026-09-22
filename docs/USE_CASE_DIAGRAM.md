# Use Case Diagram (ARTHY MART)

```mermaid
flowchart LR
    Buyer([Buyer])
    Seller([Seller])
    Admin([Admin])
    Guest([Guest / Unauthenticated])

    Guest -->|register, login, browse products| System[(ArthyMart)]

    subgraph BuyerUC [Buyer]
        B1[Browse / search / filter products]
        B2[View product details & reviews]
        B3[Manage cart: add / update / remove]
        B4[Checkout with mock payment]
        B5[View order history & details]
        B6[Cancel own pending order]
        B7[Review purchased products]
    end

    subgraph SellerUC [Seller]
        S1[Create product]
        S2[Edit own product]
        S3[Delete own product]
        S4[View own products]
        S5[View incoming orders for own products]
        S6[Update order status for own orders]
    end

    subgraph AdminUC [Admin]
        A1[View all users]
        A2[View all orders]
        A3[View all listings]
        A4[Remove inappropriate listings]
        A5[Remove users]
        A6[Update any order status]
    end

    Buyer --> B1 & B2 & B3 & B4 & B5 & B6 & B7
    Seller --> S1 & S2 & S3 & S4 & S5 & S6
    Admin --> A1 & A2 & A3 & A4 & A5 & A6

    B1 & B2 & S4 -.-> System
```

## Authorization matrix
| Endpoint | Guest | Buyer | Seller | Admin |
|---|---|---|---|---|
| POST /api/auth/register (BUYER/SELLER) | yes | yes | yes | yes |
| POST /api/auth/register (ADMIN) | no | no | no | yes |
| GET /api/products | yes | yes | yes | yes |
| POST/PUT/DELETE /api/products | no | no | own only | yes (moderate) |
| /api/cart, POST /api/orders, /api/orders/checkout | no | yes | no | no* |
| GET /api/orders (own) | no | yes | yes (own buyer orders + ?view=seller) | yes |
| PUT /api/orders/{id} | no | cancel own pending | own-product orders | any |
| POST /api/reviews | no | purchased only | no | no |
| /api/admin/* | no | no | no | yes |

\* Admin can place orders only if explicitly acting as buyer; sellers use buyer accounts for purchases in tests.
