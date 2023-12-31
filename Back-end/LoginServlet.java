//LoginServlet.java
package com.sasanka;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
      

        try {
            User user = userDao.getUserByEmail(email);

            if (user != null && user.getPassword().equals(password)) {
                // Successful login
                int customerId = user.getCustomerId();
               

                if (customerId != -1) {
                    
                    // Store customer_id in the session
                    request.getSession().setAttribute("customer_id", customerId);
                    response.sendRedirect("Welcome.jsp");
                    return; // Redirect prevents further execution of the code
                } else {
                    // Handle the case when customer_id is not found
                    request.setAttribute("errorMessage", "Customer ID not found for the given email");
                }
            } else {
                // Invalid login
                request.setAttribute("errorMessage", "Invalid email or password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred while processing your request. Please try again later.");
        }

        // Forward to Login.jsp with an error message
        request.getRequestDispatcher("Login.jsp").forward(request, response);
    }
}