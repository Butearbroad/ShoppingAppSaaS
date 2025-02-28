package com.example.shoppingapp.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.shoppingapp.db.models.ProductHistory;
import java.util.List;

@Dao
public interface ProductHistoryDao {
    @Insert
    void insertHistory(ProductHistory history);

    @Update
    void updateHistory(ProductHistory history);

    @Delete
    void deleteHistory(ProductHistory history);

    @Query("DELETE FROM product_history WHERE productName = :name AND type = :type")
    void deleteHistoryByProductAndType(String name, int type);
    @Query("SELECT * FROM product_history WHERE type = :type AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<ProductHistory> getHistoryByTypeAndDateRange(int type, long startDate, long endDate);

    @Query("SELECT * FROM product_history WHERE type = :type ORDER BY date DESC")
    List<ProductHistory> getAllHistoryByType(int type);
}
