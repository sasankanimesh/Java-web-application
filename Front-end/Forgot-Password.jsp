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
            var password = document.getElementById("newPassword").value;
            var confirmPassword = document.getElementById("confirmPassword").value;

            if (password !== confirmPassword) {
                displayModal("Passwords do not match!");
                return false;
            }
            return true;
        }
    </script>
    <title>Forgot Password</title>
</head>
<body>
    <div class="container">
        <h2>Forgot Password</h2>

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

        <form action="PasswordRecoveryServlet" method="post" onsubmit="return validateForm();">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" placeholder="user@gmail.com" required>

            <label for="newPassword">New Password:</label>
            <input type="password" id="newPassword" name="newPassword" required>

            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>

            <button type="submit">Recover Password</button>
        </form>
    </div>
</body>
</html>
