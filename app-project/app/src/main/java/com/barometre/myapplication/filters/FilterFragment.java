package com.barometre.myapplication.filters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.barometre.myapplication.R;

public class FilterFragment extends Fragment {

    private FilterViewModel filterViewModel;

    private Spinner typeSpinner;
    private Spinner ratingSpinner;
    private Spinner distanceSpinner;

    private final String[] barTypes = {
            "All", "Cocktail", "Wine", "Beer", "Pub", "Rooftop"
    };

    private final String[] ratingOptions = {
            "All ratings", "3+", "4+", "4.5+"
    };

    private final String[] distanceOptions = {
            "Any distance", "Under 1 km", "Under 3 km", "Under 5 km"
    };

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        typeSpinner = view.findViewById(R.id.typeSpinner);
        ratingSpinner = view.findViewById(R.id.ratingSpinner);
        distanceSpinner = view.findViewById(R.id.distanceSpinner);

        Button applyButton = view.findViewById(R.id.applyFiltersButton);
        Button resetButton = view.findViewById(R.id.resetFiltersButton);

        setupSpinner(typeSpinner, barTypes);
        setupSpinner(ratingSpinner, ratingOptions);
        setupSpinner(distanceSpinner, distanceOptions);

        applyButton.setOnClickListener(v -> applyFilters());
        resetButton.setOnClickListener(v -> resetFilters());
    }

    private void setupSpinner(Spinner spinner, String[] values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                values
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void applyFilters() {
        String selectedType = typeSpinner.getSelectedItem().toString();
        float selectedRating = parseRating(ratingSpinner.getSelectedItem().toString());
        double selectedDistance = parseDistance(distanceSpinner.getSelectedItem().toString());

        FilterOptions options = new FilterOptions(
                selectedType,
                selectedRating,
                selectedDistance
        );

        filterViewModel.setSelectedFilters(options);
    }

    private void resetFilters() {
        typeSpinner.setSelection(0);
        ratingSpinner.setSelection(0);
        distanceSpinner.setSelection(0);

        filterViewModel.resetFilters();
    }

    private float parseRating(String value) {
        switch (value) {
            case "3+":
                return 3.0f;
            case "4+":
                return 4.0f;
            case "4.5+":
                return 4.5f;
            default:
                return 0.0f;
        }
    }

    private double parseDistance(String value) {
        switch (value) {
            case "Under 1 km":
                return 1.0;
            case "Under 3 km":
                return 3.0;
            case "Under 5 km":
                return 5.0;
            default:
                return 0.0;
        }
    }
}