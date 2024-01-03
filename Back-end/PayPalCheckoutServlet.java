// PayPalCheckoutServlet.java
package com.sasanka;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/PayPalCheckoutServlet")
public class PayPalCheckoutServlet extends HttpServlet {

    private static final String UPDATE_ORDER_STATUS_QUERY
            = "UPDATE orders SET status = 'PAID', payment_id = ?, payment_check = 'SUCCESS' WHERE order_id = ?";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Fetch order-related information from the request
        String orderId = request.getParameter("order_id");
        String customerId = request.getParameter("customer_id");
        double total = Double.parseDouble(request.getParameter("total"));
        String paymentId = request.getParameter("payment_id");

        // Simulate processing the PayPal transaction confirmation 
        boolean isPayPalTransactionSuccessful = simulatePayPalTransaction(paymentId, total);

        // Update the order status and payment check in the database
        if (isPayPalTransactionSuccessful) {
            try (Connection connection = DatabaseUtil.getConnection()) {
                updateOrderStatus(connection, orderId, paymentId);

                // Respond to the client-side with a simple message
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<html><body>");
                out.println("<h1>Payment successful! Thank you for your order.</h1>");
                out.println("</body></html>");

            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Error processing payment. Please contact customer support.");
            }
        } else {
            // Handle unsuccessful PayPal transaction
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "PayPal transaction failed.");
        }
    }

    private void updateOrderStatus(Connection connection, String orderId, String paymentId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ORDER_STATUS_QUERY)) {
            // Set parameters for the query
            preparedStatement.setString(1, paymentId);
            preparedStatement.setString(2, getCurrentDateTime()); 
            preparedStatement.setString(3, orderId);

            // Execute the update
            preparedStatement.executeUpdate();
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private boolean simulatePayPalTransaction(String paymentId, double total) {

        try {

            String apiUrl = "PAYPAL_API_URL";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set up the request method, headers, and other necessary parameters
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String apiToken = "PAYPAL_API_TOKEN";
            connection.setRequestProperty("Authorization", "Bearer " + apiToken);

            // Create a JSON payload with payment details 
            String jsonPayload = "{\"paymentId\": \"" + paymentId + "\", \"total\": " + total + "}";

            // Send the request
            connection.getOutputStream().write(jsonPayload.getBytes());

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }

            // Close the connection
            connection.disconnect();

            // Parse the response and check for success 
            String apiResponse = responseBuilder.toString();
            return apiResponse.contains("\"status\": \"success\"");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
