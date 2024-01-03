package com.sasanka;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ProductsFetchServlet")
public class ProductsFetchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseUtil.getConnection();
            statement = connection.createStatement();

            int categoryIdParam = Integer.parseInt(request.getParameter("categoryId"));
            String query = (categoryIdParam == 0) ? "SELECT * FROM products" : "SELECT * FROM products WHERE catagory_id = " + categoryIdParam;
            resultSet = statement.executeQuery(query);

            List<Product> products = new ArrayList<>();
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String productName = resultSet.getString("product_name");
                double price = resultSet.getDouble("price");
                String imageUrl = resultSet.getString("image_url");
                int categoryId = resultSet.getInt("catagory_id");
                int quantityAvailable = resultSet.getInt("quantity_available"); // Add this line

                Product product = new Product(productId, productName, price, imageUrl, categoryId, quantityAvailable);
                products.add(product);
            }

            Gson gson = new Gson();
            String jsonProducts = gson.toJson(products);
            out.println(jsonProducts);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(resultSet, statement, connection);
            out.close();
        }
    }
}
