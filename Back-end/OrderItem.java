//OrderItem.java
package com.sasanka;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItem {
    private String productName;
    private int quantity;
    private double subtotal;
    private String imageUrl;
    private int productId;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
     public static List<OrderItem> extractOrderedItems(ResultSet resultSet) throws SQLException {
        List<OrderItem> orderedItems = new ArrayList<>();

        while (resultSet.next()) {
            OrderItem item = new OrderItem();
            item.setProductId(resultSet.getInt("product_id"));
            item.setProductName(resultSet.getString("product_name"));
            item.setQuantity(resultSet.getInt("quantity"));
            item.setSubtotal(resultSet.getDouble("subtotal"));
            item.setImageUrl(resultSet.getString("image_url"));
            orderedItems.add(item);
        }

        return orderedItems;
    }
     

}