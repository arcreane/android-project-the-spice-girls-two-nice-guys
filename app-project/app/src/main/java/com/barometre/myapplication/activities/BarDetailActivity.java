package com.barometre.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.barometre.myapplication.R;
import com.barometre.myapplication.databinding.ActivityBarDetailBinding;
import com.barometre.myapplication.fragments.BarDetailFragment;
import com.barometre.myapplication.models.Bar;
import androidx.annotation.NonNull;

public class BarDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BAR = "bar";

    private Bar bar;
    private ActivityBarDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBarDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        bar = (Bar) getIntent().getSerializableExtra(EXTRA_BAR);

        if (bar == null) {
            Toast.makeText(this, "Bar not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(bar.getName());
        }

        if (savedInstanceState == null) {
            BarDetailFragment fragment = BarDetailFragment.newInstance(bar);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_detail, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_share) {
            shareBar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareBar() {
        if (bar == null) return;

        String shareText = "📍 " + bar.getName() + "\n"
                + bar.getFullAddress() + "\n"
                + (bar.getWebsite() != null ? bar.getWebsite() : "");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, bar.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bar != null) {
            outState.putSerializable(EXTRA_BAR, bar);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bar = (Bar) savedInstanceState.getSerializable(EXTRA_BAR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}