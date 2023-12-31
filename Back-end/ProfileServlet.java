package com.sasanka;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/ProfileServlet")
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int customerId = (int) session.getAttribute("customer_id");

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String address = request.getParameter("address");
        String phone = request.getParameter("phone");
        String postalCodeString = request.getParameter("postalCode");

        // Perform any necessary validation on the input data

        int postalCode = 0;
        try {
            postalCode = Integer.parseInt(postalCodeString);
        } catch (NumberFormatException e) {
            // Handle the case where the postal code is not a valid integer
            e.printStackTrace();
        }

        // Update the customer's table with the new information
        updateUserProfile(customerId, firstName, lastName, address, phone, postalCode);

        // Redirect back to the profile page after the update
        response.sendRedirect("Profile.jsp");
    }

    private void updateUserProfile(int customerId, String firstName, String lastName, String address, String phone, int postalCode) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseUtil.getConnection();

            // Use a prepared statement to avoid SQL injection
            String query = "UPDATE customers SET first_name=?, last_name=?, address=?, phone_number=?, postalcode=? WHERE customer_id=?";
            preparedStatement = connection.prepareStatement(query);

            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, phone);
            preparedStatement.setInt(5, postalCode);
            preparedStatement.setInt(6, customerId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(preparedStatement, connection);
        }
    }
}
