<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ page import="java.sql.*" %>
<%@ page import="com.sasanka.DatabaseUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
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

        // Fetch user details
        User user = getUserById(customerId);

        // Fetch order items for the logged-in customer from the database
        List<OrderItem> orderedItems = getOrderItemsByCustomerId(customerId);

        // Fetch total from the orders table
        double total = getTotalByCustomerId(customerId);
%>

<html>
    <head>
        <title>Order Confirmation</title>
        <link rel="stylesheet" type="text/css" href="profile.css">
        <script src="profile.js"></script>
    </head>
    <body>

        <div class="cart-container">
            <h1>My Profile</h1>
            <form action="ProfileServlet" method="post">
                <h3><b>Full Name: </b></h3><p>
                    <span id="firstNameText"><%= user.getFirstName() %></span>
                    <input type="text" name="firstName" id="firstNameInput" value="<%= user.getFirstName() %>" style="display: none;">
                    <span id="lastNameText"><%= user.getLastName() %></span>
                    <input type="text" name="lastName" id="lastNameInput" value="<%= user.getLastName() %>" style="display: none;">
                </p>
                <h3><b>Email: </b></h3><p><%= user.getEmail() %></p>
                <h3><b>Address: </b></h3><p>
                    <span id="addressText"><%= user.getAddress() %></span>
                    <input type="text" name="address" id="addressInput" value="<%= user.getAddress() %>" style="display: none;">
                </p>
                <h3><b>Phone number: </b></h3><p>
                    <span id="phoneNumber"><%= user.getPhone() %></span>
                    <input type="text" name="phone" id="phoneInput" value="<%= user.getPhone() %>" style="display: none;">
                </p>
                <h3><b>Postal Code: </b></h3><p>
                    <span id="postalCodeText"><%= user.getPostalcode() %></span>
                    <input type="text" name="postalCode" id="postalCodeInput" value="<%= user.getPostalcode() %>" style="display: none;">
                </p>
                <div class="button-container">
                    <button class="checkout" type="button" onclick="editInfo()">Edit Profile</button>
                    <input class="checkout" type="submit" value="Save" style="display: none;">

                    <a class="clear-cart" href="LogoutServlet">Logout</a>

                </div>
            </form>
            <hr>


            <h3>Previous Orders</h3>
            <div class="order-items">
                <% if (!orderedItems.isEmpty()) { %>
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
                </div>
                <% } %>
                <!-- Display "Cancel Order" button only when orders are available -->
                <div class="button-container">
                    <button class="clear-cart" onclick="cancelOrder()">Cancel Order</button>
                </div>
                <% } else { %>
                <p>No orders available.</p>
                <% } %>
            </div>

        </div>            
    </div>


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
                           "WHERE odr.customer_id = " + customerId+" AND status = 'CONFIRMED'";

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
            String query = "SELECT total FROM orders WHERE customer_id = " + customerId+" AND status = 'CONFIRMED'";

            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                total = resultSet.getDouble("SUM(total)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(resultSet, statement, connection);
        }

        return total;
    }

    // Define a method to fetch user details by customer ID
    private User getUserById(int customerId) {
        User user = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseUtil.getConnection();

            // Use a prepared statement to avoid SQL injection
            String query = "SELECT * FROM customers WHERE customer_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, customerId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
               user.setEmail(resultSet.getString("email"));
            user.setAddress(resultSet.getString("address"));
           user.setPostalcode(resultSet.getInt("postalcode"));
                user.setPhone(resultSet.getString("phone_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(resultSet, preparedStatement, connection);
        }

        return user;
    }
%>
