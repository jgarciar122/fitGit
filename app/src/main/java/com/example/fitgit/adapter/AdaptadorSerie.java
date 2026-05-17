package com.example.fitgit.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgit.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorSerie extends RecyclerView.Adapter<AdaptadorSerie.SerieViewHolder> {

    // Clase simple para representar una fila editable
    public static class FilaSerie {
        public float kg;
        public int reps;

        public FilaSerie(float kg, int reps) {
            this.kg = kg;
            this.reps = reps;
        }
    }

    private List<FilaSerie> series = new ArrayList<>();

    public void añadirSerie() {
        series.add(new FilaSerie(0, 0));
        notifyItemInserted(series.size() - 1);
    }

    public void añadirSerieConDatos(float kg, int reps) {
        series.add(new FilaSerie(kg, reps));
        notifyItemInserted(series.size() - 1);
    }

    public List<FilaSerie> getSeries() {
        return series;
    }

    @NonNull
    @Override
    public SerieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serie, parent, false);
        return new SerieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieViewHolder holder, int position) {
        FilaSerie fila = series.get(position);

        holder.tvNumSerie.setText(String.valueOf(position + 1));

        // Ponemos los valores sin disparar el watcher
        holder.etKg.removeTextChangedListener(holder.kgWatcher);
        holder.etReps.removeTextChangedListener(holder.repsWatcher);

        holder.etKg.setText(fila.kg > 0 ? String.valueOf(fila.kg) : "");
        holder.etReps.setText(fila.reps > 0 ? String.valueOf(fila.reps) : "");

        holder.kgWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    series.get(holder.getAdapterPosition()).kg = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                    series.get(holder.getAdapterPosition()).kg = 0;
                }
            }
        };

        holder.repsWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    series.get(holder.getAdapterPosition()).reps = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    series.get(holder.getAdapterPosition()).reps = 0;
                }
            }
        };

        holder.etKg.addTextChangedListener(holder.kgWatcher);
        holder.etReps.addTextChangedListener(holder.repsWatcher);

        holder.btnBorrar.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            series.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, series.size());
        });
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    static class SerieViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumSerie;
        TextInputEditText etKg, etReps;
        ImageButton btnBorrar;
        TextWatcher kgWatcher, repsWatcher;

        public SerieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumSerie = itemView.findViewById(R.id.tv_num_serie);
            etKg = itemView.findViewById(R.id.et_kg);
            etReps = itemView.findViewById(R.id.et_reps);
            btnBorrar = itemView.findViewById(R.id.btn_borrar_serie);
        }
    }
}