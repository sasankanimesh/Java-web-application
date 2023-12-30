<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome</title>
    <link rel="stylesheet" type="text/css" href="welcome.css">
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

<script>
    function changeContent(page, clickedTab) {
        var contentDiv = document.getElementById('content');
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function() {
            if (this.readyState === 4 && this.status === 200) {
                contentDiv.innerHTML = this.responseText;

                var activeTab = document.querySelector('nav a.active');
                if (activeTab) {
                    activeTab.classList.remove('active');
                }

                clickedTab.classList.add('active');

                if (page.includes('Home.jsp')) {
                    showAllProducts();
                }
            }
        };
        xhttp.open("GET", page, true);
        xhttp.send();
    }
</script>

</body>
</html>
