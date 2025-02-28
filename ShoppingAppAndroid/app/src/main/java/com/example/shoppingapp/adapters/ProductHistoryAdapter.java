package com.example.shoppingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingapp.R;
import com.example.shoppingapp.db.models.ProductHistory;
import java.util.List;
import java.util.Locale;

public class ProductHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public interface HistoryListItem {
        boolean isHeader();
    }

    public static class HistoryHeaderItem implements HistoryListItem {
        private String headerTitle;
        private double groupTotal;

        public HistoryHeaderItem(String headerTitle, double groupTotal) {
            this.headerTitle = headerTitle;
            this.groupTotal = groupTotal;
        }

        public String getHeaderTitle() { return headerTitle; }
        public double getGroupTotal() { return groupTotal; }
        @Override
        public boolean isHeader() { return true; }
    }

    public static class HistoryDataItem implements HistoryListItem {
        private ProductHistory history;

        public HistoryDataItem(ProductHistory history) {
            this.history = history;
        }

        public ProductHistory getHistory() { return history; }
        @Override
        public boolean isHeader() { return false; }
    }

    private List<HistoryListItem> items;

    public ProductHistoryAdapter(List<HistoryListItem> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ViewHolder для заголовка группы
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupHeaderTitle;
        TextView tvGroupTotal;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvGroupHeaderTitle = itemView.findViewById(R.id.tvGroupHeaderTitle);
            tvGroupTotal = itemView.findViewById(R.id.tvGroupTotal);
        }
    }

    // Обновлённый ViewHolder для элемента истории
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvProductDetails;
        TextView tvTotal;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDetails = itemView.findViewById(R.id.tvProductDetails);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_group_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_HEADER) {
            HistoryHeaderItem headerItem = (HistoryHeaderItem) items.get(position);
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvGroupHeaderTitle.setText(headerItem.getHeaderTitle());
            headerHolder.tvGroupTotal.setText(String.format(Locale.getDefault(), "Итого: %.2f₽", headerItem.getGroupTotal()));
        } else {
            HistoryDataItem dataItem = (HistoryDataItem) items.get(position);
            ProductHistory history = dataItem.getHistory();
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.tvProductName.setText(history.getProductName());
            // Объединяем количество и информацию о цене в один текст
            String details = String.format(Locale.getDefault(), "Кол-во: %d, Цена: %.2f₽ за %.2f %s",
                    history.getQuantity(), history.getPrice(), history.getVolume(), history.getUnit());
            itemHolder.tvProductDetails.setText(details);
            itemHolder.tvTotal.setText(String.format(Locale.getDefault(), "Сумма: %.2f₽", history.getTotal()));
        }
    }

    public void updateList(List<HistoryListItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
}
