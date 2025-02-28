package com.example.shoppingapp.network;

import com.example.shoppingapp.db.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/Product")
    Call<List<Product>> getAllProducts();

    @GET("/api/ProductSearch")
    Call<List<Product>> searchProducts(@Query("name") String name, @Query("category") String category);
}
