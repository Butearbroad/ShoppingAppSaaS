package com.example.shoppingapp.db.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey
    private final int id;
    private final String name;
    private final double price;
    private final String category;
    private final String type;
    private final String unit;
    private final int volume;

    // Геттеры, сеттеры, конструктор и переопределение equals()/hashCode() для сравнения
    // Например:
    public Product(int id, String name, double price, String category, String type, String unit, int volume) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.type = type;
        this.unit = unit;
        this.volume = volume;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    public int getVolume() {
        return volume;
    }

    // ... геттеры и сеттеры
}
