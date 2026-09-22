package com.arthy.arthymart.dto;

import com.arthy.arthymart.model.OrderItem;

import java.math.BigDecimal;

public class OrderItemDTO {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public OrderItemDTO() {}

    public OrderItemDTO(int id, int orderId, int productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = unitPrice != null ? unitPrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
    }

    public static OrderItemDTO fromOrderItem(OrderItem item) {
        if (item == null) return null;
        return new OrderItemDTO(item.getId(), item.getOrderId(), item.getProductId(), item.getQuantity(), item.getPricePerUnit());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
