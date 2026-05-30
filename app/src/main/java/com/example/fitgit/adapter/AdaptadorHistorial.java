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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.SesionViewHolder> {

    private List<EntrenamientoDia> listaEntrenamientos = new ArrayList<>();

    // Interfaces para los listeners de borrado
    public interface OnEliminarSesionListener {
        void onEliminarSesion(int sesionId);
    }

    public interface OnEliminarEjercicioListener {
        void onEliminarEjercicio(int sesionId, String ejercicioId);
    }

    private OnEliminarSesionListener eliminarSesionListener;
    private OnEliminarEjercicioListener eliminarEjercicioListener;

    public void setOnEliminarSesionListener(OnEliminarSesionListener listener) {
        this.eliminarSesionListener = listener;
    }

    public void setOnEliminarEjercicioListener(OnEliminarEjercicioListener listener) {
        this.eliminarEjercicioListener = listener;
    }

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

        // Obtenemos el sesionId del primer SerieRegistro
        int sesionId = !entrenamiento.ejercicios.isEmpty() &&
                !entrenamiento.ejercicios.get(0).series.isEmpty()
                ? entrenamiento.ejercicios.get(0).series.get(0).sesionId
                : -1;

        String fecha = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("es"))
                .format(new Date(entrenamiento.fecha));
        holder.tvFecha.setText(fecha);
        holder.tvNombreRutina.setText(entrenamiento.nombreRutina);

        int numEjercicios = entrenamiento.ejercicios != null ? entrenamiento.ejercicios.size() : 0;
        holder.tvNumEjercicios.setText(numEjercicios + " ejercicio" + (numEjercicios != 1 ? "s" : ""));

        // Expandir/colapsar
        holder.itemView.setOnClickListener(v -> {
            if (holder.rvEjercicios.getVisibility() == View.GONE) {
                holder.rvEjercicios.setVisibility(View.VISIBLE);
                holder.ivExpandir.setImageResource(android.R.drawable.arrow_up_float);
                cargarEjercicios(entrenamiento.ejercicios, sesionId, holder);
            } else {
                holder.rvEjercicios.setVisibility(View.GONE);
                holder.ivExpandir.setImageResource(android.R.drawable.arrow_down_float);
            }
        });

    }

    private void cargarEjercicios(List<EjercicioConSeries> ejercicios, int sesionId, SesionViewHolder holder) {
        AdaptadorEjerciciosSesion adaptador = new AdaptadorEjerciciosSesion();
        adaptador.setEjercicios(ejercicios);
        adaptador.setOnEliminarEjercicioListener((sId, ejercicioId) -> {
            if (eliminarEjercicioListener != null) {
                eliminarEjercicioListener.onEliminarEjercicio(sId, ejercicioId);
            }
        });
        holder.rvEjercicios.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvEjercicios.setAdapter(adaptador);
    }

    public int obtenerSesionId(int position) {
        EntrenamientoDia entrenamiento = listaEntrenamientos.get(position);
        if (!entrenamiento.ejercicios.isEmpty()
                && !entrenamiento.ejercicios.get(0).series.isEmpty()) {
            return entrenamiento.ejercicios.get(0).series.get(0).sesionId;
        }
        return -1;
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