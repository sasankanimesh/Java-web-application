<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>GREEN Supermarket</title>
    <link rel="stylesheet" type="text/css" href="navbar.css">
</head>
<body>

<div class="navbar">
    <img class="logo" src="logo_trial.png" alt="">

    <div class="right-menu">
        <% if (session.getAttribute("customer_id") == null) { %>
        <!-- User is not logged in -->
        <a class="registration-btn" href="Registration.jsp">Register</a>
        <a class="login-btn" href="Login.jsp">Login</a>
        <a class="cart-btn" href="Cart.jsp">🛒 Cart</a>
        <% } else { %>
        <!-- User is logged in -->
        <a class="logout-btn" href="Profile.jsp">My Profile</a>
        <a class="cart-btn" href="Cart.jsp">🛒 Cart</a>
        <% } %>
    </div>
</div>

</body>
</html>
