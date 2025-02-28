package com.example.shoppingapp.ui.home;


import com.example.shoppingapp.ui.products.ProductListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.example.shoppingapp.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        EditText searchBar = view.findViewById(R.id.search_bar);
        searchBar.setFocusable(false); // чтобы не открывалась клавиатура сразу

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("activateSearch", true);
                ProductListFragment productListFragment = new ProductListFragment();
                productListFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, productListFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}
