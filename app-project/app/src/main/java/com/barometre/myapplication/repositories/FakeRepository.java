package com.barometre.myapplication.repositories;

import com.barometre.myapplication.models.Bar;

import java.util.Collections;
import java.util.List;

public class FakeRepository implements IBarRepository {

    @Override
    public List<Bar> getAllBars() {
        return Collections.emptyList();
    }

    @Override
    public List<Bar> searchBarsByName(String name) {
        return Collections.emptyList();
    }

    @Override
    public List<Bar> getBarsSortedByRating() {
        return Collections.emptyList();
    }

    @Override
    public List<Bar> getBarsByCity(String city) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getAllCities() {
        return Collections.emptyList();
    }

    @Override
    public Bar getBarById(String id) {
        return null;
    }

    @Override
    public void addFavorite(String barId) {

    }

    @Override
    public void removeFavorite(String barId) {

    }

    @Override
    public boolean isFavorite(String barId) {
        return false;
    }

    @Override
    public List<Bar> getFavoriteBars() {
        return Collections.emptyList();
    }

    @Override
    public void cacheBar(Bar bar) {

    }

    @Override
    public List<Bar> getCachedBars() {
        return Collections.emptyList();
    }

    @Override
    public List<Bar> getBarsNearLocation(double latitude, double longitude, double radiusKm) {
        return Collections.emptyList();
    }

    @Override
    public List<Bar> getFilteredBars(String city, List<String> tags, double minRating) {
        return Collections.emptyList();
    }
}