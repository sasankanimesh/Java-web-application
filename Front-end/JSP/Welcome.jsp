<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome</title>
    <link rel="stylesheet" type="text/css" href="welcome.css">
    <script src="welcome.js"></script> 
</head>
<body>
    
<nav>
    <%@ include file="Navbar.jsp" %>    
    <a href="#" onclick="changeContent('Home.jsp', this)" class="active">Home</a>
    <a href="#" onclick="changeContent('AboutUs.jsp', this)">About Us</a>
    <a href="#" onclick="changeContent('ContactUs.jsp', this)">Contact Us</a>
</nav>
    
<div id="content">
    <%-- Include the content of Home.jsp by default --%>
    <%@ include file="Home.jsp" %>
</div>

<%@ include file="footer.html" %>

</body>
</html>
