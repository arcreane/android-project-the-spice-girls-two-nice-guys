package com.barometre.myapplication.filters;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FilterViewModel extends ViewModel {

    private final MutableLiveData<FilterOptions> selectedFilters =
            new MutableLiveData<>(FilterOptions.defaultFilters());

    public LiveData<FilterOptions> getSelectedFilters() {
        return selectedFilters;
    }

    public void setSelectedFilters(FilterOptions filters) {
        selectedFilters.setValue(filters);
    }

    public void resetFilters() {
        selectedFilters.setValue(FilterOptions.defaultFilters());
    }
}