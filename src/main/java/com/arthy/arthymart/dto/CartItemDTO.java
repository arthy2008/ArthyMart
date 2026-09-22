package com.arthy.arthymart.dto;

import com.arthy.arthymart.model.CartItem;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CartItemDTO {
    private int id;
    private int userId;
    private int productId;
    private String productName;
    private String imageUrl;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal lineTotal;
    private Timestamp createdAt;

    public CartItemDTO() {}

    public CartItemDTO(int id, int userId, int productId, String productName, String imageUrl,
                       BigDecimal unitPrice, int quantity, BigDecimal lineTotal, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
        this.createdAt = createdAt;
    }

    public static CartItemDTO fromCartItem(CartItem ci) {
        if (ci == null) return null;
        return new CartItemDTO(
                ci.getId(),
                ci.getUserId(),
                ci.getProductId(),
                ci.getProductName(),
                ci.getImageUrl(),
                ci.getUnitPrice(),
                ci.getQuantity(),
                ci.getLineTotal(),
                ci.getCreatedAt()
        );
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
