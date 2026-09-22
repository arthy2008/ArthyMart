package com.arthy.arthymart.dto;

import com.arthy.arthymart.model.Order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponseDTO {
    private int id;
    private int buyerId;
    private BigDecimal totalAmount;
    private String status;
    private Timestamp createdAt;
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderResponseDTO() {}

    public OrderResponseDTO(int id, int buyerId, BigDecimal totalAmount, String status, Timestamp createdAt, List<OrderItemDTO> items) {
        this.id = id;
        this.buyerId = buyerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items != null ? items : new ArrayList<>();
    }

    public static OrderResponseDTO fromOrder(Order o) {
        if (o == null) return null;
        List<OrderItemDTO> itemDTOs = o.getItems() != null
                ? o.getItems().stream().map(OrderItemDTO::fromOrderItem).collect(Collectors.toList())
                : new ArrayList<>();
        return new OrderResponseDTO(
                o.getId(),
                o.getBuyerId(),
                o.getTotalAmount(),
                o.getStatus() != null ? o.getStatus().name() : null,
                o.getCreatedAt(),
                itemDTOs
        );
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
