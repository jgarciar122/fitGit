package com.example.fitgit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgit.R;
import com.example.fitgit.model.EjercicioConSeries;
import com.example.fitgit.model.SerieRegistro;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorEjerciciosSesion extends RecyclerView.Adapter<AdaptadorEjerciciosSesion.ViewHolder> {

    private List<EjercicioConSeries> ejercicios = new ArrayList<>();

    public void setEjercicios(List<EjercicioConSeries> ejercicios) {
        this.ejercicios = ejercicios;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ejercicio_sesion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EjercicioConSeries ejercicio = ejercicios.get(position);

        holder.tvNombreEjercicio.setText(ejercicio.nombreEjercicio);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ejercicio.series.size(); i++) {
            SerieRegistro serie = ejercicio.series.get(i);
            if (i > 0) sb.append("  |  ");
            sb.append(serie.kg).append("kg × ").append(serie.repeticiones).append(" reps");
        }
        holder.tvSeries.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEjercicio, tvSeries;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreEjercicio = itemView.findViewById(R.id.tv_nombre_ejercicio_sesion);
            tvSeries = itemView.findViewById(R.id.tv_series_ejercicio_sesion);
        }
    }
}