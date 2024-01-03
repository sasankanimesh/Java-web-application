// CheckoutServlet.java
package com.sasanka;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/CheckoutServlet")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String firstName = request.getParameter("FirstName");
        String lastName = request.getParameter("lastName");
        String address = request.getParameter("address");
        String postalCodeStr = request.getParameter("postalCode");
        String phone = request.getParameter("phone");

        // Check if postalCodeStr is a valid integer
        int postalCode;
        try {
            postalCode = Integer.parseInt(postalCodeStr);
        } catch (NumberFormatException e) {
            // Handle the case where the postal code is not a valid integer
            response.getWriter().println("Postal code must be a valid number.");
            return; // Exit the method to avoid further processing
        }

        HttpSession session = request.getSession();
        int customerId = (int) session.getAttribute("customer_id");

        // Update customer details in the database
        boolean updateSuccess = updateCustomerDetails(customerId, firstName, lastName, address, postalCode, phone);

        if (updateSuccess) {
            // Fetch ordered items
            List<OrderItem> orderedItems = getOrderedItems(customerId);

            // Send the order confirmation email
            sendOrderConfirmationEmail(getEmailByCustomerId(customerId), orderedItems);

            // Invalidate the session
            //session.invalidate();

            // Forward to the order confirmation page
             updateOrderStatus(customerId);
             
            request.setAttribute("orderedItems", orderedItems);
            request.getRequestDispatcher("Welcome.jsp").forward(request, response);
        } else {
            // Handle the update failure
            response.getWriter().println("Failed to update customer details.");
        }
    }
    private void updateOrderStatus(int customerId) {
    try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE orders SET status = 'CONFIRMED' WHERE customer_id = ? AND status = 'PENDING'")) {
        updateStmt.setInt(1, customerId);
        updateStmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private boolean updateCustomerDetails(int customerId, String firstName, String lastName, String address,
            int postalCode, String phone) {
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE customers SET first_name = ?, last_name = ?, phone_number = ?, address = ?, postalcode = ? WHERE customer_id = ?")) {

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setInt(5, postalCode);
            preparedStatement.setInt(6, customerId);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getEmailByCustomerId(int customerId) {
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT email FROM customers WHERE customer_id = ?")) {

            preparedStatement.setInt(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<OrderItem> getOrderedItems(int customerId) {
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement stmt = connection.prepareStatement(
                        "SELECT oi.quantity, oi.subtotal, p.product_name, p.image_url, p.product_id FROM order_items oi "
                                + "JOIN products p ON oi.product_id = p.product_id "
                                + "JOIN orders o ON oi.order_id = o.order_id "
                                + "WHERE o.customer_id = ? AND o.status = 'PENDING'")) {

            stmt.setInt(1, customerId);

            return OrderItem.extractOrderedItems(stmt.executeQuery());

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception as needed
            return null;
        }
    }

   private void sendOrderConfirmationEmail(String userEmail, List<OrderItem> orderedItems) {

    StringBuilder emailBody = new StringBuilder();
    emailBody.append("Dear Customer,\n\n");
    emailBody.append("Thank you for your order! We are pleased to confirm your recent purchase.\n\n");
    emailBody.append("Order Details:\n");
    
    for (OrderItem item : orderedItems) {
        emailBody.append("Product: ").append(item.getProductName()).append("\n");
        emailBody.append("Quantity: ").append(item.getQuantity()).append("\n");
        emailBody.append("Subtotal: Rs.").append(item.getSubtotal()).append("\n\n");
    }

    emailBody.append("Total: Rs.").append(calculateTotal(orderedItems)).append("\n\n");
    emailBody.append("We appreciate your business and look forward to serving you again.\n");
    emailBody.append("Best regards,\nGreen Supermarket.");

    // Example usage with EmailSender class (replace with your actual implementation)
    EmailSender.sendEmail(userEmail, "Order Confirmation", emailBody.toString());
}

private double calculateTotal(List<OrderItem> orderedItems) {
    // Implement the logic to calculate the total based on ordered items
    double total = 0.0;

    for (OrderItem item : orderedItems) {
        total += item.getSubtotal();
    }

    return total;
}

}
