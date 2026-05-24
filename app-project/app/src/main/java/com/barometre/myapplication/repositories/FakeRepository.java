package com.barometre.myapplication.repositories;

import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.utils.DistanceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FakeRepository implements IBarRepository {

    private static final List<Bar> ALL_BARS = new ArrayList<>(Arrays.asList(
            new Bar("1", "Le Syndicat", "rue du Faubourg Saint-Denis", "51", "75010", "Paris",
                    "+33 9 51 18 76 06", "lesyndicat.org", "Mar-Sam 18h-2h",
                    48.8706, 2.3522, 4.7, Arrays.asList("cocktail", "speakeasy"), null),
            new Bar("2", "Little Red Door", "rue Charlot", "60", "75003", "Paris",
                    "+33 1 42 71 19 32", "lrdparis.com", "Lun-Sam 18h-2h",
                    48.8621, 2.3607, 4.6, Arrays.asList("cocktail", "design"), null),
            new Bar("3", "Bisou", "rue Saint-Sabin", "3", "75011", "Paris",
                    "+33 1 43 38 58 99", "", "Lun-Sam 17h-1h",
                    48.8567, 2.3699, 4.4, Arrays.asList("wine", "vin naturel"), null),
            new Bar("4", "Candelaria", "rue de Bretagne", "52", "75003", "Paris",
                    "+33 1 42 74 41 28", "", "Mar-Sam 18h-2h",
                    48.8632, 2.3618, 4.5, Arrays.asList("cocktail", "mezcal", "speakeasy"), null),
            new Bar("5", "Sherry Butt", "rue Amelot", "20", "75011", "Paris",
                    "+33 1 43 55 14 08", "", "Tlj 18h-2h",
                    48.8581, 2.3691, 4.3, Arrays.asList("beer", "craft"), null),
            new Bar("6", "Le Perchoir Marais", "rue de la Verrerie", "14", "75004", "Paris",
                    "+33 1 48 04 09 36", "leperchoir.fr", "Tlj 18h-2h",
                    48.8558, 2.3524, 4.8, Arrays.asList("rooftop", "cocktail"), null),
            new Bar("7", "Glass", "rue de Douai", "7", "75009", "Paris",
                    "+33 1 40 16 12 35", "", "Mar-Sam 19h-2h",
                    48.8812, 2.3318, 4.2, Arrays.asList("pub", "rock"), null),
            new Bar("8", "La Mezcaleria", "rue Oberkampf", "110", "75011", "Paris",
                    "+33 1 43 55 87 65", "", "Tlj 18h-2h",
                    48.8649, 2.3772, 4.1, Arrays.asList("cocktail", "mezcal"), null)
    ));

    private final Set<String> favoriteIds = new HashSet<>();
    private final List<Bar> cachedBars = new ArrayList<>();

    @Override
    public List<Bar> getAllBars() {
        return new ArrayList<>(ALL_BARS);
    }

    @Override
    public List<Bar> searchBarsByName(String name) {
        if (name == null || name.isEmpty()) return getAllBars();
        String lower = name.toLowerCase();
        List<Bar> result = new ArrayList<>();
        for (Bar bar : ALL_BARS) {
            if (bar.getName().toLowerCase().contains(lower)) result.add(bar);
        }
        return result;
    }

    @Override
    public List<Bar> getBarsSortedByRating() {
        List<Bar> sorted = new ArrayList<>(ALL_BARS);
        sorted.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
        return sorted;
    }

    @Override
    public List<Bar> getBarsByCity(String city) {
        List<Bar> result = new ArrayList<>();
        for (Bar bar : ALL_BARS) {
            if (bar.getCity().equalsIgnoreCase(city)) result.add(bar);
        }
        return result;
    }

    @Override
    public List<String> getAllCities() {
        Set<String> cities = new HashSet<>();
        for (Bar bar : ALL_BARS) cities.add(bar.getCity());
        return new ArrayList<>(cities);
    }

    @Override
    public Bar getBarById(String id) {
        for (Bar bar : ALL_BARS) {
            if (bar.getId().equals(id)) return bar;
        }
        return null;
    }

    @Override
    public void addFavorite(String barId) {
        favoriteIds.add(barId);
        for (Bar bar : ALL_BARS) {
            if (bar.getId().equals(barId)) bar.setFavorite(true);
        }
    }

    @Override
    public void removeFavorite(String barId) {
        favoriteIds.remove(barId);
        for (Bar bar : ALL_BARS) {
            if (bar.getId().equals(barId)) bar.setFavorite(false);
        }
    }

    @Override
    public boolean isFavorite(String barId) {
        return favoriteIds.contains(barId);
    }

    @Override
    public List<Bar> getFavoriteBars() {
        List<Bar> result = new ArrayList<>();
        for (Bar bar : ALL_BARS) {
            if (favoriteIds.contains(bar.getId())) result.add(bar);
        }
        return result;
    }

    @Override
    public void cacheBar(Bar bar) {
        cachedBars.add(bar);
    }

    @Override
    public List<Bar> getCachedBars() {
        return new ArrayList<>(cachedBars);
    }

    @Override
    public List<Bar> getBarsNearLocation(double latitude, double longitude, double radiusKm) {
        List<Bar> result = new ArrayList<>();
        for (Bar bar : ALL_BARS) {
            if (!bar.hasCoordinates()) continue;
            double dist = DistanceUtils.calculateDistanceKm(latitude, longitude,
                    bar.getLatitude(), bar.getLongitude());
            if (dist <= radiusKm) result.add(bar);
        }
        return result;
    }

    @Override
    public List<Bar> getFilteredBars(String city, List<String> tags, double minRating) {
        List<Bar> result = new ArrayList<>();
        for (Bar bar : ALL_BARS) {
            if (city != null && !city.isEmpty() && !bar.getCity().equalsIgnoreCase(city)) continue;
            if (minRating > 0 && bar.getRating() < minRating) continue;
            if (tags != null && !tags.isEmpty()) {
                boolean matched = false;
                for (String filterTag : tags) {
                    if (filterTag == null || filterTag.equalsIgnoreCase("All")) {
                        matched = true;
                        break;
                    }
                    for (String barTag : bar.getTags()) {
                        if (barTag.toLowerCase().contains(filterTag.toLowerCase())) {
                            matched = true;
                            break;
                        }
                    }
                    if (matched) break;
                }
                if (!matched) continue;
            }
            result.add(bar);
        }
        return result;
    }
}
