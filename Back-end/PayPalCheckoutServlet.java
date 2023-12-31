//PayPalCheckoutServlet.java
package com.sasanka;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/PayPalCheckoutServlet")
public class PayPalCheckoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Process the PayPal transaction confirmation here
        // You may want to save the order details to your database and perform other necessary actions

        // Respond to the client-side with a simple message
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>Payment successful! Thank you for your order.</h1>");
        out.println("</body></html>");
    }
}