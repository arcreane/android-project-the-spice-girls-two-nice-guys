package com.barometre.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.barometre.myapplication.R;
import com.barometre.myapplication.fragments.BarDetailFragment;
import com.barometre.myapplication.fragments.MapFragment;
import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.viewmodel.BarViewModel;

public class MainActivity extends AppCompatActivity
        implements MapFragment.OnBarSelectedListener {

    private BarViewModel barViewModel;
    private boolean isLandscape;
    private TextView offlineBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bar-omètre");
        }

        // offline banner
        offlineBanner = findViewById(R.id.tv_offline_banner);

        // detects orientation (landscape or portrait)
        isLandscape = findViewById(R.id.fragment_container_detail) != null;

        // initialize shared view model
        barViewModel = new ViewModelProvider(this).get(BarViewModel.class);
        barViewModel.loadAllBars();

        //load fragment if not already there

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_map, new MapFragment()).commit();
        }
        barViewModel.getIsOffline().observe(this, isOffline -> {
            if (isOffline) showOfflineBanner();
            else hideOfflineBanner();
        });
    }

    //toolbar
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
        if (id == R.id.action_show_favorites) {
            navigateToFavorites();
            return true;
        }
        if (id == R.id.action_settings) {
            navigateToSettings();
            return true;
        }
        if (id == R.id.action_filter) {
            //not implemented yet
            showFilterPanel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //map marker

    @Override
    public void onBarSelected(Bar bar) {
        barViewModel.selectBar(bar);
        if (isLandscape) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_detail, BarDetailFragment.newInstance(bar))
                    .commit();
        } else {
            navigateToBarDetail(bar);
        }
    }
    //navigators

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

    //offline banner

    public void showOfflineBanner() {
        if (offlineBanner != null) offlineBanner.setVisibility(View.VISIBLE);
    }

    public void hideOfflineBanner() {
        if (offlineBanner != null) offlineBanner.setVisibility(View.GONE);
    }

    //lifecycle
    @Override
    protected void onStart() {
        super.onStart();
        // uncomment once ConnectivityReceiver exists
        // connectivityReceiver = new ConnectivityReceiver(barViewModel);
        // IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        // registerReceiver(connectivityReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // uncomment once ConnectivityReceiver exists
        // if (connectivityReceiver != null) unregisterReceiver(connectivityReceiver);
    }
}