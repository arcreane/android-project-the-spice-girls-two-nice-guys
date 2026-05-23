package com.barometre.myapplication.filters;

public class FilterOptions {

    private String type;
    private float minimumRating;
    private double maxDistanceKm;

    public FilterOptions(String type, float minimumRating, double maxDistanceKm) {
        this.type = type;
        this.minimumRating = minimumRating;
        this.maxDistanceKm = maxDistanceKm;
    }

    public String getType() {
        return type;
    }

    public float getMinimumRating() {
        return minimumRating;
    }

    public double getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public static FilterOptions defaultFilters() {
        return new FilterOptions("All", 0.0f, 0.0);
    }
}