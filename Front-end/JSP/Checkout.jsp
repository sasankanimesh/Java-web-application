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
    if (session.getAttribute("customer_id") == null ) {
        // Redirect to the login page if the user is not logged in
        response.sendRedirect("Login.jsp");
    } else {
        // Access the customer_id from the session
        int customerId = (int) session.getAttribute("customer_id");

        System.out.println("Logged-in customer ID: " + customerId);

        // Fetch order items for the logged-in customer from the database
        List<OrderItem> orderedItems = getOrderItemsByCustomerId(customerId);

        // Fetch total from the orders table
        double total = getTotalByCustomerId(customerId);
%>



<html>
    <head>
        <title>Checkout</title>
        <link rel="stylesheet" type="text/css" href="checkout.css"> 
        <script src="https://www.paypal.com/sdk/js?client-id=Ae54tvaoE_GxUKbeNxrOsHbz-bCLRLSegkNS6CMootL0v7_IWnrxxVwIuuqiFDqt7NpZGereTX8l_EOJ"></script>
    </head>
    <body>
        <div class="checkout-container">
            <h1>Checkout</h1>

            <h3>Order Details</h3>
            <div class="order-items">
                <% for (OrderItem orderedItem : orderedItems) {%>
                <div class="order-item">
                    <div class="product-details">
                        <div class="image-box">
                            <img src="<%= orderedItem.getImageUrl()%>" alt="Product Image">
                        </div>
                        <div class="product-info">
                            <div class="product-name"><%= orderedItem.getProductName()%></div>
                            <div class="quantity">Quantity: <%= orderedItem.getQuantity()%></div>
                            <div class="subtotal">Subtotal: <%= orderedItem.getSubtotal()%></div>
                        </div>
                    </div>
                </div>
                <% }%>
                <div class="confirmation">
                    Total Amount: <%= total%>
                </div>
            </div>
            <hr>
            <h3>Shipping Details</h3>
            <div class="billing-form">
                <form action="CheckoutServlet" method="post">
                    <div class="form-group">
                        <label for="firstName">Full Name:</label>
                        <input type="text" id="firstName" name="FirstName" required>
                    </div>

                    <div class="form-group">
                        <label for="lastName">Last Name:</label>
                        <input type="text" id="lastName" name="lastName" required>
                    </div>

                    <div class="form-group">
                        <label for="address">Address:</label>
                        <textarea id="address" name="address" rows="4" cols="50" required></textarea>
                    </div>

                    <div class="form-group">
                        <label for="postalCode">Postal Code:</label>
                        <input type="text" id="postalCode" name="postalCode" maxlength="10">
                    </div>

                    <div class="form-group">
                        <label for="phone">Phone:</label>
                        <input type="tel" id="phone" name="phone" placeholder="07X-XXX-XXXX" required>
                    </div>
                    <hr>
                    <h3>Payment Method</h3>

                    <div class="payment-method">
                        <input type="radio" id="cash" name="paymentMethod" value="cash" required>
                        <label for="cash">Cash on Delivery</label>
                    </div>
                    <div class="payment-method">
                        <input type="radio" id="paypal" name="paymentMethod" value="paypal">
                        <label for="paypal">PayPal</label>
                    </div>

                    <hr>
                    <div id="paypal-button-container"></div>

                    <div class="button-container">
                        <button class="checkout" type="submit">Confirm Order</button>
                    </div>
                </form>
            </div>


        </div>
        <%

            double exchangeRate = 326.09;

            // Convert the total amount from LKR to USD
            double totalUsd = total / exchangeRate;
        %>
        <script>
            const paypalButtonContainer = document.getElementById('paypal-button-container');
            const paymentMethodRadios = document.getElementsByName('paymentMethod');

            paypalButtonContainer.style.display = paymentMethodRadios[0].checked ? 'block' : 'none';

            paymentMethodRadios.forEach(radio => {
                radio.addEventListener('change', () => {
                    paypalButtonContainer.style.display = radio.value === 'paypal' ? 'block' : 'none';
                });
            });

            paypal.Buttons({
                createOrder: function (data, actions) {
                    return actions.order.create({
                        purchase_units: [{
                                amount: {
                                    value: <%= String.format("%.2f", totalUsd)%>,
                                    currency_code: 'USD'
                                }
                            }]
                    });
                },
                onApprove: function (data, actions) {
                    return actions.order.capture().then(function (details) {
                        fetch('/PayPalCheckoutServlet', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                orderID: data.orderID,
                                paymentID: details.id
                            })
                        })
                                .then(response => response.json())
                                .then(data => {
                                    // Check if the payment was successful
                                    if (data.success) {
                                        // Handle successful payment
                                        document.querySelector('.billing-form').style.display = 'none';
                                        document.querySelector('.confirmation').innerText = 'Payment Successful!';
                                        document.querySelector('#paypal-button-container').style.display = 'none';
                                    } else {
                                        // Handle payment failure
                                        console.error('Payment failed:', data.error);
//                                        alert('Payment failed. Please try again.');
                                    }
                                })
                                .catch((error) => {
                                    console.error('Error:', error);
                                });
                    });
                },
                style: {
                    color: 'gold',
                    shape: 'pill',
                    label: 'paypal',
                    height: 40
                }
            }).render('#paypal-button-container');
        </script>
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
                    String query = "SELECT o.product_name, oi.quantity, oi.subtotal, o.image_url, o.product_id FROM order_items oi "
                            + "JOIN products o ON oi.product_id = o.product_id "
                            + "JOIN orders odr ON oi.order_id = odr.order_id "
                            + "WHERE odr.customer_id = " + customerId+ " AND odr.status = 'PENDING'";

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
                    String query = "SELECT total FROM orders WHERE customer_id = " + customerId+ " AND status = 'PENDING'";

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
    </body>
</html>
