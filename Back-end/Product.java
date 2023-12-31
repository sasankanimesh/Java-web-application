//Product.java
package com.sasanka;

public class Product {
    private int productId;
    private String productName;
    private double price;
    private String imageUrl;
    private int categoryId;
    private double subtotal;
    private int quantity;

    public Product() {
        // Default constructor
    }

    public Product(int productId, String productName, double price, String imageUrl, int categoryId) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }
    public int setProductId() {
        return productId;
    }
    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String img_url){
        this.imageUrl = img_url;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }
}