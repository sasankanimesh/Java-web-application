package com.sasanka;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.Order;
import com.paypal.orders.OrdersCaptureRequest;

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
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            String orderId = request.getParameter("orderID"); 

            PayPalHttpClient client = new PayPalHttpClient(
                    new PayPalEnvironment.Sandbox("PAYPAL_CLIENT_ID", "PAYPAL_SECRET"));

            OrdersCaptureRequest requestCapture = new OrdersCaptureRequest(orderId);
            HttpResponse<Order> captureResponse = client.execute(requestCapture);

            Order capturedOrder = captureResponse.result();
            
            // Check the status of the payment
            if (capturedOrder.status().equals("COMPLETED")) {
                out.println("<html><body>");
                out.println("<h1>Payment successful! Thank you for your order.</h1>");
                out.println("</body></html>");
            } else {
                out.println("<html><body>");
                out.println("<h1>Payment not successful. Please try again.</h1>");
                out.println("</body></html>");
            }

        } catch (Exception e) {
            out.println("<html><body>");
            out.println("<h1>Error processing payment. Please try again.</h1>");
            out.println("</body></html>");
            e.printStackTrace();
        }
    }
}
