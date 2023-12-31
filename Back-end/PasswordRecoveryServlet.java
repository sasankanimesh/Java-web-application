//PasswordRecoveryServlet.java
package com.sasanka;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/PasswordRecoveryServlet")
public class PasswordRecoveryServlet extends HttpServlet {

    private UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        if (userDao.isUserExists(email)) {
            // Get the new password entered by the user
            String newPassword = request.getParameter("newPassword");

            // Update the user's password in the database
            userDao.updatePassword(email, newPassword);

            // Redirect to a success page
            response.sendRedirect("Login.jsp");
        } else {
            request.setAttribute("errorMessage", "Entered email does not existing!");
            request.getRequestDispatcher("Forgot-Password.jsp").forward(request, response);
            
        }
    }
}