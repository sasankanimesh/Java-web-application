<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="forms.css">
    <script>
        function displayModal(message) {
            document.getElementById('customModal').style.display = 'block';
            document.getElementById('overlay').style.display = 'block';
            document.getElementById('modalMessage').innerHTML = message;
        }

        function hideModal() {
            document.getElementById('customModal').style.display = 'none';
            document.getElementById('overlay').style.display = 'none';
        }

        function validateForm() {
            var password = document.getElementById("password").value;
            var confirmPassword = document.getElementById("confirmPassword").value;

            if (password !== confirmPassword) {
                displayModal("Passwords do not match!");
                return false;
            }
            return true;
        }
    </script>
    <title>Registration Page</title>
</head>
<body>
    <div class="container">
        <h2>Registration</h2>

        <% String errorMessage = (String) request.getAttribute("errorMessage");
           if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="custom-alert">
                <%= errorMessage %>
            </div>
        <% } %>

        <div id="customModal" class="custom-modal">
            <span class="close-btn" onclick="hideModal()">&times;</span>
            <p id="modalMessage"></p>
        </div>

        <div id="overlay" class="overlay" onclick="hideModal()"></div>

        <form action="RegistrationServlet" method="post" onsubmit="return validateForm();">
            <label for="firstName">First Name:</label>
            <input type="text" id="firstName" name="firstName" required>
            
            <label for="lastName">Last Name:</label>
            <input type="text" id="lastName" name="lastName" required>

            <label for="email">Email:</label>
            <input type="email" id="email" name="email" placeholder="user@gmail.com" required>

            <label for="phone">Phone Number:</label>
            <input type="tel" id="phone" name="phone" placeholder="071-XXXX XXX" required>
            
            <label for="address">Address:</label>
    <textarea id="address" name="address" rows="4" cols="50"></textarea>
        <label for="postalcode">Postal Code:</label>
    <input type="text" id="postalcode" name="postalCode" maxlength="10">

            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>

            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>

            <button type="submit">Register</button>
        </form>
    </div>
</body>
</html>
