package com.barometre.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
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

    /**
     * Directly streams flat, relational-mapped key-values cleanly into SQLite rows
     * without running structural translations, type assertions, or nested parsing.
     */
    private void seedRelationalCache(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            int resourceId = context.getResources().getIdentifier("paris_bars", "raw", context.getPackageName());
            if (resourceId == 0) return;

            InputStream is = context.getResources().openRawResource(resourceId);
            byte[] buffer = new byte[is.available()];
            int readBytes = is.read(buffer);
            is.close();
            if (readBytes <= 0) return;

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();

                // 1-to-1 Direct relational mappings. No nested object unboxing.
                values.put(COLUMN_ID, obj.optString("id", "bar_" + i));
                values.put(COLUMN_NAME, obj.optString("name", ""));
                values.put(COLUMN_STREET, obj.optString("street", ""));
                values.put(COLUMN_HOUSE_NUMBER, obj.optString("house_number", ""));
                values.put(COLUMN_POSTCODE, obj.optString("postcode", ""));
                values.put(COLUMN_CITY, obj.optString("city", ""));
                values.put(COLUMN_PHONE, obj.optString("phone", ""));
                values.put(COLUMN_WEBSITE, obj.optString("website", ""));
                values.put(COLUMN_OPENING_HOURS, obj.optString("opening_hours", ""));

                // Pure primitive mappings directly out of flat layout
                values.put(COLUMN_LATITUDE, obj.optDouble("latitude", 0.0));
                values.put(COLUMN_LONGITUDE, obj.optDouble("longitude", 0.0));
                values.put(COLUMN_RATING, obj.optDouble("rating", 4.0));
                values.put(COLUMN_TAGS, obj.optString("tags", ""));
                values.put(COLUMN_PHOTO_URL, obj.optString("photo_url", ""));

                // Track state initializations
                values.put(COLUMN_IS_FAVORITE, 0);
                values.put(COLUMN_LAST_VIEWED, 0);

                db.insert(TABLE_BARS, null, values);
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Relational pre-mapped cache successfully seeded.");
        } catch (Exception e) {
            Log.e(TAG, "Failed seeding from relational structure", e);
        } finally {
            db.endTransaction();
        }
    }
}