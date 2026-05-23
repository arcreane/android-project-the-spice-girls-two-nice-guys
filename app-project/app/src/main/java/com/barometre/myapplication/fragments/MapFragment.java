package com.barometre.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.barometre.myapplication.R;
import com.barometre.myapplication.viewmodel.BarViewModel;
import com.barometre.myapplication.models.Bar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final LatLng PARIS_CENTER = new LatLng(48.8566, 2.3522);

    private static final float DEFAULT_ZOOM = 12f;

    private GoogleMap googleMap;
    private BarViewModel barViewModel;
    private OnBarSelectedListener listener;

    private final Map<Marker, Bar> markerBarMap = new HashMap<>();

    //interface for activity communication
    public interface OnBarSelectedListener {
        void onBarSelected(Bar bar);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBarSelectedListener) {
            listener = (OnBarSelectedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnBarSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        barViewModel = new ViewModelProvider(requireActivity()).get(BarViewModel.class);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (googleMap != null) {
            outState.putParcelable("camera", googleMap.getCameraPosition());
        }
    }

    //setup marker and tap listener
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Restore camera if returning from rotation
        Bundle args = getArguments();
        if (args != null && args.containsKey("camera")) {
            CameraPosition saved = args.getParcelable("camera");
            if (saved != null) {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(saved));
            }
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARIS_CENTER, DEFAULT_ZOOM));
        }
        googleMap.setOnMarkerClickListener(marker -> {
            Bar bar = markerBarMap.get(marker);
            if (bar != null && listener != null) {
                listener.onBarSelected(bar);
                highlightMarker(marker);
            }
            return true;
        });
        googleMap.setOnMapClickListener(latLng -> clearMarkerHighlights());
        observeBars();
    }

    private void observeBars() {
        barViewModel.getFilteredBars().observe(getViewLifecycleOwner(), bars -> {
            if (googleMap != null && bars != null) {
                addMarkersToMap(bars);
            }
        });

        // observe user location once added to barviewmodel
        // barViewModel.getUserLocation().observe(getViewLifecycleOwner(), location -> {
        //     if (location != null) {
        //         showUserLocationPin(location.getLatitude(), location.getLongitude());
        //     }
        // });
    }

    private void addMarkersToMap(List<Bar> bars) {
        googleMap.clear();
        markerBarMap.clear();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boolean hasValidBar = false;

        for (Bar bar : bars) {
            if (!bar.hasCoordinates()) continue;

            LatLng position = new LatLng(bar.getLatitude(), bar.getLongitude());

            MarkerOptions options = new MarkerOptions()
                    .position(position)
                    .title(bar.getName())
                    .snippet(bar.getCity())
                    //once created
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_bar))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            Marker marker = googleMap.addMarker(options);
            if (marker != null) {
                markerBarMap.put(marker, bar);
                boundsBuilder.include(position);
                hasValidBar = true;
            }
        }

        // Fit camera to show all markers (with padding)
        if (hasValidBar && bars.size() > 1) {
            try {
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 120)
                );
            } catch (Exception e) {
                // Bounds too small — fallback to Paris center
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PARIS_CENTER, DEFAULT_ZOOM));
            }
        }
    }
    public void showUserLocationPin(double latitude, double longitude) {
        if (googleMap == null) return;

        LatLng userPos = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(userPos)
                .title("You are here")
                // once created
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_user))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
    }

    private Marker currentHighlighted;

    private void highlightMarker(Marker marker) {
        clearMarkerHighlights();
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        currentHighlighted = marker;
    }

    private void clearMarkerHighlights() {
        if (currentHighlighted != null) {
            currentHighlighted.setIcon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            currentHighlighted = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
