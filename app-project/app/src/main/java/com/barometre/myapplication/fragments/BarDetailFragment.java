package com.barometre.myapplication.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.barometre.myapplication.databinding.FragmentBarDetailBinding;
import com.barometre.myapplication.models.Bar;

public class BarDetailFragment extends Fragment {
    private static final String ARG_BAR = "bar";

    private static final int CONTEXT_ADD_FAVORITE = 1;
    private static final int CONTEXT_SHARE        = 2;

    private Bar bar;

    private FragmentBarDetailBinding binding;


    public static BarDetailFragment newInstance(Bar bar) {
        BarDetailFragment fragment = new BarDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BAR, bar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            bar = (Bar) getArguments().getSerializable(ARG_BAR);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBarDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (bar == null) return;

        bindViews();
        registerForContextMenu(binding.detailRootCard);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void bindViews() {

        String imageUrl = "https://loremflickr.com/320/240/bar,pub,cocktail?lock=" + bar.getId();
        Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(binding.imgBarDetail);

        binding.tvDetailName.setText(bar.getName());

        binding.ratingBarDetail.setRating((float) bar.getRating());
        binding.tvDetailRating.setText(String.format("%.1f / 5", bar.getRating()));
        binding.tvDetailAddress.setText(bar.getFullAddress());

        if (bar.getPhone() != null && !bar.getPhone().isEmpty()) {
            binding.tvDetailPhone.setVisibility(View.VISIBLE);
            binding.tvDetailPhone.setText(bar.getPhone());
            binding.tvDetailPhone.setOnClickListener(v -> dialPhone(bar.getPhone()));
        } else {
            binding.tvDetailPhone.setVisibility(View.GONE);
        }

        if (bar.getOpeningHours() != null && !bar.getOpeningHours().isEmpty()) {
            binding.tvDetailHours.setVisibility(View.VISIBLE);
            binding.tvDetailHours.setText(bar.getOpeningHours());
        } else {
            binding.tvDetailHours.setVisibility(View.GONE);
        }
        if (bar.getWebsite() != null && !bar.getWebsite().isEmpty()) {
            binding.btnOpenWebsite.setVisibility(View.VISIBLE);
            binding.btnOpenWebsite.setOnClickListener(v -> openWebsite());
        } else {
            binding.btnOpenWebsite.setVisibility(View.GONE);
        }

        if (bar.hasCoordinates()) {
            binding.btnOpenMaps.setVisibility(View.VISIBLE);
            binding.btnOpenMaps.setOnClickListener(v -> openMapsNavigation());
        } else {
            binding.btnOpenMaps.setVisibility(View.GONE);
        }

        binding.btnShareBar.setOnClickListener(v -> shareBar());
    }

    private void openWebsite() {
        String url = bar.getWebsite();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void openMapsNavigation() {
        String uri = "google.navigation:q=" + bar.getLatitude() + "," + bar.getLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String fallback = "https://maps.google.com/?q=" +
                    bar.getLatitude() + "," + bar.getLongitude();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(fallback)));
        }
    }
    private void shareBar() {
        String shareText = "📍 " + bar.getName() + "\n"
                + bar.getFullAddress() + "\n"
                + (bar.getWebsite() != null ? bar.getWebsite() : "");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, bar.getName());
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private void dialPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu,
                                    @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle(bar != null ? bar.getName() : "Options");
        menu.add(Menu.NONE, CONTEXT_ADD_FAVORITE, Menu.NONE, "Add to Favorites");
        menu.add(Menu.NONE, CONTEXT_SHARE,        Menu.NONE, "Share");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == CONTEXT_ADD_FAVORITE) {
            addToFavorites();
            return true;
        } else if (item.getItemId() == CONTEXT_SHARE) {
            shareBar();
            return true;
        }
        return super.onContextItemSelected(item);
    }
    private void addToFavorites() {
        if (bar == null) return;
        // TODO: replace with FavoritesRepository.getInstance(requireContext()).addFavorite(bar);
        Toast.makeText(requireContext(),
                bar.getName() + " added to favorites",
                Toast.LENGTH_SHORT).show();
    }
}