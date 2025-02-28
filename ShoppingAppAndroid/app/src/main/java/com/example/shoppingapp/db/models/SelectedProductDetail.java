package com.example.shoppingapp.db.models;

public class SelectedProductDetail {
    private int selectedProductId;
    private int productId;
    private String productName;
    private double productPrice;
    private String productUnit;
    private int productVolume;
    private int quantity;
    private boolean purchased;

    public SelectedProductDetail(int selectedProductId, int productId, String productName,
                                 double productPrice, String productUnit, int productVolume,
                                 int quantity, boolean purchased) {
        this.selectedProductId = selectedProductId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productUnit = productUnit;
        this.productVolume = productVolume;
        this.quantity = quantity;
        this.purchased = purchased;
    }

    public int getSelectedProductId() {
        return selectedProductId;
    }
    public int getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public double getProductPrice() {
        return productPrice;
    }
    public String getProductUnit() {
        return productUnit;
    }
    public int getProductVolume() {
        return productVolume;
    }
    public int getQuantity() {
        return quantity;
    }
    public boolean isPurchased() {
        return purchased;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public double getTotalSum() {
        return (quantity * productPrice);
    }
}
