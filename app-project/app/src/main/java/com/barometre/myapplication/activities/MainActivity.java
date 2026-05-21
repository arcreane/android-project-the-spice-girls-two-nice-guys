package com.barometre.myapplication.activities;

import android.os.Bundle;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.barometre.myapplication.R;
import com.barometre.myapplication.filters.FilterFragment;
import com.barometre.myapplication.location.LocationViewModel;

import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.barometre.myapplication.location.LocationHelper;

public class MainActivity extends AppCompatActivity {

    private LocationHelper locationHelper;
    private LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        locationHelper = new LocationHelper(this);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        findViewById(R.id.testSettingsButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.testThemeButton).setOnClickListener(v -> {
            requestUserLocation();
        });

        findViewById(R.id.openFiltersButton).setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.filterContainer, new FilterFragment())
                    .addToBackStack(null)
                    .commit();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void requestUserLocation() {
        if (!locationHelper.hasLocationPermission()) {
            locationHelper.requestLocationPermission(this);
            return;
        }

        locationHelper.getLastKnownLocation(new LocationHelper.LocationCallback() {
            @Override
            public void onLocationReceived(Location location) {
                locationViewModel.setUserLocation(location);

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                Toast.makeText(
                        MainActivity.this,
                        "Location found " + latitude + ", " + longitude,
                        Toast.LENGTH_LONG

                ).show();

            }

            @Override
            public void onLocationError(String message) {
                Toast.makeText(
                        MainActivity.this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (locationHelper.hasLocationPermission()) {
                requestUserLocation();
            } else {
                Toast.makeText(
                        this,
                        "Location permission denied",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}