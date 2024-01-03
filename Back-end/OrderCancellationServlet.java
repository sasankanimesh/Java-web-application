// OrderCancellationServlet.java
package com.sasanka;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/OrderCancellationServlet")
public class OrderCancellationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve user ID from session
        int customerId = (int) request.getSession().getAttribute("customer_id");

        // Clear the cart for the user in the database and send an email
        clearCart(customerId);
        updateOrderStatus(customerId);

        // Redirect to Welcome.jsp after cart is cleared
        response.sendRedirect("Welcome.jsp");
    }
    private void updateOrderStatus(int customerId) {
    try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE orders SET status = 'CANCELLED' WHERE customer_id = ? AND status = 'CONFIRMED'")) {
        updateStmt.setInt(1, customerId);
        updateStmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private void clearCart(int customerId) {
        Connection connection = null;
        PreparedStatement deleteOrderItemsStatement = null;
        PreparedStatement updateTotalStatement = null;

        try {
            // Get a database connection
            connection = DatabaseUtil.getConnection();

            // Delete order items for the given customer from the order_items table
            String deleteOrderItemsQuery = "DELETE FROM order_items WHERE order_id IN (SELECT order_id FROM orders WHERE customer_id = ?)";
            deleteOrderItemsStatement = connection.prepareStatement(deleteOrderItemsQuery);
            deleteOrderItemsStatement.setInt(1, customerId);
            int deletedRows = deleteOrderItemsStatement.executeUpdate();

            // If there were order items deleted, update the total in the orders table
            if (deletedRows > 0) {
                String updateTotalQuery = "UPDATE orders SET total = 0 WHERE customer_id = ?";
                updateTotalStatement = connection.prepareStatement(updateTotalQuery);
                updateTotalStatement.setInt(1, customerId);
                updateTotalStatement.executeUpdate();

                // Send an email to the customer
                sendClearCartEmail(customerId);
            }

            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            // Handle any database-related exceptions
            try {
                if (connection != null) {
                    // Rollback the transaction in case of an exception
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            // Close the database resources
            DatabaseUtil.close(null, deleteOrderItemsStatement, null);
            DatabaseUtil.close(null, updateTotalStatement, connection);
        }
    }

    private void sendClearCartEmail(int customerId) {
        String userEmail = UserDao.getEmailByCustomerId(customerId);

        // send the email
        if (userEmail != null && !userEmail.isEmpty()) {
            String emailBody = "Dear Customer,\n\n";
            emailBody += "We wanted to inform you that your order has been canceled successfully.\n";
            emailBody += "If you have any questions or concerns, please do not hesitate to contact our support team.\n";
            emailBody += "Thank you for considering us. We appreciate your business.\n\n";
            emailBody += "Best regards,\nGREEN Supermarket.";

            EmailSender.sendEmail(userEmail, "Order Cancelled", emailBody);
        }
    }
}
