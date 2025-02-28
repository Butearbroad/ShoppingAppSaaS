package com.example.shoppingapp.db.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product_history")
public class ProductHistory {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String productName;
    private int quantity;
    private double price;
    private double volume;
    private String unit;
    private long date;
    private int type;

    public ProductHistory(String productName, int quantity, double price, double volume, String unit, long date, int type) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.volume = volume;
        this.unit = unit;
        this.date = date;
        this.type = type;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public double getTotal() {
        return price * quantity;
    }
}
