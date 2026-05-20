package com.example.fitgit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgit.R;
import com.example.fitgit.model.EntrenamientoDia;
import com.example.fitgit.model.EjercicioConSeries;
import com.example.fitgit.model.SerieRegistro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.SesionViewHolder> {

    private List<EntrenamientoDia> listaEntrenamientos = new ArrayList<>();

    public void setEntrenamientos(List<EntrenamientoDia> entrenamientos) {
        this.listaEntrenamientos = entrenamientos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sesion, parent, false);
        return new SesionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        EntrenamientoDia entrenamiento = listaEntrenamientos.get(position);

        // Fecha
        String fecha = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("es"))
                .format(new Date(entrenamiento.fecha));
        holder.tvFecha.setText(fecha);

        // Nombre rutina
        holder.tvNombreRutina.setText(entrenamiento.nombreRutina);

        // Número de ejercicios
        int numEjercicios = entrenamiento.ejercicios != null ? entrenamiento.ejercicios.size() : 0;
        holder.tvNumEjercicios.setText(numEjercicios + " ejercicio" + (numEjercicios != 1 ? "s" : ""));

        // Click para expandir/colapsar
        holder.itemView.setOnClickListener(v -> {
            if (holder.rvEjercicios.getVisibility() == View.GONE) {
                holder.rvEjercicios.setVisibility(View.VISIBLE);
                holder.ivExpandir.setImageResource(android.R.drawable.arrow_up_float);
                cargarEjercicios(entrenamiento.ejercicios, holder);
            } else {
                holder.rvEjercicios.setVisibility(View.GONE);
                holder.ivExpandir.setImageResource(android.R.drawable.arrow_down_float);
            }
        });
    }

    private void cargarEjercicios(List<EjercicioConSeries> ejercicios, SesionViewHolder holder) {
        AdaptadorEjerciciosSesion adaptador = new AdaptadorEjerciciosSesion();
        adaptador.setEjercicios(ejercicios);
        holder.rvEjercicios.setLayoutManager(
                new LinearLayoutManager(holder.itemView.getContext())
        );
        holder.rvEjercicios.setAdapter(adaptador);
    }

    @Override
    public int getItemCount() {
        return listaEntrenamientos.size();
    }

    static class SesionViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNombreRutina, tvNumEjercicios;
        ImageView ivExpandir;
        RecyclerView rvEjercicios;

        public SesionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tv_fecha_sesion);
            tvNombreRutina = itemView.findViewById(R.id.tv_nombre_rutina_sesion);
            tvNumEjercicios = itemView.findViewById(R.id.tv_num_ejercicios_sesion);
            ivExpandir = itemView.findViewById(R.id.iv_expandir);
            rvEjercicios = itemView.findViewById(R.id.rv_ejercicios_sesion);
        }
    }
}