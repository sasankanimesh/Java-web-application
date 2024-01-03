//AddToCartServlet.java
package com.sasanka;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/AddToCartServlet")
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve parameters from the request
        String customerId = request.getParameter("customerId");
        String productId = request.getParameter("productId");

        // Check if customerId and productId are not null or empty
        if (customerId == null || customerId.isEmpty() || productId == null || productId.isEmpty()) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.write("Invalid parameters");
            return;
        }

        Connection connection = null;
        PreparedStatement orderItemStmt = null;

        try {
            // Establish database connection
            connection = DatabaseUtil.getConnection();

            // Check if an open order exists for the customer
            int orderId = getOpenOrderForCustomer(connection, customerId);

            // If no open order exists, create a new order
            if (orderId == -1) {
                orderId = createNewOrder(connection, customerId);
            }

            // Insert or update the selected product into the order_items table
            int quantity = getProductQuantity(connection, orderId, Integer.parseInt(productId));

            if (quantity > 0) {
                // If the product already exists, update the quantity and subtotal
                updateOrderItem(connection, orderId, Integer.parseInt(productId), quantity + 1);
            } else {
                // If the product doesn't exist, insert a new order item
                insertOrderItem(connection, orderId, Integer.parseInt(productId), getProductPrice(connection, productId));
            }

            // Update the total in the orders table
            updateOrderTotal(connection, orderId);

            // Fetch ordered items
            List<OrderItem> orderedItems = getOrderedItems(connection, orderId);

            // Set the ordered items as a request attribute
            request.setAttribute("orderedItems", orderedItems);

            // Forward to the Cart.jsp
            RequestDispatcher dispatcher = request.getRequestDispatcher("Cart.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
        } finally {
            // Close resources
            DatabaseUtil.close(null, orderItemStmt, null);
            DatabaseUtil.close(null, connection);
        }
    }

    private int getOpenOrderForCustomer(Connection connection, String customerId) throws SQLException {
        int orderId = -1;

        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT order_id FROM orders WHERE customer_id = ? AND status = 'PENDING'")) {
            stmt.setString(1, customerId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    orderId = resultSet.getInt("order_id");
                }
            }
        }

        return orderId;
    }

    private int createNewOrder(Connection connection, String customerId) throws SQLException {
        int orderId = -1;

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO orders (customer_id, total, status) VALUES (?, 0, 'PENDING')",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customerId);
            int affectedRows = stmt.executeUpdate();

            // Check if the order was created successfully
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }
        }

        return orderId;
    }

    private double getProductPrice(Connection connection, String productId) throws SQLException {
        double price = 0.0;

        try (PreparedStatement stmt = connection.prepareStatement("SELECT price FROM products WHERE product_id = ?")) {
            stmt.setInt(1, Integer.parseInt(productId));
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    price = resultSet.getDouble("price");
                }
            }
        }

        return price;
    }

    private int getProductQuantity(Connection connection, int orderId, int productId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT quantity FROM order_items WHERE order_id = ? AND product_id = ?")) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("quantity");
                }
            }
        }
        return 0;
    }

    private void updateOrderItem(Connection connection, int orderId, int productId, int newQuantity)
            throws SQLException {
        double price = getProductPrice(connection, Integer.toString(productId));
        double newSubtotal = price * newQuantity;

        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE order_items SET quantity = ?, subtotal = ? WHERE order_id = ? AND product_id = ?")) {
            stmt.setInt(1, newQuantity);
            stmt.setDouble(2, newSubtotal);
            stmt.setInt(3, orderId);
            stmt.setInt(4, productId);
            stmt.executeUpdate();
        }
    }

    private void insertOrderItem(Connection connection, int orderId, int productId, double subtotal)
            throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO order_items (order_id, product_id, quantity, subtotal) VALUES (?, ?, 1, ?)")) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            stmt.setDouble(3, subtotal);
            stmt.executeUpdate();
        }
    }

    private void updateOrderTotal(Connection connection, int orderId) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE orders SET total = (SELECT SUM(subtotal) FROM order_items WHERE order_id = ?) WHERE order_id = ?")) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

    private List<OrderItem> getOrderedItems(Connection connection, int orderId) throws SQLException {
        List<OrderItem> orderedItems = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT oi.quantity, oi.subtotal, p.product_name, p.image_url, p.product_id FROM order_items oi " +
                        "JOIN products p ON oi.product_id = p.product_id " +
                        "WHERE oi.order_id = ?")) {
            stmt.setInt(1, orderId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    OrderItem item = new OrderItem();
                    item.setQuantity(resultSet.getInt("quantity"));
                    item.setSubtotal(resultSet.getDouble("subtotal"));
                    item.setProductName(resultSet.getString("product_name"));
                    item.setImageUrl(resultSet.getString("image_url"));
                    item.setProductId(resultSet.getInt("product_id"));
                    orderedItems.add(item);
                }
            }
        }

        return orderedItems;
    }
}