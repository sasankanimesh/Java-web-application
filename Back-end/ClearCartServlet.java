//ClearCartServlet.java
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

@WebServlet("/ClearCartServlet")
public class ClearCartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve user ID from session
        int customerId = (int) request.getSession().getAttribute("customer_id");

        // Clear the cart for the user in the database
        clearCart(customerId);

        // Send a success response
        response.getWriter().write("Cart cleared successfully");
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

}