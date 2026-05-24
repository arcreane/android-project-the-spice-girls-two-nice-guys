package com.barometre.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class BarDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "BarDbHelper";
    private static final String DATABASE_NAME = "barometre.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;

    public static final String TABLE_BARS = "bars";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_HOUSE_NUMBER = "house_number";
    public static final String COLUMN_POSTCODE = "postcode";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_WEBSITE = "website";
    public static final String COLUMN_OPENING_HOURS = "opening_hours";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_TAGS = "tags";
    public static final String COLUMN_PHOTO_URL = "photo_url";
    public static final String COLUMN_IS_FAVORITE = "is_favorite";
    public static final String COLUMN_LAST_VIEWED = "last_viewed";
    public static final String TAGS_SEPARATOR = " • ";

    public BarDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BARS_TABLE = "CREATE TABLE " + TABLE_BARS + " ("
                + COLUMN_ID + " TEXT PRIMARY KEY, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_STREET + " TEXT, "
                + COLUMN_HOUSE_NUMBER + " TEXT, "
                + COLUMN_POSTCODE + " TEXT, "
                + COLUMN_CITY + " TEXT, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_WEBSITE + " TEXT, "
                + COLUMN_OPENING_HOURS + " TEXT, "
                + COLUMN_LATITUDE + " REAL, "
                + COLUMN_LONGITUDE + " REAL, "
                + COLUMN_RATING + " REAL, "
                + COLUMN_TAGS + " TEXT, "
                + COLUMN_PHOTO_URL + " TEXT, "
                + COLUMN_IS_FAVORITE + " INTEGER DEFAULT 0, "
                + COLUMN_LAST_VIEWED + " INTEGER DEFAULT 0"
                + ")";
        db.execSQL(CREATE_BARS_TABLE);
        seedRelationalCache(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARS);
        onCreate(db);
    }
    private void seedRelationalCache(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            int resourceId = context.getResources().getIdentifier(
                    "paris_bars", "raw", context.getPackageName());
            if (resourceId == 0) {
                Log.w(TAG, "paris_bars raw resource not found — skipping seed");
                return;
            }


            InputStream is = context.getResources().openRawResource(resourceId);
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(sb.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();

                values.put(COLUMN_ID, obj.optString("id", "bar_" + i));
                values.put(COLUMN_NAME, obj.optString("name", ""));
                values.put(COLUMN_STREET, obj.optString("street", ""));
                values.put(COLUMN_HOUSE_NUMBER, obj.optString("house_number", ""));
                values.put(COLUMN_POSTCODE, obj.optString("postcode", ""));
                values.put(COLUMN_CITY, obj.optString("city", ""));
                values.put(COLUMN_PHONE, obj.optString("phone", ""));
                values.put(COLUMN_WEBSITE, obj.optString("website", ""));
                values.put(COLUMN_OPENING_HOURS, obj.optString("opening_hours", ""));
                values.put(COLUMN_LATITUDE, obj.optDouble("latitude", 0.0));
                values.put(COLUMN_LONGITUDE, obj.optDouble("longitude", 0.0));
                values.put(COLUMN_RATING, obj.optDouble("rating", 4.0));
                values.put(COLUMN_TAGS, obj.optString("tags", ""));
                values.put(COLUMN_PHOTO_URL, obj.optString("photo_url", ""));
                values.put(COLUMN_IS_FAVORITE, 0);
                values.put(COLUMN_LAST_VIEWED, 0);

                db.insert(TABLE_BARS, null, values);
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "Seeded " + jsonArray.length() + " bars successfully.");

        } catch (Exception e) {
            Log.e(TAG, "Failed seeding bars", e);
        } finally {
            db.endTransaction();
        }
    }
    public static java.util.List<String> tagsFromDb(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        String[] parts = raw.split(java.util.regex.Pattern.quote(TAGS_SEPARATOR));
        java.util.List<String> list = new java.util.ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                list.add(trimmed);
            }
        }
        return list;
    }

    public static String tagsToDb(java.util.List<String> tags) {
        if (tags == null || tags.isEmpty()) return "";
        return android.text.TextUtils.join(TAGS_SEPARATOR, tags);
    }

    public static String getString(Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        if (idx == -1) return "";
        String val = cursor.getString(idx);
        return val != null ? val : "";
    }

    public static double getDouble(Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        return idx == -1 ? 0.0 : cursor.getDouble(idx);
    }

    public static int getInt(Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        return idx == -1 ? 0 : cursor.getInt(idx);
    }
}