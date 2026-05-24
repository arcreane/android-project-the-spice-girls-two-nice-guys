package com.barometre.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barometre.myapplication.R;
import com.barometre.myapplication.models.Bar;

import java.util.List;

public class BarAdapter extends RecyclerView.Adapter<BarAdapter.BarViewHolder> {

    public interface OnBarClickListener {
        void onBarClick(Bar bar);
    }

    private final List<Bar> bars;
    private final OnBarClickListener listener;

    public BarAdapter(List<Bar> bars, OnBarClickListener listener) {
        this.bars = bars;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bar, parent, false);
        return new BarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarViewHolder holder, int position) {
        Bar bar = bars.get(position);
        holder.bind(bar, listener);
    }

    @Override
    public int getItemCount() {
        return bars.size();
    }

    public void updateBars(List<Bar> newBars) {
        bars.clear();
        bars.addAll(newBars);
        notifyDataSetChanged();
    }

    static class BarViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvAddress;
        private final TextView tvRating;

        public BarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName    = itemView.findViewById(R.id.tv_bar_name);
            tvAddress = itemView.findViewById(R.id.tv_bar_address);
            tvRating  = itemView.findViewById(R.id.tv_bar_rating);
        }

        public void bind(Bar bar, OnBarClickListener listener) {
            tvName.setText(bar.getName());
            tvAddress.setText(bar.getFullAddress());
            tvRating.setText(String.format("★ %.1f", bar.getRating()));
            itemView.setOnClickListener(v -> listener.onBarClick(bar));
        }
    }
}