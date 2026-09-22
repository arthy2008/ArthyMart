package com.arthy.arthymart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CartItem {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private String productName;
    private String imageUrl;
    private Timestamp createdAt;

    public CartItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public BigDecimal getLineTotal() {
        if (unitPrice == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
