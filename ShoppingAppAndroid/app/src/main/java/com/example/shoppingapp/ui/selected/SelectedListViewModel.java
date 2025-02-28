package com.example.shoppingapp.ui.selected;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.shoppingapp.db.models.SelectedProduct;
import com.example.shoppingapp.repository.SelectedProductRepository;
import java.util.List;

public class SelectedListViewModel extends AndroidViewModel {
    private final SelectedProductRepository selectedProductRepository;
    private final MutableLiveData<List<SelectedProduct>> selectedProducts;

    public SelectedListViewModel(@NonNull Application application) {
        super(application);
        selectedProductRepository = SelectedProductRepository.getInstance(application.getApplicationContext());
        selectedProducts = new MutableLiveData<>();
        loadSelectedProducts();
    }

    private void loadSelectedProducts() {
        selectedProductRepository.getAllSelectedProducts(new SelectedProductRepository.DataCallback<List<SelectedProduct>>() {
            @Override
            public void onDataLoaded(List<SelectedProduct> data) {
                selectedProducts.postValue(data);
            }
        });
    }

    public LiveData<List<SelectedProduct>> getSelectedProducts() {
        return selectedProducts;
    }
}
