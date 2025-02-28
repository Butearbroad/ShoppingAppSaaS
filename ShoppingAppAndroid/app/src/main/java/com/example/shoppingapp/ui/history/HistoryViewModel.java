package com.example.shoppingapp.ui.history;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.List;

public class HistoryViewModel extends ViewModel {
    private final MutableLiveData<List<String>> history;

    public HistoryViewModel() {
        history = new MutableLiveData<>();
        loadHistory();
    }

    private void loadHistory() {
        history.setValue(Arrays.asList("Заказ 1", "Заказ 2", "Заказ 3"));
    }

    public LiveData<List<String>> getHistory() {
        return history;
    }
}
