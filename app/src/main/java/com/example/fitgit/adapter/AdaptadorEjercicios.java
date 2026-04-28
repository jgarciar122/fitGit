package com.example.fitgit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.fitgit.R;
import com.example.fitgit.ui.DetallesEjercicioActivity;
import com.example.fitgit.model.Ejercicio;
import com.google.android.material.chip.Chip;
import java.util.List;

public class AdaptadorEjercicios extends RecyclerView.Adapter<AdaptadorEjercicios.ViewHolder> {

    private List<Ejercicio> listaEjercicios;
    // Sustituye esto por tu clave real si no es esta
    private static final String API_KEY = "84a7879aafmshd2ebff39f76e114p1e4397jsn321495fa09a5";

    public AdaptadorEjercicios(List<Ejercicio> listaEjercicios) {
        this.listaEjercicios = listaEjercicios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ejercicio ejercicio = listaEjercicios.get(position);

        holder.tvNombre.setText(ejercicio.getNombre());
        holder.chipMusculo.setText(ejercicio.getMusculoObjetivo());
        holder.chipEquipamiento.setText(ejercicio.getEquipamiento());

// Dentro de onBindViewHolder en AdaptadorEjercicios.java
        GlideUrl glideUrl = new GlideUrl(ejercicio.getUrlGif(), new LazyHeaders.Builder()
                .addHeader("x-rapidapi-key", "84a7879aafmshd2ebff39f76e114p1e4397jsn321495fa09a5")
                .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
                // A veces Glide necesita un User-Agent para que el servidor no crea que es un bot
                .addHeader("User-Agent", "Mozilla/5.0")
                .build());

        Glide.with(holder.itemView.getContext())
                .asGif()
                .load(glideUrl)
                .placeholder(R.drawable.imagen_ejemplo)
                .error(R.drawable.imagen_ejemplo) // Aquí saldrá tu imagen si falla
                .into(holder.ivImagen);

        // Navegación al detalle
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), DetallesEjercicioActivity.class);
            intent.putExtra("ejercicio_seleccionado", ejercicio);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaEjercicios != null ? listaEjercicios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        ImageView ivImagen;
        Chip chipMusculo, chipEquipamiento;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            ivImagen = itemView.findViewById(R.id.ivGifEjercicio);
            chipMusculo = itemView.findViewById(R.id.chipMusculo);
            chipEquipamiento = itemView.findViewById(R.id.chipEquipamiento);
        }
    }
}