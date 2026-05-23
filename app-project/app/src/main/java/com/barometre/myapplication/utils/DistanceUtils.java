package com.barometre.myapplication.utils;

import android.location.Location;

public class DistanceUtils {

    public static double calculateDistanceKm(
            double userLat,
            double userLng,
            double barLat,
            double barLng
    ) {
        float[] results = new float[1];

        Location.distanceBetween(
                userLat,
                userLng,
                barLat,
                barLng,
                results
        );

        return results[0] / 1000.0;
    }

    public static String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            int meters = (int) Math.round(distanceKm * 1000);
            return meters + " m";
        }

        return String.format("%.1f km", distanceKm);
    }
}
