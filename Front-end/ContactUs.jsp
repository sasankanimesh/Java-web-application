<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*, java.io.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Contact Us</title>
   <link rel="stylesheet" type="text/css" href="contactUs.css">
   
</head>
<body>

<h1>Contact Us</h1>
<p>
    Welcome to our Contact Us page. You can reach us through the following methods:
</p>

<ul>
    <li>Email: <a href="mailto:contact@example.com">contact@example.com</a></li>
    <li>Phone: <a href="tel:+11234567890">+1 123-456-7890</a></li>
    <li>Address: <a href="https://www.google.com/maps?q=37.7749,-122.4194" target="_blank">1234 Elm Street, Springfield, SL</a></li>
    <li>Social Media:
        <ul>
            <li><a href="https://www.facebook.com/example" target="_blank">Facebook</a></li>
            <li><a href="https://twitter.com/example" target="_blank">Twitter</a></li>
            <li><a href="https://www.instagram.com/example" target="_blank">Instagram</a></li>
        </ul>
    </li>
    <li>Online Chat: Use our live chat option on the website</li>
</ul>

   <div class="container">
        <h2>Feedback</h2>


        <form action="FeedbackServlet" method="post">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" placeholder="user@gmail.com" required>

             <label for="feedback">Your Feedback:</label><br>
             <textarea id="feedback" name="feedback" rows="5" cols="60" required=""></textarea><br>

            <button type="submit">Submit Feedback</button>
        </form>
    </div>

</body>
</html>
