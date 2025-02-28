package com.example.shoppingapp.db.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "selected_products")
public class SelectedProduct {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private final int productId;
    private int quantity;
    private boolean purchased;

    public SelectedProduct(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.purchased = false;
    }

    public int getId() {
        return id;
    }
    public int getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }
    public boolean isPurchased() {
        return purchased;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
