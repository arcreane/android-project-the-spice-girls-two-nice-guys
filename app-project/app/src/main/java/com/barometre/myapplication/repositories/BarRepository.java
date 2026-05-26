package com.barometre.myapplication.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.barometre.myapplication.database.BarDbHelper;
import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.utils.DistanceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.barometre.myapplication.database.BarDbHelper.*;

public class BarRepository implements IBarRepository {

    private final BarDbHelper dbHelper;

    public BarRepository(Context context) {
        this.dbHelper = new BarDbHelper(context.getApplicationContext());
    }


    // Cursor → Bar


    private Bar cursorToBar(Cursor cursor) {
        Bar bar = new Bar(
                getString(cursor, COLUMN_ID),
                getString(cursor, COLUMN_NAME),
                getString(cursor, COLUMN_STREET),
                getString(cursor, COLUMN_HOUSE_NUMBER),
                getString(cursor, COLUMN_POSTCODE),
                getString(cursor, COLUMN_CITY),
                getString(cursor, COLUMN_PHONE),
                getString(cursor, COLUMN_WEBSITE),
                getString(cursor, COLUMN_OPENING_HOURS),
                getDouble(cursor, COLUMN_LATITUDE),
                getDouble(cursor, COLUMN_LONGITUDE),
                getDouble(cursor, COLUMN_RATING),
                tagsFromDb(getString(cursor, COLUMN_TAGS)),
                getString(cursor, COLUMN_PHOTO_URL)
        );
        bar.setFavorite(getInt(cursor, COLUMN_IS_FAVORITE) == 1);
        return bar;
    }

    private List<Bar> collectAll(Cursor cursor) {
        List<Bar> bars = new ArrayList<>();
        if (cursor == null) return bars;
        try {
            while (cursor.moveToNext()) {
                bars.add(cursorToBar(cursor));
            }
        } finally {
            cursor.close();
        }
        return bars;
    }


    // Read


