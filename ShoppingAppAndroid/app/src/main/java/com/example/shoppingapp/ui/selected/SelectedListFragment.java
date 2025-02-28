package com.example.shoppingapp.ui.selected;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.shoppingapp.R;
import com.example.shoppingapp.adapters.SelectedProductAdapter;
import com.example.shoppingapp.db.AppDatabase;
import com.example.shoppingapp.db.dao.SelectedProductDao;
import com.example.shoppingapp.db.models.Product;
import com.example.shoppingapp.db.models.SelectedProduct;
import com.example.shoppingapp.db.models.SelectedProductDetail;
import java.util.ArrayList;
import java.util.List;

public class SelectedListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SelectedProductAdapter adapter;
    private final List<SelectedProductDetail> selectedProductDetailList = new ArrayList<>();

    public SelectedListFragment() {
        // Пустой конструктор
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_product_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewSelectedProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SelectedProductAdapter(getContext(), selectedProductDetailList);
        recyclerView.setAdapter(adapter);


        new LoadSelectedProductsTask().execute();

        return view;
    }

    private class LoadSelectedProductsTask extends AsyncTask<Void, Void, List<SelectedProductDetail>> {
        @Override
        protected List<SelectedProductDetail> doInBackground(Void... voids) {
            SelectedProductDao selectedDao = AppDatabase.getInstance(getContext()).selectedProductDao();
            List<SelectedProduct> selectedProducts = selectedDao.getAllSelectedProducts();
            List<SelectedProductDetail> details = new ArrayList<>();
            for (SelectedProduct sp : selectedProducts) {
                Product product = AppDatabase.getInstance(getContext()).productDao().getProductById(sp.getProductId());
                if (product != null) {
                    details.add(new SelectedProductDetail(
                            sp.getId(),
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getUnit(),
                            product.getVolume(),
                            sp.getQuantity(),
                            sp.isPurchased()
                    ));
                }
            }
            return details;
        }

        @Override
        protected void onPostExecute(List<SelectedProductDetail> details) {
            selectedProductDetailList.clear();
            selectedProductDetailList.addAll(details);
            adapter.notifyDataSetChanged();
        }
    }
}
