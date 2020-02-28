package com.example.thunderbolt_108.serverapp.Model;

public class Order {
    private String ProductId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;

    public Order() {

    }

    public Order(String productId,String productName,String quantity,String price,String discount) {
        ProductId=productId;
        ProductName=productName;
        Quantity=quantity;
        Price=price;
        Discount=discount;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getProductId() {
        return ProductId;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public String getPrice() {
        return Price;
    }

    public String getDiscount() {
        return Discount;
    }

}

