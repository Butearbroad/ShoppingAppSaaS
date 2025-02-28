package com.example.shoppingapp.ui.history;

import com.example.shoppingapp.R;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shoppingapp.adapters.ProductHistoryAdapter;
import com.example.shoppingapp.adapters.ProductHistoryAdapter.HistoryDataItem;
import com.example.shoppingapp.adapters.ProductHistoryAdapter.HistoryHeaderItem;
import com.example.shoppingapp.adapters.ProductHistoryAdapter.HistoryListItem;
import com.example.shoppingapp.db.AppDatabase;
import com.example.shoppingapp.db.models.ProductHistory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private Button btnPurchased, btnSelected;
    private Spinner spinnerPeriod, spinnerGrouping;
    private TextView tvOverallTotal;
    private RecyclerView recyclerView;
    private ProductHistoryAdapter adapter;
    private int currentType = 0;

    private long customStartDate = 0, customEndDate = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        btnPurchased = view.findViewById(R.id.btnPurchased);
        btnSelected = view.findViewById(R.id.btnSelected);
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod);
        spinnerGrouping = view.findViewById(R.id.spinnerGrouping);
        tvOverallTotal = view.findViewById(R.id.tvOverallTotal);
        recyclerView = view.findViewById(R.id.recyclerViewHistory);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Настройка адаптера для спиннеров
        ArrayAdapter<String> periodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                new String[]{"Без периода", "Сегодня", "Неделя", "Месяц", "Декада", "Год"});
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(periodAdapter);

        ArrayAdapter<String> groupingAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                new String[]{"По дням", "По неделям", "По месяцам"});
        groupingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrouping.setAdapter(groupingAdapter);

        btnPurchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentType = 0;
                loadHistoryData();
            }
        });

        btnSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentType = 1;
                loadHistoryData();
            }
        });

        spinnerPeriod.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int i, long l) {
                String selected = (String) adapterView.getItemAtPosition(i);
                    loadHistoryData();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {}
        });

        spinnerGrouping.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> adapterView, View view, int i, long l) {
                loadHistoryData();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> adapterView) {}
        });

        loadHistoryData();

        return view;
    }

    private void pickCustomPeriod() {
        Calendar calendar = Calendar.getInstance();
        customStartDate = calendar.getTimeInMillis();
        customEndDate = calendar.getTimeInMillis();
        loadHistoryData();
    }

    private void loadHistoryData() {
        String periodSelection = (String) spinnerPeriod.getSelectedItem();
        final long[] range = getDateRange(periodSelection);
        final long startDate = range[0];
        final long endDate = range[1];

        String groupingSelection = (String) spinnerGrouping.getSelectedItem();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ProductHistory> historyList;
                if(startDate == 0 && endDate == Long.MAX_VALUE) {
                    historyList = AppDatabase.getInstance(getContext())
                            .productHistoryDao().getAllHistoryByType(currentType);
                } else {
                    historyList = AppDatabase.getInstance(getContext())
                            .productHistoryDao().getHistoryByTypeAndDateRange(currentType, startDate, endDate);
                }

                List<HistoryListItem> groupedList = groupHistoryData(historyList, groupingSelection);

                double overallTotal = 0;
                for(ProductHistory ph : historyList) {
                    overallTotal += ph.getTotal();
                }

                if(getActivity() != null) {
                    double finalOverallTotal = overallTotal;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvOverallTotal.setText(String.format(Locale.getDefault(), "Общий итог: %.2f₽", finalOverallTotal));
                            adapter.updateList(groupedList);
                        }
                    });
                }
            }
        }).start();
    }

    private long[] getDateRange(String periodSelection) {
        Calendar cal = Calendar.getInstance();
        long start = 0;
        long end = Long.MAX_VALUE;
        switch (periodSelection) {
            case "Без периода":
                start = 0;
                end = Long.MAX_VALUE;
                break;
            case "Сегодня":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                start = cal.getTimeInMillis();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                end = cal.getTimeInMillis() - 1;
                break;
            case "Неделя":
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                start = cal.getTimeInMillis();
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                end = cal.getTimeInMillis() - 1;
                break;
            case "Месяц":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                start = cal.getTimeInMillis();
                cal.add(Calendar.MONTH, 1);
                end = cal.getTimeInMillis() - 1;
                break;
            case "Декада":
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int decadeStart = ((day - 1) / 10) * 10 + 1;
                cal.set(Calendar.DAY_OF_MONTH, decadeStart);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                start = cal.getTimeInMillis();
                cal.add(Calendar.DAY_OF_MONTH, 10);
                end = cal.getTimeInMillis() - 1;
                break;
            case "Год":
                cal.set(Calendar.DAY_OF_YEAR, 1);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                start = cal.getTimeInMillis();
                cal.add(Calendar.YEAR, 1);
                end = cal.getTimeInMillis() - 1;
                break;
            case "Свой период":
                start = customStartDate;
                end = customEndDate;
                break;
        }
        return new long[]{start, end};
    }

    private List<HistoryListItem> groupHistoryData(List<ProductHistory> historyList, String groupingMode) {
        List<HistoryListItem> groupedList = new ArrayList<>();
        if(historyList == null || historyList.isEmpty()) return groupedList;

        SimpleDateFormat sdf;
        switch (groupingMode) {
            case "По неделям":
                sdf = new SimpleDateFormat("w 'неделя' yyyy", Locale.getDefault());
                break;
            case "По месяцам":
                sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                break;
            case "По дням":
            default:
                sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                break;
        }

        String currentGroup = "";
        double groupTotal = 0;
        for(ProductHistory ph : historyList) {
            String groupLabel = sdf.format(ph.getDate());
            if(!groupLabel.equals(currentGroup)) {
                if(!currentGroup.isEmpty()){
                    groupedList.add(new HistoryHeaderItem(currentGroup, groupTotal));
                }
                currentGroup = groupLabel;
                groupTotal = 0;
                groupedList.add(new HistoryHeaderItem(currentGroup, 0));
            }
            groupTotal += ph.getTotal();
            groupedList.add(new HistoryDataItem(ph));
        }
        for(int i = groupedList.size()-1; i >= 0; i--) {
            HistoryListItem item = groupedList.get(i);
            if(item.isHeader()) {
                HistoryHeaderItem header = (HistoryHeaderItem) item;
                double total = 0;
                for(int j = i+1; j < groupedList.size(); j++){
                    HistoryListItem next = groupedList.get(j);
                    if(next.isHeader()) break;
                    total += ((HistoryDataItem) next).getHistory().getTotal();
                }
                header = new HistoryHeaderItem(header.getHeaderTitle(), total);
                groupedList.set(i, header);
            }
        }
        return groupedList;
    }
}
