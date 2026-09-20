package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.OrderDAO;
import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.Order;
import com.arthy.arthymart.model.OrderItem;
import com.arthy.arthymart.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
    }

    public OrderService(OrderDAO orderDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
    }

    public Order placeOrder(int buyerId, List<OrderItem> items) throws SQLException, IllegalArgumentException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {
            Optional<Product> productOpt = productDAO.findById(item.getProductId());
            if (productOpt.isEmpty()) {
                throw new IllegalArgumentException("Product ID " + item.getProductId() + " does not exist.");
            }

            Product product = productOpt.get();
            if (product.getStockQty() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            item.setPricePerUnit(product.getPrice());
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setTotalAmount(totalAmount);
        order.setStatus(Order.Status.PENDING);

        return orderDAO.createOrderWithItems(order, items);
    }

    public List<Order> getOrdersByBuyer(int buyerId) throws SQLException {
        return orderDAO.findByBuyerId(buyerId);
    }
}
