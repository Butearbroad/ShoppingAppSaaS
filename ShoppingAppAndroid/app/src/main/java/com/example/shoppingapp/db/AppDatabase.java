package com.example.shoppingapp.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.shoppingapp.db.dao.ProductDao;
import com.example.shoppingapp.db.dao.SelectedProductDao;
import com.example.shoppingapp.db.dao.ProductHistoryDao;
import com.example.shoppingapp.db.models.Product;
import com.example.shoppingapp.db.models.SelectedProduct;
import com.example.shoppingapp.db.models.ProductHistory;

@Database(entities = {Product.class, SelectedProduct.class, ProductHistory.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ProductDao productDao();
    public abstract SelectedProductDao selectedProductDao();
    public abstract ProductHistoryDao productHistoryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
