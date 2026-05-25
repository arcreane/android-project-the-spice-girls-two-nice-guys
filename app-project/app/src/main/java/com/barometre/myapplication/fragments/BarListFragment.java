package com.barometre.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.barometre.myapplication.R;
import com.barometre.myapplication.adapters.BarAdapter;
import com.barometre.myapplication.databinding.FragmentBarListBinding;
import com.barometre.myapplication.filters.FilterViewModel;
import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.viewmodel.BarViewModel;

public class BarListFragment extends Fragment {

    public interface OnBarSelectedListener {
        void onBarSelected(Bar bar);
    }

    private FragmentBarListBinding binding;
    private BarViewModel barViewModel;
    private FilterViewModel filterViewModel;
    private BarAdapter adapter;
    private OnBarSelectedListener selectionListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBarSelectedListener) {
            selectionListener = (OnBarSelectedListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBarListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        barViewModel = new ViewModelProvider(requireActivity()).get(BarViewModel.class);
        filterViewModel = new ViewModelProvider(requireActivity()).get(FilterViewModel.class);

        setupRecyclerView();
        setupSearch();
        observeData();
    }

    // Contribute filter-by-type submenu to the activity toolbar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bar_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_rating) {
            boolean nowChecked = !item.isChecked();
            item.setChecked(nowChecked);
            barViewModel.setSortByRating(nowChecked);
            return true;
        }

        String type = getTypeForMenuId(id);
        if (type != null) {
            item.setChecked(true);
            barViewModel.setTypeFilter(type);
            updateFilterLabel(type);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        adapter = new BarAdapter(bar -> {
            if (selectionListener != null) selectionListener.onBarSelected(bar);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                barViewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private String getTypeForMenuId(int id) {
        if (id == R.id.filter_all) return "All";
        if (id == R.id.filter_cocktail) return "Cocktail";
        if (id == R.id.filter_wine) return "Wine";
        if (id == R.id.filter_beer) return "Beer";
        if (id == R.id.filter_pub) return "Pub";
        if (id == R.id.filter_rooftop) return "Rooftop";
        return null;
    }

    private void updateFilterLabel(String type) {
        if ("All".equals(type)) {
            binding.activeFilterLabel.setVisibility(View.GONE);
        } else {
            binding.activeFilterLabel.setText(type);
            binding.activeFilterLabel.setVisibility(View.VISIBLE);
        }
    }

    private void observeData() {
        barViewModel.getBars().observe(getViewLifecycleOwner(), bars -> {
            adapter.setBars(bars);
            boolean isEmpty = bars == null || bars.isEmpty();
            binding.emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        filterViewModel.getSelectedFilters().observe(getViewLifecycleOwner(), filters ->
                barViewModel.setFilters(filters));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
