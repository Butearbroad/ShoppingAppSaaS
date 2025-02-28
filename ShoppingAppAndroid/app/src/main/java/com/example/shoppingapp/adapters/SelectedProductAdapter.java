package com.example.shoppingapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingapp.R;
import com.example.shoppingapp.db.AppDatabase;
import com.example.shoppingapp.db.models.ProductHistory;
import com.example.shoppingapp.db.models.SelectedProduct;
import com.example.shoppingapp.db.models.SelectedProductDetail;
import java.util.List;

public class SelectedProductAdapter extends RecyclerView.Adapter<SelectedProductAdapter.SelectedProductViewHolder> {

    private final List<SelectedProductDetail> selectedProductList;
    private final Context context;

    public SelectedProductAdapter(Context context, List<SelectedProductDetail> selectedProductList) {
        this.context = context;
        this.selectedProductList = selectedProductList;
    }

    @NonNull
    @Override
    public SelectedProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_product, parent, false);
        return new SelectedProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedProductViewHolder holder, int position) {
        SelectedProductDetail item = selectedProductList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return selectedProductList.size();
    }

    public class SelectedProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvTotal;
        View itemContainer;

        public SelectedProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName     = itemView.findViewById(R.id.tvProductName);
            tvDetails  = itemView.findViewById(R.id.tvProductDetails);
            tvTotal    = itemView.findViewById(R.id.tvTotal);
            itemContainer = itemView;
        }

        public void bind(SelectedProductDetail item) {
            tvName.setText(item.getProductName());

            String details = String.format("Цена: %.2f | Объём: %d %s | Кол-во: %d",
                    item.getProductPrice(),
                    item.getProductVolume(),
                    item.getProductUnit(),
                    item.getQuantity());
            tvDetails.setText(details);
            tvTotal.setText(String.format("Сумма: %.2f", item.getTotalSum()));

            // Применяем зачеркивание, если товар отмечен как купленный
            if (item.isPurchased()) {
                tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvDetails.setPaintFlags(tvDetails.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTotal.setPaintFlags(tvTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvName.setPaintFlags(tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvDetails.setPaintFlags(tvDetails.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTotal.setPaintFlags(tvTotal.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            itemContainer.setOnClickListener(v -> {
                item.setPurchased(!item.isPurchased());
                new UpdateSelectedProductTask(item).execute();

                if (item.isPurchased()) {
                    new Thread(() -> {
                        ProductHistory history = new ProductHistory(
                                item.getProductName(),
                                item.getQuantity(),
                                item.getProductPrice(),
                                item.getProductVolume(),
                                item.getProductUnit(),
                                System.currentTimeMillis(),
                                0  // 0 - купленный товар
                        );
                        AppDatabase.getInstance(context)
                                .productHistoryDao().insertHistory(history);
                    }).start();
                } else {
                    new Thread(() -> {
                        AppDatabase.getInstance(context)
                                .productHistoryDao().deleteHistoryByProductAndType(item.getProductName(), 0);
                    }).start();
                }
                notifyItemChanged(getAdapterPosition());
            });

            itemContainer.setOnLongClickListener(v -> {
                showActionsDialog(item, getAdapterPosition());
                return true;
            });
        }

        private void showActionsDialog(SelectedProductDetail item, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Выберите действие");
            String[] actions = {"Удалить", "Изменить"};
            builder.setItems(actions, (dialog, which) -> {
                if (which == 0) {
                    new DeleteSelectedProductTask(item, position).execute();
                } else if (which == 1) {
                    showEditDialog(item, position);
                }
            });
            builder.show();
        }

        private void showEditDialog(SelectedProductDetail item, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Изменить запись");
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_selected_product, null);
            EditText etQuantity = dialogView.findViewById(R.id.etEditQuantity);
            etQuantity.setText(String.valueOf(item.getQuantity()));
            builder.setView(dialogView);
            builder.setPositiveButton("Сохранить", (dialog, which) -> {
                int newQuantity = Integer.parseInt(etQuantity.getText().toString());
                item.setQuantity(newQuantity);
                new UpdateSelectedProductTask(item).execute();
                notifyItemChanged(position);
            });
            builder.setNegativeButton("Отмена", null);
            builder.show();
        }
    }

    private class UpdateSelectedProductTask extends AsyncTask<Void, Void, Void> {
        private final SelectedProductDetail item;

        public UpdateSelectedProductTask(SelectedProductDetail item) {
            this.item = item;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Преобразуем detail в объект SelectedProduct для обновления БД
            SelectedProduct sp = new SelectedProduct(item.getProductId(), item.getQuantity());
            sp.setId(item.getSelectedProductId());
            sp.setPurchased(item.isPurchased());
            AppDatabase.getInstance(context).selectedProductDao().updateSelectedProduct(sp);
            return null;
        }
    }

    private class DeleteSelectedProductTask extends AsyncTask<Void, Void, Void> {
        private final SelectedProductDetail item;
        private final int position;

        public DeleteSelectedProductTask(SelectedProductDetail item, int position) {
            this.item = item;
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SelectedProduct sp = new SelectedProduct(item.getProductId(), item.getQuantity());
            sp.setId(item.getSelectedProductId());
            AppDatabase.getInstance(context).selectedProductDao().deleteSelectedProduct(sp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            selectedProductList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