    @Override
    public List<Bar> getAllBars() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BARS, null, null, null, null, null, null);
        return collectAll(cursor);
    }

    @Override
    public List<Bar> searchBarsByName(String name) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BARS, null,
                COLUMN_NAME + " LIKE ?",
                new String[]{"%" + name + "%"},
                null, null, null);
        return collectAll(cursor);
    }

    @Override
    public List<Bar> getBarsSortedByRating() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BARS, null, null, null, null, null,
                COLUMN_RATING + " DESC");
        return collectAll(cursor);
    }

    @Override
    public List<Bar> getBarsByCity(String city) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BARS, null,
                COLUMN_CITY + " = ?",
                new String[]{city},
                null, null, null);
        return collectAll(cursor);
    }

    @Override
    public List<String> getAllCities() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                true, TABLE_BARS,
                new String[]{COLUMN_CITY},
                null, null, null, null,
                COLUMN_CITY + " ASC", null);
        List<String> cities = new ArrayList<>();
        if (cursor == null) return cities;
        try {
            while (cursor.moveToNext()) {
                cities.add(cursor.getString(0));
            }
        } finally {
            cursor.close();
        }
        return cities;
    }

    @Override
    public Bar getBarById(String id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BARS, null,
                COLUMN_ID + " = ?",
                new String[]{id},
                null, null, null, "1");
        List<Bar> result = collectAll(cursor);

        if (!result.isEmpty()) {
            SQLiteDatabase wdb = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_LAST_VIEWED, System.currentTimeMillis());
            wdb.update(TABLE_BARS, values, COLUMN_ID + " = ?", new String[]{id});
        }

        return result.isEmpty() ? null : result.get(0);
    }


    // Favorites


    @Override
    public void addFavorite(String barId) {
        setFavoriteFlag(barId, true);
    }

    @Override
    public void removeFavorite(String barId) {
        setFavoriteFlag(barId, false);
    }

    private void setFavoriteFlag(String barId, boolean favorite) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_FAVORITE, favorite ? 1 : 0);
        db.update(TABLE_BARS, values, COLUMN_ID + " = ?", new String[]{barId});
    }

    @Override
    public boolean isFavorite(String barId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BARS,
                new String[]{COLUMN_IS_FAVORITE},
                COLUMN_ID + " = ?",
                new String[]{barId},
                null, null, null, "1");
        if (cursor == null) return false;
        try {
            return cursor.moveToFirst() && cursor.getInt(0) == 1;
        } finally {
            cursor.close();
        }
    }

    @Override
    public List<Bar> getFavoriteBars() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BARS, null,
                COLUMN_IS_FAVORITE + " = 1",
                null, null, null, null);
        return collectAll(cursor);
    }

    // Cache (offline)

    @Override
    public void cacheBar(Bar bar) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID,            bar.getId());
        values.put(COLUMN_NAME,          bar.getName());
        values.put(COLUMN_STREET,        bar.getStreet());
        values.put(COLUMN_HOUSE_NUMBER,  bar.getHouseNumber());
        values.put(COLUMN_POSTCODE,      bar.getPostcode());
        values.put(COLUMN_CITY,          bar.getCity());
        values.put(COLUMN_PHONE,         bar.getPhone());
        values.put(COLUMN_WEBSITE,       bar.getWebsite());
        values.put(COLUMN_OPENING_HOURS, bar.getOpeningHours());
        values.put(COLUMN_LATITUDE,      bar.getLatitude());
        values.put(COLUMN_LONGITUDE,     bar.getLongitude());
        values.put(COLUMN_RATING,        bar.getRating());
        values.put(COLUMN_TAGS,          tagsToDb(bar.getTags()));
        values.put(COLUMN_PHOTO_URL,     bar.getPhotoUrl());
        values.put(COLUMN_IS_FAVORITE,   bar.isFavorite() ? 1 : 0);
        db.insertWithOnConflict(TABLE_BARS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public List<Bar> getCachedBars() {
        return getAllBars();
    }


    // GPS / Filters

    @Override
    public List<Bar> getBarsNearLocation(double latitude, double longitude, double radiusKm) {
        List<Bar> nearby = new ArrayList<>();
        for (Bar bar : getAllBars()) {
            if (!bar.hasCoordinates()) continue;
            double dist = DistanceUtils.calculateDistanceKm(
                    latitude, longitude, bar.getLatitude(), bar.getLongitude());
            if (dist <= radiusKm) nearby.add(bar);
        }
        return nearby;
    }

    @Override
    public List<Bar> getFilteredBars(String city, List<String> tags, double minRating) {
        StringBuilder where = new StringBuilder();
        List<String> args  = new ArrayList<>();

        boolean filterCity   = city != null && !city.isEmpty() && !city.equalsIgnoreCase("All");
        boolean filterRating = minRating > 0.0;

        if (filterCity) {
            where.append(COLUMN_CITY).append(" = ?");
            args.add(city);
        }
        if (filterRating) {
            if (where.length() > 0) where.append(" AND ");
            where.append(COLUMN_RATING).append(" >= ?");
            args.add(String.valueOf(minRating));
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BARS, null,
                where.length() > 0 ? where.toString() : null,
                args.isEmpty() ? null : args.toArray(new String[0]),
                null, null,
                COLUMN_RATING + " DESC");

        List<Bar> results = collectAll(cursor);

        if (tags != null && !tags.isEmpty()) {
            List<Bar> filtered = new ArrayList<>();
            for (Bar bar : results) {
                for (String tag : tags) {
                    if (bar.getTags().contains(tag)) {
                        filtered.add(bar);
                        break;
                    }
                }
            }
            return filtered;
        }

        return results;
    }

    public List<Bar> getRecentlyViewedBars(int limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_BARS, null,
                COLUMN_LAST_VIEWED + " > 0",
                null, null, null,
                COLUMN_LAST_VIEWED + " DESC",
                String.valueOf(limit));
        return collectAll(cursor);
    }
}