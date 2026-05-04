package com.example.fitgit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitgit.R;
import com.example.fitgit.model.Rutina;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorRutinas extends RecyclerView.Adapter<AdaptadorRutinas.RutinaViewHolder> {

    private List<Rutina> listaRutinas = new ArrayList<>();

    public void setRutinas(List<Rutina> rutinas) {
        this.listaRutinas = rutinas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RutinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rutina, parent, false);
        return new RutinaViewHolder(view);
    }

    public interface OnRutinaClickListener {
        void onRutinaClick(Rutina rutina);
    }

    private OnRutinaClickListener listener;

    public void setOnRutinaClickListener(OnRutinaClickListener listener) {
        this.listener = listener;
    }



    @Override
    public void onBindViewHolder(@NonNull RutinaViewHolder holder, int position) {
        Rutina rutina = listaRutinas.get(position);
        holder.tvNombre.setText(rutina.getNombre());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRutinaClick(rutina);
        });

    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    class RutinaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        public RutinaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_rutina);
        }
    }
}