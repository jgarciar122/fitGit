package com.example.fitgit.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgit.model.SerieRegistro;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorSeriesResumen extends RecyclerView.Adapter<AdaptadorSeriesResumen.ViewHolder> {

    private List<SerieRegistro> series = new ArrayList<>();

    public void setSeries(List<SerieRegistro> series) {
        this.series = series;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos un TextView simple para cada serie
        TextView tv = new TextView(parent.getContext());
        tv.setPadding(0, 4, 0, 4);
        tv.setTextSize(14);
        tv.setTextColor(0xFF44474E);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SerieRegistro serie = series.get(position);
        ((TextView) holder.itemView).setText(
                "Serie " + (position + 1) + ":  " + serie.kg + " kg  ×  " + serie.repeticiones + " reps"
        );
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}