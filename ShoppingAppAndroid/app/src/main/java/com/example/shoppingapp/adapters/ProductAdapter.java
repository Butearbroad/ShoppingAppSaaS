package com.example.shoppingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.shoppingapp.db.AppDatabase;
import com.example.shoppingapp.db.models.Product;
import com.example.shoppingapp.db.models.ProductHistory;
import com.example.shoppingapp.db.models.SelectedProduct;
import com.example.shoppingapp.repository.SelectedProductRepository;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private int selectedPosition = -1;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void resetSelection() {
        int previous = selectedPosition;
        selectedPosition = -1;
        if (previous != -1) {
            notifyItemChanged(previous);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);

        if (position == selectedPosition) {
            holder.quantityLayout.setVisibility(View.VISIBLE);
            holder.quantityEdit.setText("1");
        } else {
            holder.quantityLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        LinearLayout quantityLayout;
        Button addButton, minusButton, plusButton;
        EditText quantityEdit;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            quantityLayout = itemView.findViewById(R.id.quantity_layout);
            addButton = itemView.findViewById(R.id.add_button);
            minusButton = itemView.findViewById(R.id.minus_button);
            plusButton = itemView.findViewById(R.id.plus_button);
            quantityEdit = itemView.findViewById(R.id.quantity_edit);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                int previous = selectedPosition;
                selectedPosition = (position == selectedPosition) ? -1 : position;

                if (previous != -1) notifyItemChanged(previous);
                if (selectedPosition != -1) notifyItemChanged(selectedPosition);
            });

            minusButton.setOnClickListener(v -> updateQuantity(-1));
            plusButton.setOnClickListener(v -> updateQuantity(1));

            addButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    addProductToCart();
                    resetSelection();
                }
            });
        }

        private void updateQuantity(int delta) {
            try {
                int qty = Integer.parseInt(quantityEdit.getText().toString());
                qty = Math.max(qty + delta, 1);
                quantityEdit.setText(String.valueOf(qty));
            } catch (NumberFormatException e) {
                quantityEdit.setText("1");
            }
        }

        private void addProductToCart() {
            try {
                int qty = Integer.parseInt(quantityEdit.getText().toString());
                Product product = productList.get(getAdapterPosition());

                SelectedProductRepository.getInstance(itemView.getContext())
                        .insertSelectedProduct(new SelectedProduct(product.getId(), qty));

                new Thread(() -> {
                    ProductHistory history = new ProductHistory(
                            product.getName(),
                            qty,
                            product.getPrice(),
                            product.getVolume(),
                            product.getUnit(),
                            System.currentTimeMillis(),
                            1  // 1 - выбранный товар
                    );
                    AppDatabase.getInstance(itemView.getContext())
                            .productHistoryDao().insertHistory(history);
                }).start();

                Toast.makeText(itemView.getContext(),
                        "Товар добавлен в корзину",
                        Toast.LENGTH_SHORT).show();

            } catch (NumberFormatException e) {
                quantityEdit.setText("1");
            }
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText(String.format("%.2f ₽ за %d %s", product.getPrice(), product.getVolume(), product.getUnit()));
        }
    }
}