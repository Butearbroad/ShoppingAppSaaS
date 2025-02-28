package com.example.shoppingapp.ui.products;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.shoppingapp.db.models.Product;
import com.example.shoppingapp.repository.ProductRepository;
import java.util.List;

public class ProductListViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;
    private final MutableLiveData<List<Product>> productList;

    public ProductListViewModel(@NonNull Application application) {
        super(application);

        productRepository = ProductRepository.getInstance(application.getApplicationContext());
        productList = new MutableLiveData<>();
    }

    public void searchProducts(String query, String category) {
        productRepository.searchProducts(query, category, new ProductRepository.DataCallback<List<Product>>() {
            @Override
            public void onDataLoaded(List<Product> data) {
                productList.postValue(data);
            }
        });
    }

    public LiveData<List<Product>> getProductList() {
        return productList;
    }
}
