package com.barometre.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.barometre.myapplication.filters.FilterOptions;
import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.repositories.FakeRepository;
import com.barometre.myapplication.repositories.IBarRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BarViewModel extends ViewModel {

    private IBarRepository repository = new FakeRepository();

    private final MutableLiveData<List<Bar>> filteredBars = new MutableLiveData<>();
    private final MutableLiveData<Bar> selectedBar = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isOffline = new MutableLiveData<>(false);

    private String searchQuery = "";
    private boolean sortByRatingEnabled = false;
    private FilterOptions activeFilters = FilterOptions.defaultFilters();

    public void init(android.content.Context context) {
        if (repository instanceof FakeRepository) {
            repository = new com.barometre.myapplication.repositories.BarRepository(context);
        }
    }

    public void loadAllBars() {
        refresh();
    }

    public void selectBar(Bar bar) {
        selectedBar.setValue(bar);
    }

    public void setOfflineMode(boolean offline) {
        isOffline.setValue(offline);
        if (offline) {
            filteredBars.setValue(repository.getCachedBars());
        } else {
            refresh();
        }
    }

    public void setSearchQuery(String query) {
        searchQuery = query != null ? query.trim() : "";
        refresh();
    }

    public void sortByRating() {
        sortByRatingEnabled = !sortByRatingEnabled;
        refresh();
    }

    public void setSortByRating(boolean enabled) {
        sortByRatingEnabled = enabled;
        refresh();
    }

    public boolean isSortByRatingEnabled() {
        return sortByRatingEnabled;
    }

    public void setFilters(FilterOptions filters) {
        activeFilters = filters != null ? filters : FilterOptions.defaultFilters();
        refresh();
    }

    public void setTypeFilter(String type) {
        activeFilters = new FilterOptions(
                type != null ? type : "All",
                activeFilters.getMinimumRating(),
                activeFilters.getMaxDistanceKm()
        );
        refresh();
    }

    public void applyFilters(String city, List<String> tags, double minRating) {
        filteredBars.setValue(repository.getFilteredBars(city, tags, minRating));
    }

    public void applyFilterOptions(FilterOptions options) {
        if (options == null) {
            setFilters(FilterOptions.defaultFilters());
            return;
        }
        setFilters(options);
    }

    public void showBarsNearLocation(double latitude, double longitude, double radiusKm) {
        filteredBars.setValue(repository.getBarsNearLocation(latitude, longitude, radiusKm));
    }

    private void refresh() {
        List<Bar> result;

        boolean hasSearch = !searchQuery.isEmpty();
        boolean hasTypeFilter = !"All".equals(activeFilters.getType());
        boolean hasRatingFilter = activeFilters.getMinimumRating() > 0;

        if (hasSearch) {
            result = repository.searchBarsByName(searchQuery);
        } else if (hasTypeFilter || hasRatingFilter) {
            List<String> tags = hasTypeFilter
                    ? Collections.singletonList(activeFilters.getType())
                    : null;
            result = repository.getFilteredBars(null, tags, activeFilters.getMinimumRating());
        } else {
            result = repository.getAllBars();
        }

        if (sortByRatingEnabled) {
            result = new ArrayList<>(result);
            result.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
        }

        filteredBars.setValue(result);
    }

    public LiveData<List<Bar>> getBars() { return filteredBars; }
    public LiveData<List<Bar>> getFilteredBars() { return filteredBars; }
    public LiveData<Bar> getSelectedBar() { return selectedBar; }
    public LiveData<Boolean> getIsOffline() { return isOffline; }
}
