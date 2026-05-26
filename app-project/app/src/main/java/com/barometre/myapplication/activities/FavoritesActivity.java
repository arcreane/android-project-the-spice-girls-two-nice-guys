package com.barometre.myapplication.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barometre.myapplication.R;
import com.barometre.myapplication.adapters.BarAdapter;
import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.repositories.BarRepository;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private BarRepository repository;
    private RecyclerView recyclerView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar_favorites);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Favourites");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new BarRepository(this);

        recyclerView = findViewById(R.id.rv_favorites);
        emptyView    = findViewById(R.id.tv_favorites_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadFavorites();
    }

    private void loadFavorites() {
        List<Bar> favorites = repository.getFavoriteBars();

        if (favorites.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            BarAdapter adapter = new BarAdapter(favorites, bar -> {
                android.content.Intent intent = new android.content.Intent(
                        this, BarDetailActivity.class);
                intent.putExtra(BarDetailActivity.EXTRA_BAR, bar);
                startActivity(intent);
            });
            adapter.setRemoveFavoriteEnabled(true);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}