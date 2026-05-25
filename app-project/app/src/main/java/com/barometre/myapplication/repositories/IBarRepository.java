package com.barometre.myapplication.repositories;

import com.barometre.myapplication.models.Bar;
import java.util.List;

/**
 * Fakerepo and Barrepo (SQLite) both implement this.
 * inform the rest when u change smth in this file
 * add it here first, then implement it in both classes.
 */
public interface IBarRepository {

    // karma — MainActivity / Map

    /** Returns all bars. Used to populate map markers on startup. */
    List<Bar> getAllBars();

    // tristan — RecyclerView / List

    /** Returns bars filtered by name. */
    List<Bar> searchBarsByName(String name);

    /** Returns bars sorted by rating descending. */
    List<Bar> getBarsSortedByRating();

    /** Returns bars filtered by city. */
    List<Bar> getBarsByCity(String city);

    /** Returns all distinct cities available in the data. */
    List<String> getAllCities();

    // peushi — Bar Detail: loading a single bar

    /** Returns a single bar by its ID */
    Bar getBarById(String id);


    // jakub — Favorites & Storage

    /** Marks a bar as favorite. */
    void addFavorite(String barId);

    /** Removes a bar from favorites. */
    void removeFavorite(String barId);

    /** Returns true if the bar is currently a favorite. */
    boolean isFavorite(String barId);

    /** Returns all bars marked as favorites. */
    List<Bar> getFavoriteBars();

    /** Saves a bar to the local cache (used by BroadcastReceiver offline mode). */
    void cacheBar(Bar bar);

    /** Returns all locally cached bars (used when offline). */
    List<Bar> getCachedBars();

    List<Bar> getRecentlyViewedBars(int limit);

    // sasha — GPS / Filters

    /**
     * Returns bars within a given radius (in km) from a GPS point.
     * "bars near me" feature.
     */
    List<Bar> getBarsNearLocation(double latitude, double longitude, double radiusKm);

    /**
     * Returns bars matching all active filters*/
    List<Bar> getFilteredBars(String city, List<String> tags, double minRating);
}