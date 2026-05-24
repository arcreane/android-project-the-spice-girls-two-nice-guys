package com.barometre.myapplication.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.barometre.myapplication.R;
import com.barometre.myapplication.filters.FilterFragment;
import com.barometre.myapplication.fragments.BarDetailFragment;
import com.barometre.myapplication.fragments.BarListFragment;
import com.barometre.myapplication.fragments.MapFragment;
import com.barometre.myapplication.location.LocationHelper;
import com.barometre.myapplication.location.LocationViewModel;
import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.receivers.ConnectivityReceiver;
import com.barometre.myapplication.viewmodel.BarViewModel;


public class MainActivity extends AppCompatActivity
        implements MapFragment.OnBarSelectedListener,
                   BarListFragment.OnBarSelectedListener {

    private BarViewModel barViewModel;
    private LocationViewModel locationViewModel;
    private LocationHelper locationHelper;

    private boolean isLandscape;
    private TextView offlineBanner;

    private ConnectivityReceiver connectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bar-omètre");
        }

        offlineBanner = findViewById(R.id.tv_offline_banner);
        isLandscape = findViewById(R.id.fragment_container_detail) != null;

        barViewModel = new ViewModelProvider(this).get(BarViewModel.class);
        barViewModel.init(this);
        barViewModel.loadAllBars();

        locationHelper = new LocationHelper(this);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_map, new BarListFragment())
                    .commit();
        }

        barViewModel.getIsOffline().observe(this, isOffline -> {
            if (isOffline) {
                showOfflineBanner();
            } else {
                hideOfflineBanner();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_rating) {
            barViewModel.sortByRating();
            return true;
        }
        if (id == R.id.action_show_map) {
            showMapView();
            return true;
        }
        if (id == R.id.action_show_favorites) {
            navigateToFavorites();
            return true;
        }
        if (id == R.id.action_settings) {
            navigateToSettings();
            return true;
        }
        if (id == R.id.action_filter) {
            showFilterPanel();
            return true;
        }
        if (id == R.id.action_near_me) {
            requestUserLocation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterPanel() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_map, new FilterFragment())
                .addToBackStack("filter")
                .commit();
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

                barViewModel.showBarsNearLocation(latitude, longitude, 3.0);

                Toast.makeText(
                        MainActivity.this,
                        "Location found: " + latitude + ", " + longitude,
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
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBarSelected(Bar bar) {
        barViewModel.selectBar(bar);

        if (isLandscape) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_detail, BarDetailFragment.newInstance(bar))
                    .commit();
        } else {
            navigateToBarDetail(bar);
        }
    }

    public void showMapView() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_map, new MapFragment())
                .addToBackStack("map")
                .commit();
    }

    public void navigateToBarDetail(Bar bar) {
        Intent intent = new Intent(this, BarDetailActivity.class);
        intent.putExtra(BarDetailActivity.EXTRA_BAR, bar);
        startActivity(intent);
    }

    public void navigateToFavorites() {
        startActivity(new Intent(this, FavoritesActivity.class));
    }

    public void navigateToSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void showOfflineBanner() {
        if (offlineBanner != null) {
            offlineBanner.setVisibility(View.VISIBLE);
        }
    }

    public void hideOfflineBanner() {
        if (offlineBanner != null) {
            offlineBanner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (connectivityReceiver == null) {
            connectivityReceiver = new ConnectivityReceiver(barViewModel);
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectivityReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityReceiver != null) {
            unregisterReceiver(connectivityReceiver);
            connectivityReceiver = null;
        }
    }
}
