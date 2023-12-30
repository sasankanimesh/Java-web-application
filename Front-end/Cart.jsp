<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ page import="java.sql.*" %>
<%@ page import="com.sasanka.DatabaseUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.sasanka.Product" %>
<%@ page import="com.sasanka.User" %>
<%@ page import="com.sasanka.OrderItem" %>

<%
    // Check if the user is logged in
    if (session.getAttribute("customer_id") == null) {
        // Redirect to the login page if the user is not logged in
        response.sendRedirect("Login.jsp");
    } else {
        int customerId = (int) session.getAttribute("customer_id");

       
        System.out.println("Logged-in customer ID: " + customerId);

        // Fetch order items for the logged-in customer from the database
        List<OrderItem> orderedItems = getOrderItemsByCustomerId(customerId);

        // Fetch total from the orders table
        double total = getTotalByCustomerId(customerId);
%>

<html>
<head>
    <title>Green Supermarket Cart</title>
    <link rel="stylesheet" type="text/css" href="cart.css">
</head>
<body>
    <div class="cart-container">
        <h2>My Cart</h2>
        <div class="order-items">
            <% for (OrderItem orderedItem : orderedItems) { %>
                <div class="order-item">
                    <div class="product-details">
                        <div class="image-box">
                            <img src="<%= orderedItem.getImageUrl() %>" alt="Product Image">
                        </div>
                        <div class="product-info">
                            <div class="product-name"><%= orderedItem.getProductName() %></div>
                            <div class="quantity">Quantity: <%= orderedItem.getQuantity() %></div>
                            <div class="subtotal">Subtotal: <%= orderedItem.getSubtotal() %></div>
                        </div>
                    </div>
                     <div class="button-container">
                        <button class="clear-cart" onclick="removeItem(<%= orderedItem.getProductId() %>)">Remove</button>
                    </div>
                </div>
            <% } %>
        </div>
        <div class="total" align="right">
            Total: Rs.<%= total %>
        </div>
        <div class="button-container">
            <button class="clear-cart" onclick="clearCart()">Clear Cart</button>
            <button class="checkout" onclick="redirectToCheckout()">Checkout</button>
        </div>
    </div>
        <script>
function redirectToCheckout() {
    // Navigate to the Checkout.jsp page
    window.location.href = 'Checkout.jsp';
}
</script>

    <script>
        function removeItem(productId) {
            if (confirm("Are you sure you want to remove this item from your cart?")) {
                // Make an AJAX call to remove the item
                var xhr = new XMLHttpRequest();
                xhr.open("POST", "RemoveItemServlet", true);
                xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                
                // Send the productId as a parameter
                var params = "productId=" + productId;
                
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            location.reload();
                        } else {
                            console.error("Error removing item: " + xhr.status);
                        }
                    }
                };
                xhr.send(params);
            }
        }

        function clearCart() {
            if (confirm("Are you sure you want to clear your cart?")) {
                // Make an AJAX call to clear the cart
                var xhr = new XMLHttpRequest();
                xhr.open("POST", "ClearCartServlet", true);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        location.reload();
                    }
                };
                xhr.send();
            }
        }

 
    </script>
</body>
</html>

<%
    }
%>

<%! 
    // Define a method to fetch order items by customer ID
    private List<OrderItem> getOrderItemsByCustomerId(int customerId) {
        List<OrderItem> orderedItems = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseUtil.getConnection();
            statement = connection.createStatement();

            // Query to fetch order items based on customer ID
            String query = "SELECT o.product_name, oi.quantity, oi.subtotal, o.image_url, o.product_id FROM order_items oi " +
                           "JOIN products o ON oi.product_id = o.product_id " +
                           "JOIN orders odr ON oi.order_id = odr.order_id " +
                           "WHERE odr.customer_id = " + customerId;

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

    // Define a method to fetch total by customer ID
    private double getTotalByCustomerId(int customerId) {
        double total = 0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseUtil.getConnection();
            statement = connection.createStatement();

            // Query to fetch total based on customer ID
            String query = "SELECT total FROM orders WHERE customer_id = " + customerId;

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
%>
