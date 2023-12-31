//RemoveItemServlet.java
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

@WebServlet("/RemoveItemServlet")
public class RemoveItemServlet extends HttpServlet {
    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // Retrieve user ID from session
    int customerId = (int) request.getSession().getAttribute("customer_id");

    // Get the product ID from the request parameter
    int productId = Integer.parseInt(request.getParameter("productId"));

    // Log the received productId (add this line)
    System.out.println("Received productId: " + productId);

    // Remove the item for the user in the database
    removeItem(customerId, productId);

    // Send a success response
    response.getWriter().write("Item removed successfully");
}


   private void removeItem(int customerId, int productId) {
    Connection connection = null;
    PreparedStatement deleteOrderItemStatement = null;
    PreparedStatement updateTotalStatement = null;

    try {
        // Get a database connection
        connection = DatabaseUtil.getConnection();
        
        // Set autocommit to false to start a transaction
        connection.setAutoCommit(false);

        // Delete the specified order item from the order_items table
        String deleteOrderItemQuery = "DELETE FROM order_items WHERE order_id IN " +
                "(SELECT order_id FROM orders WHERE customer_id = ?) AND product_id = ?";
        deleteOrderItemStatement = connection.prepareStatement(deleteOrderItemQuery);
        deleteOrderItemStatement.setInt(1, customerId);
        deleteOrderItemStatement.setInt(2, productId);
        int deletedRows = deleteOrderItemStatement.executeUpdate();

        // If the order item was deleted, update the total in the orders table
        if (deletedRows > 0) {
            String updateTotalQuery = "UPDATE orders SET total = (SELECT SUM(subtotal) FROM order_items WHERE order_id IN " +
                    "(SELECT order_id FROM orders WHERE customer_id = ?)) WHERE customer_id = ?";
            updateTotalStatement = connection.prepareStatement(updateTotalQuery);
            updateTotalStatement.setInt(1, customerId);
            updateTotalStatement.setInt(2, customerId);
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
        // Reset autocommit to true when done
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException rollbackException) {
            rollbackException.printStackTrace();
        }

        // Close the database resources
        DatabaseUtil.close(null, deleteOrderItemStatement, null);
        DatabaseUtil.close(null, updateTotalStatement, connection);
    }
}
}