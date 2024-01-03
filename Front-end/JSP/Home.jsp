<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="com.sasanka.DatabaseUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.sasanka.Product" %>
<%@ page import="com.sasanka.User" %>

<!DOCTYPE html>
<html>
    <head>

        <meta charset="UTF-8">
        <title>Home</title>
        <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
        <link rel="stylesheet" type="text/css" href="home.css">
    </head>
    <body>

        <div>
            <h1>Welcome to GREEN Supermarket</h1>

            <%
                int selectedCategoryId = 0; 
                // Check if categoryId is present in the request parameters
                String categoryIdParam = request.getParameter("categoryId");
                if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
                    try {
                        selectedCategoryId = Integer.parseInt(categoryIdParam);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            %>

            <div class="category-container">
                <div class="category <%= selectedCategoryId == 0 ? "active" : "" %>" data-category-id="0" onclick="showAllProducts()">All</div>
                <%
                    Connection connection = null;
                    Statement statement = null;
                    ResultSet resultSetCategories = null;

                    try {
                        connection = DatabaseUtil.getConnection();
                        statement = connection.createStatement();

                        resultSetCategories = statement.executeQuery("SELECT * FROM catagory");

                        while (resultSetCategories.next()) {
                            int categoryId = resultSetCategories.getInt("catagory_id");
                            String categoryName = resultSetCategories.getString("name");

                            String activeClass = (selectedCategoryId == categoryId) ? "active" : "";

                            out.println("<div class='category " + activeClass + "' data-category-id='" + categoryId + "' onclick='showProductsByCategory(" + categoryId + ")'>" + categoryName + "</div>");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        DatabaseUtil.close(resultSetCategories, statement, connection);
                    }
                %>
            </div>

            <div class="product-container" id="productContainer"></div> 

        </div>

        <script>
            // Initial display: Show all products
            showAllProducts();

            function showAllProducts() {
                // Remove active class from all categories
                var allCategories = document.querySelectorAll('.category');
                allCategories.forEach(function (category) {
                    category.classList.remove('active');
                });

                // Highlight the "All" button
                var allCategory = document.querySelector('.category[data-category-id="0"]');
                if (allCategory) {
                    allCategory.classList.add('active');
                }

                // proceed to fetch and display all products
                showProducts(0);
            }

            function showProductsByCategory(categoryId) {
                // Update the active class for categories
                var activeCategory = document.querySelector('.category.active');
                if (activeCategory) {
                    activeCategory.classList.remove('active');
                }

                var selectedCategory = document.querySelector('.category[data-category-id="' + categoryId + '"]');
                if (selectedCategory) {
                    selectedCategory.classList.add('active');
                }

                // proceed to fetch and display products
                showProducts(categoryId);
            }

            function showProducts(categoryId) {
                var productContainer = document.getElementById("productContainer");
                productContainer.innerHTML = ""; // Clear existing products

                //AJAX request
                var xhr = new XMLHttpRequest();
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4) {
                        if (xhr.status === 200) {
                            var products = JSON.parse(xhr.responseText);

                            // Display products in the productContainer
                            products.forEach(function (product) {
                                var productDiv = document.createElement("div");
                                productDiv.className = "product";
                                productDiv.innerHTML = "<img src='" + product.imageUrl + "' alt='" + product.productName + "'><br>" +
                                        "<strong>" + product.productName + "</strong><br>" +
                                        "Price: Rs." + product.price + "<br>";

                                // Check if quantity is greater than 0 to display "Add to Cart" or "Sold Out"
                                if (product.quantity > 0) {
                                    productDiv.innerHTML += "<button class='add-to-cart' onclick='addToCart(" + product.productId + ")'>Add to Cart</button>";
                                } else {
                                    productDiv.innerHTML += "<span class='sold-out'>Sold Out</span>";
                                }

                                productContainer.appendChild(productDiv);
                            });
                        } else {
                            // Handle errors
                            console.error("Error fetching products. Status: " + xhr.status);
                        }
                    }
                };

                var url = "ProductsFetchServlet?categoryId=" + categoryId;
                console.log("Fetching products from URL: " + url); // Log the URL to check if it's correct
                xhr.open("GET", url, true);
                xhr.send();
            }


            function addToCart(productId) {
            <% if (session.getAttribute("customer_id") == null) { %>
                window.location.href = "Login.jsp";
            <% } else { %>
                var customerId = '<%= session.getAttribute("customer_id") %>';
                makeAjaxCallToAddToCart(customerId, productId);
            <% } %>
            }

            function makeAjaxCallToAddToCart(customerId, productId) {
                var data = {
                    customerId: customerId,
                    productId: productId
                };

                var url = '<%= request.getContextPath() %>/AddToCartServlet';

                // Perform the AJAX request
                fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
                    },
                    body: new URLSearchParams(data)
                })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error('Network response was not ok');
                            }
                            return response.text();
                        })
                        .then(responseText => {
                            console.log(responseText);
                        })
                        .catch(error => {
                            console.error('Error adding item to the cart:', error);
                        });
            }
        </script>

    </body>
</html>
