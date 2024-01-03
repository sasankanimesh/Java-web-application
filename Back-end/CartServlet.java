/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.sasanka;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/CartServlet")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if the user is logged in
        HttpSession session = request.getSession();
        if (session.getAttribute("customer_id") == null) {
            // Redirect to the login page if the user is not logged in
            response.sendRedirect("Login.jsp");
        } else {
            int customerId = (int) session.getAttribute("customer_id");

            // Fetch order items for the logged-in customer from the database
            List<OrderItem> orderedItems = getOrderItemsByCustomerId(customerId);

            // Fetch total from the orders table
            double total = getTotalByCustomerId(customerId);

            // Create a JSON object to hold the data
            JsonObject jsonData = new JsonObject();
            jsonData.addProperty("total", total);

            // Create a JSON array for ordered items
            JsonArray jsonOrderedItems = new JsonArray();
            for (OrderItem orderedItem : orderedItems) {
                JsonObject jsonItem = new JsonObject();
                jsonItem.addProperty("productName", orderedItem.getProductName());
                jsonItem.addProperty("quantity", orderedItem.getQuantity());
                jsonItem.addProperty("subtotal", orderedItem.getSubtotal());
                jsonItem.addProperty("imageUrl", orderedItem.getImageUrl());
                jsonItem.addProperty("productId", orderedItem.getProductId());
                jsonOrderedItems.add(jsonItem);
            }

            jsonData.add("orderedItems", jsonOrderedItems);

            // Set the content type and write JSON data to the response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonData.toString());
        }
    }
private List<OrderItem> getOrderItemsByCustomerId(int customerId) {
    List<OrderItem> orderedItems = new ArrayList<>();
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    try {
        connection = DatabaseUtil.getConnection();
        statement = connection.createStatement();

        // Query to fetch order items based on customer ID
        String query = "SELECT o.product_name, oi.quantity, oi.subtotal, o.image_url, o.product_id " +
                        "FROM order_items oi " +
                        "JOIN products o ON oi.product_id = o.product_id " +
                        "JOIN orders odr ON oi.order_id = odr.order_id " +
                        "WHERE odr.customer_id = " + customerId + " AND odr.status = 'PENDING'";

        resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(resultSet.getString("product_name"));
            orderItem.setQuantity(resultSet.getInt("quantity"));
            orderItem.setSubtotal(resultSet.getDouble("subtotal"));
            orderItem.setImageUrl(resultSet.getString("image_url"));
            orderItem.setProductId(resultSet.getInt("product_id"));

            orderedItems.add(orderItem);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        DatabaseUtil.close(resultSet, statement, connection);
    }

    return orderedItems;
}
private double getTotalByCustomerId(int customerId) {
    double total = 0;
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    try {
        connection = DatabaseUtil.getConnection();
        statement = connection.createStatement();

        // Query to fetch total based on customer ID
        String query = "SELECT total FROM orders WHERE customer_id = " + customerId + " AND status = 'PENDING'";

        resultSet = statement.executeQuery(query);

        if (resultSet.next()) {
            total = resultSet.getDouble("total");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        DatabaseUtil.close(resultSet, statement, connection);
    }

    return total;
}

}
