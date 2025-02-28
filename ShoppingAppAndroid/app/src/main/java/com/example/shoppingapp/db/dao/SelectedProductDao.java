package com.example.shoppingapp.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.shoppingapp.db.models.SelectedProduct;

import java.util.List;

@Dao
public interface SelectedProductDao {
    @Insert
    void insertSelectedProduct(SelectedProduct selectedProduct);

    @Query("SELECT * FROM selected_products")
    List<SelectedProduct> getAllSelectedProducts();

    @Update
    void updateSelectedProduct(SelectedProduct selectedProduct);

    @Delete
    void deleteSelectedProduct(SelectedProduct selectedProduct);
}
