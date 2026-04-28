package com.example.fitgit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgit.R;
import com.example.fitgit.ui.DetallesEjercicioActivity;
import com.example.fitgit.model.Ejercicio;
import com.google.android.material.chip.Chip;
import java.util.List;

public class AdaptadorEjercicios extends RecyclerView.Adapter<AdaptadorEjercicios.ViewHolder> {

    private List<Ejercicio> listaEjercicios;

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

        // Seteamos los textos
        holder.tvNombre.setText(ejercicio.getNombre());
        holder.chipMusculo.setText(ejercicio.getMusculoObjetivo());
        holder.chipEquipamiento.setText(ejercicio.getEquipamiento());

        // --- CAMBIO CLAVE: Carga de GIF con Glide ---
        com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .asGif() // Obligamos a que lo trate como GIF
                .load(ejercicio.getUrlGif()) // Usamos la URL que viene de la API
                .placeholder(R.drawable.imagen_ejemplo) // Lo que se ve mientras descarga
                .error(R.drawable.imagen_ejemplo) // Lo que se ve si falla (puedes crear uno de error)
                .into(holder.ivImagen);
        // --------------------------------------------

        // Navegación al detalle
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), DetallesEjercicioActivity.class);
            intent.putExtra("ejercicio_seleccionado", ejercicio);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaEjercicios.size();
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