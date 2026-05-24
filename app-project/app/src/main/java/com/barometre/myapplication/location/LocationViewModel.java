package com.barometre.myapplication.location;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {

    private final MutableLiveData<Location> userLocation = new MutableLiveData<>();

    public LiveData<Location> getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location location) {
        userLocation.setValue(location);
    }

    public boolean hasLocation() {
        return userLocation.getValue() != null;
    }

    public Location getCurrentLocationValue() {
        return userLocation.getValue();
    }
}
