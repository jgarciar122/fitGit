package com.example.fitgit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitgit.R;
import com.example.fitgit.model.RutinaConConteo;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorRutinas extends RecyclerView.Adapter<AdaptadorRutinas.RutinaViewHolder> {

    private List<RutinaConConteo> listaRutinas = new ArrayList<>();

    public void setRutinas(List<RutinaConConteo> rutinas) {
        this.listaRutinas = rutinas;
        notifyDataSetChanged();
    }

    // Listener para abrir la rutina
    public interface OnRutinaClickListener {
        void onRutinaClick(RutinaConConteo rutina);
    }

    private OnRutinaClickListener listener;

    public void setOnRutinaClickListener(OnRutinaClickListener listener) {
        this.listener = listener;
    }

    // Listener para eliminar la rutina
    public interface OnEliminarRutinaListener {
        void onEliminarClick(RutinaConConteo rutina);
    }

    private OnEliminarRutinaListener eliminarListener;

    public void setOnEliminarRutinaListener(OnEliminarRutinaListener listener) {
        this.eliminarListener = listener;
    }

    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rutina, parent, false);
        return new RutinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        RutinaConConteo rutina = listaRutinas.get(position);
        holder.tvNombre.setText(rutina.nombre);
        holder.tvContador.setText(String.valueOf(rutina.numEjercicios));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRutinaClick(rutina);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (eliminarListener != null) eliminarListener.onEliminarClick(rutina);
        });
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    class RutinaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvContador;
        ImageButton btnEliminar;

        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_rutina);
            tvContador = itemView.findViewById(R.id.tv_num_ejercicios);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar_rutina);
        }
    }
}