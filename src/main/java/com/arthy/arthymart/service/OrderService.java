package com.arthy.arthymart.service;

import com.arthy.arthymart.dao.CartDAO;
import com.arthy.arthymart.dao.OrderDAO;
import com.arthy.arthymart.dao.ProductDAO;
import com.arthy.arthymart.model.CartItem;
import com.arthy.arthymart.model.Order;
import com.arthy.arthymart.model.OrderItem;
import com.arthy.arthymart.model.Product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderService {

    private final OrderDAO orderDAO;
    private final ProductDAO productDAO;
    private final CartDAO cartDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
        this.cartDAO = new CartDAO();
    }

    public OrderService(OrderDAO orderDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.cartDAO = new CartDAO();
    }

    public OrderService(OrderDAO orderDAO, ProductDAO productDAO, CartDAO cartDAO) {
        this.orderDAO = orderDAO;
        this.productDAO = productDAO;
        this.cartDAO = cartDAO;
    }

    public Order placeOrder(int buyerId, List<OrderItem> items) throws SQLException, IllegalArgumentException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : items) {
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive for product ID " + item.getProductId());
            }
            if (item.getQuantity() > 1000) {
                throw new IllegalArgumentException("Quantity exceeds maximum allowed (1000).");
            }
            Optional<Product> productOpt = productDAO.findById(item.getProductId());
            if (productOpt.isEmpty()) {
                throw new IllegalArgumentException("Product ID " + item.getProductId() + " does not exist.");
            }

            Product product = productOpt.get();
            if (product.getStockQty() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            // Server determines price; never trust client-supplied totals.
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

    /** Checkout all items currently in the buyer's cart (mock payment = always approved). */
    public Order checkoutCart(int buyerId) throws SQLException {
        List<CartItem> cart = cartDAO.findByUserId(buyerId);
        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty.");
        }
        List<OrderItem> items = new ArrayList<>();
        for (CartItem ci : cart) {
            OrderItem oi = new OrderItem();
            oi.setProductId(ci.getProductId());
            oi.setQuantity(ci.getQuantity());
            items.add(oi);
        }
        return placeOrder(buyerId, items);
    }

    public List<Order> getOrdersByBuyer(int buyerId) throws SQLException {
        return orderDAO.findByBuyerId(buyerId);
    }

    public List<Order> getOrdersForSeller(int sellerId) throws SQLException {
        return orderDAO.findBySellerId(sellerId);
    }

    public List<Order> getAllOrders() throws SQLException {
        return orderDAO.findAll();
    }

    public Order getOrderById(int orderId) throws SQLException {
        return orderDAO.findById(orderId);
    }

    public void updateOrderStatus(int orderId, Order.Status newStatus) throws SQLException {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status is required.");
        }
        boolean ok = orderDAO.updateStatus(orderId, newStatus);
        if (!ok) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
    }
}
