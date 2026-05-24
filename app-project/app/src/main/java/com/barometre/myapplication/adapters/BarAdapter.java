package com.barometre.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barometre.myapplication.databinding.ItemBarBinding;
import com.barometre.myapplication.models.Bar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BarAdapter extends RecyclerView.Adapter<BarAdapter.BarViewHolder> {

    public interface OnBarClickListener {
        void onBarClick(Bar bar);
    }

    private List<Bar> bars = new ArrayList<>();
    private final OnBarClickListener listener;

    public BarAdapter(OnBarClickListener listener) {
        this.listener = listener;
    }

    public void setBars(List<Bar> newBars) {
        bars = newBars != null ? newBars : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBarBinding binding = ItemBarBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BarViewHolder holder, int position) {
        holder.bind(bars.get(position));
    }

    @Override
    public int getItemCount() {
        return bars.size();
    }

    class BarViewHolder extends RecyclerView.ViewHolder {
        private final ItemBarBinding binding;

        BarViewHolder(ItemBarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Bar bar) {
            binding.barName.setText(bar.getName());
            binding.barAddress.setText(bar.getFullAddress());
            binding.barRating.setText(String.format(Locale.getDefault(), "★ %.1f", bar.getRating()));

            String tags = bar.getTagsString();
            if (tags.isEmpty()) {
                binding.barTags.setVisibility(View.GONE);
            } else {
                binding.barTags.setText(tags);
                binding.barTags.setVisibility(View.VISIBLE);
            }

            binding.getRoot().setOnClickListener(v -> listener.onBarClick(bar));
        }
    }
}
