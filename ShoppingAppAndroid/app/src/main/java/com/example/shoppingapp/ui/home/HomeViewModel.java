package com.example.shoppingapp.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.shoppingapp.db.models.Product;
import com.example.shoppingapp.repository.ProductRepository;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;
    private final MutableLiveData<List<Product>> productList;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        productRepository = ProductRepository.getInstance(application.getApplicationContext());
        productList = new MutableLiveData<>();
        loadProducts();
    }

    private void loadProducts() {
        productRepository.getAllProducts(new ProductRepository.DataCallback<List<Product>>() {
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
