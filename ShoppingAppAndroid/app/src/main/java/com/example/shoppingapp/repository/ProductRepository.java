package com.example.shoppingapp.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.shoppingapp.db.AppDatabase;
import com.example.shoppingapp.db.models.Product;
import com.example.shoppingapp.db.dao.ProductDao;
import com.example.shoppingapp.network.ApiService;
import com.example.shoppingapp.network.RetrofitClient;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class ProductRepository {
    private static ProductRepository instance;
    private final ProductDao productDao;
    private final ApiService apiService;
    private final Executor executor;

    public interface DataCallback<T> {
        void onDataLoaded(T data);
    }

    public interface SyncCallback {
        void onSyncComplete();
    }

    private ProductRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        productDao = database.productDao();
        apiService = RetrofitClient.getApiService();
        executor = Executors.newSingleThreadExecutor();
    }

    public static synchronized ProductRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProductRepository(context);
        }
        return instance;
    }

    public void getAllProducts(DataCallback<List<Product>> callback) {
        executor.execute(() -> {
            List<Product> products = productDao.getAllProducts();
            new Handler(Looper.getMainLooper()).post(() -> callback.onDataLoaded(products));
        });
    }

    public void searchProducts(String query, String category, DataCallback<List<Product>> callback) {
        executor.execute(() -> {
            String processedQuery = (query == null || query.isEmpty()) ? null : "%" + query + "%";
            String processedCategory = (category == null || category.isEmpty()) ? null : category;

            List<Product> products;
            if (processedQuery == null && processedCategory == null) {
                products = productDao.getAllProducts();
            } else {
                products = productDao.searchProducts(processedQuery, processedCategory);
            }

            new Handler(Looper.getMainLooper()).post(() -> callback.onDataLoaded(products));
        });
    }

    public void syncProductsWithApi(String query, String category, SyncCallback callback) {
        executor.execute(() -> {
            try {
                Call<List<Product>> call;
                if ((query == null || query.isEmpty()) && (category == null || category.isEmpty())) {
                    call = apiService.getAllProducts();
                } else {
                    call = apiService.searchProducts(query, category);
                }
                Response<List<Product>> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    List<Product> apiProducts = response.body();
                    List<Product> localProducts = productDao.getAllProducts();

                    for (Product local : localProducts) {
                        boolean exists = apiProducts.stream().anyMatch(p -> p.getId() == local.getId());
                        if (!exists) {
                            productDao.deleteProduct(local);
                        }
                    }

                    for (Product apiProd : apiProducts) {
                        Product local = productDao.getProductById(apiProd.getId());
                        if (local == null) {
                            productDao.insertProduct(apiProd);
                        } else if (!local.equals(apiProd)) {
                            productDao.updateProduct(apiProd);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                new Handler(Looper.getMainLooper()).post(callback::onSyncComplete);
            }
        });
    }
}
