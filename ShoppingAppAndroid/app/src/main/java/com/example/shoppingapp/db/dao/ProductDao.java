package com.example.shoppingapp.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoppingapp.db.models.Product;

import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM products WHERE " +
            "((:query IS NULL OR :query = '' OR name LIKE :query)) AND " +
            "((:category IS NULL OR :category = '' OR lower(category) LIKE '%' || lower(:category) || '%'))")
    List<Product> searchProducts(String query, String category);

    @Query("SELECT * FROM products")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE id = :id")
    Product getProductById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);
}
