package com.example.shoppingapp.repository;

import android.content.Context;

import com.example.shoppingapp.db.AppDatabase;
import com.example.shoppingapp.db.models.SelectedProduct;
import com.example.shoppingapp.db.dao.SelectedProductDao;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SelectedProductRepository {
    private static SelectedProductRepository instance;
    private final AppDatabase database;
    private final SelectedProductDao selectedProductDao;
    private final Executor executor;

    public interface DataCallback<T> {
        void onDataLoaded(T data);
    }

    private SelectedProductRepository(Context context) {
        database = AppDatabase.getInstance(context);
        selectedProductDao = database.selectedProductDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public static synchronized SelectedProductRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SelectedProductRepository(context);
        }
        return instance;
    }

    public void getAllSelectedProducts(final DataCallback<List<SelectedProduct>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<SelectedProduct> selectedProducts = selectedProductDao.getAllSelectedProducts();
                callback.onDataLoaded(selectedProducts);
            }
        });
    }

    public void insertSelectedProduct(final SelectedProduct selectedProduct) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                selectedProductDao.insertSelectedProduct(selectedProduct);
            }
        });
    }
}
