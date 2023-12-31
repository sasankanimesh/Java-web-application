//RegistrationServlet.java
package com.sasanka;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import static java.lang.Integer.parseInt;

@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
    String postalCodeStr = request.getParameter("postalCode");
    int postalCode;
    
    // Validate the postal code format
    if (postalCodeStr == null || postalCodeStr.isEmpty()) {
        // Handle the case where the postal code is empty
        response.getWriter().println("Postal code cannot be empty.");
        return; // Exit the method to avoid further processing
    }

    try {
        postalCode = Integer.parseInt(postalCodeStr);
    } catch (NumberFormatException e) {
        // Handle the case where the postal code is not a valid integer
        response.getWriter().println("Postal code must be a valid number.");
        return; // Exit the method to avoid further processing
    }

        // Validate email format, Gmail address, and phone number format
        if (!isValidEmail(email) || !isGmailAddress(email) || !isValidPhoneNumber(phone)) {
            request.setAttribute("errorMessage", "Invalid registration details");
            request.getRequestDispatcher("Registration.jsp").forward(request, response);
            return;
        }

        // Check if email already exists in the database
        if (userDao.isUserExists(email)) {
            request.setAttribute("errorMessage", "Email is already registered");
            request.getRequestDispatcher("Registration.jsp").forward(request, response);
            return;
        }

        // Proceed with registration if all checks pass
        User user = new User(firstName, lastName, email, password, phone, address, postalCode);

        if (userDao.registerUser(user)) {
            // Registration successful, redirect to the login page
            response.sendRedirect("Login.jsp");
        } else {
            // Registration failed, set an error message and forward to Registration.jsp
            request.setAttribute("errorMessage", "Registration failed");
            request.getRequestDispatcher("Registration.jsp").forward(request, response);
        }
    }

    private boolean isValidEmail(String email) {
       
        return email != null && email.contains("@");
    }

    private boolean isGmailAddress(String email) {
        // Check if the email is a valid Gmail address
        return email.endsWith("@gmail.com");
    }

    private boolean isValidPhoneNumber(String phone) {
        // Add your phone number validation logic here
        return phone != null && phone.matches("\\d{10}");
    }
}
