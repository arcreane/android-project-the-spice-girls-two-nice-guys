package com.barometre.myapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.repositories.FakeRepository;
import com.barometre.myapplication.repositories.IBarRepository;
import com.barometre.myapplication.filters.FilterOptions;

import java.util.Collections;
import java.util.List;

//communication between fragments
public class BarViewModel extends ViewModel {

    // swap fakerepo for actual one later
    private final IBarRepository repository = new FakeRepository();
    //all bars
    private final MutableLiveData<List<Bar>> bars = new MutableLiveData<>();
    //currently selected bar (map marker tap)
    private final MutableLiveData<Bar> selectedBar = new MutableLiveData<>();
    // filtered bars
    private final MutableLiveData<List<Bar>> filteredBars = new MutableLiveData<>();
    //offline mode flag
    private final MutableLiveData<Boolean> isOffline = new MutableLiveData<>(false);

    public void loadAllBars() {
        List<Bar> all = repository.getAllBars();
        bars.setValue(all);
        filteredBars.setValue(all);
    }

    public void sortByRating(){
        filteredBars.setValue(repository.getBarsSortedByRating());
    }

    public void applyFilters(String city, java.util.List<String> tags, double minRating) {
        filteredBars.setValue(repository.getFilteredBars(city, tags, minRating));
    }

    public void selectBar(Bar bar){
        selectedBar.setValue(bar);
    }

    public void applyFilterOptions(FilterOptions options) {
        if (options == null ) {
            filteredBars.setValue(bars.getValue());
            return;
        }

        List<String> tags = null;

        if (options.getType() != null && !options.getType().equalsIgnoreCase("All")) {
            tags = Collections.singletonList(options.getType());

        }

        applyFilters(null, tags, options.getMinimumRating());
    }

    public void setOfflineMode(boolean offline) {
        isOffline.setValue(offline);
        if (offline) {
            List<Bar> cached = repository.getCachedBars();
            bars.setValue(cached);
            filteredBars.setValue(cached);
        } else {
            loadAllBars();
        }
    }
    // once gps is implemented uncomment
    // private final MutableLiveData<Location> userLocation = new MutableLiveData<>();

    public LiveData<List<Bar>> getBars() {return bars;}
    public LiveData<List<Bar>> getFilteredBars() {return filteredBars;}
    public LiveData<Bar> getSelectedBar() {return selectedBar;}
    public LiveData<Boolean> getIsOffline() {return isOffline;}

    // public liveData<Location> getUserLocation() {return userLocation;}
    // public void setUserLocation(Location location) { userLocation.setValue(location);}
}
