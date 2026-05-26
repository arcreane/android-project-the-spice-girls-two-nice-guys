package com.barometre.myapplication.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barometre.myapplication.R;
import com.barometre.myapplication.adapters.BarAdapter;
import com.barometre.myapplication.models.Bar;
import com.barometre.myapplication.repositories.BarRepository;

import java.util.List;

public class FavoritesFragment extends Fragment {

    public interface OnBarSelectedListener {
        void onBarSelected(Bar bar);
    }

    private BarRepository repository;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private OnBarSelectedListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBarSelectedListener) {
            listener = (OnBarSelectedListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = new BarRepository(requireContext());
        recyclerView = view.findViewById(R.id.rv_favorites);
        emptyView    = view.findViewById(R.id.tv_favorites_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        loadFavorites();
    }

    public void loadFavorites() {
        List<Bar> favorites = repository.getFavoriteBars();

        if (favorites.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            BarAdapter adapter = new BarAdapter(favorites, bar -> {
                if (listener != null) listener.onBarSelected(bar);
            });
            adapter.setRemoveFavoriteEnabled(true);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}