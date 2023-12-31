//FeedbackServlet.java
package com.sasanka;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/FeedbackServlet")
public class FeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
 @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    Connection connection = null;
    PreparedStatement preparedStatement = null;

    try {
        connection = DatabaseUtil.getConnection();

        // Retrieve customer_id based on the entered email
        String email = request.getParameter("email");
        Integer customerId = null;

        // Query the database to get customer_id
        String query = "SELECT customer_id FROM customers WHERE email=?";
        preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, email);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            // Check if the result set contains data before retrieving the customer ID
            customerId = resultSet.getInt("customer_id");
        }

        // Insert feedback into the feedback table
        if (customerId != null) {
            String feedback = request.getParameter("feedback");

            query = "INSERT INTO feedback (customer_id, comments) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, feedback);
            preparedStatement.executeUpdate();

            // Set success message as request attribute
            request.setAttribute("successMessage", "Feedback submitted successfully!");
        } else {
            request.setAttribute("errorMessage", "Error: Customer not found with the provided email address.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        // Close the resources
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Forward the request to the Welcome.jsp page
    RequestDispatcher dispatcher = request.getRequestDispatcher("/Welcome.jsp");
    dispatcher.forward(request, response);
}}