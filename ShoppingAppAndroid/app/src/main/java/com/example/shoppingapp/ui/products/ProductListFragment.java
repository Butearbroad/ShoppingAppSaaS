package com.example.shoppingapp.ui.products;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shoppingapp.adapters.ProductAdapter;
import com.example.shoppingapp.R;
import com.example.shoppingapp.db.models.Product;
import com.example.shoppingapp.repository.ProductRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductListFragment extends Fragment {
    private ProductRepository productRepository;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private EditText searchInput;
    private Spinner categorySpinner;
    private Button filterButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final List<String> categories = Arrays.asList(
            "Все категории", "Фрукты", "Овощи",
            "Молочные продукты", "Мясо", "Напитки"
    );

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        searchInput = view.findViewById(R.id.search_input);
        categorySpinner = view.findViewById(R.id.category_spinner);
        filterButton = view.findViewById(R.id.filter_button);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        productRepository = ProductRepository.getInstance(requireContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setSelection(0);

        filterButton.setOnClickListener(v -> performSearch());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            String query = searchInput.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            if (category.equals("Все категории")) category = "";

            String finalCategory = category;
            productRepository.syncProductsWithApi(query, category, () -> {
                loadProductsFromDatabase(query, finalCategory);
                swipeRefreshLayout.setRefreshing(false);
            });
        });

        productRepository.syncProductsWithApi("", "", () ->
                loadProductsFromDatabase("", "")
        );

        return view;
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        if (category.equals("Все категории")) category = "";

        String finalCategory = category;
        productRepository.syncProductsWithApi(query, category, () ->
                loadProductsFromDatabase(query, finalCategory)
        );
    }

    private void loadProductsFromDatabase(String query, String category) {
        productRepository.searchProducts(query, category, this::updateUI);
    }

    private void updateUI(List<Product> data) {
        requireActivity().runOnUiThread(() -> {
            productList.clear();
            productList.addAll(data);
            productAdapter.resetSelection();
            productAdapter.notifyDataSetChanged();
        });
    }
}
