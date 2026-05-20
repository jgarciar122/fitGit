package com.example.fitgit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgit.R;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.model.Sesion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorHistorial extends RecyclerView.Adapter<AdaptadorHistorial.SesionViewHolder> {

    private List<Sesion> listaSesiones = new ArrayList<>();
    private AppDatabase db;
    private LifecycleOwner lifecycleOwner;

    public AdaptadorHistorial(AppDatabase db, LifecycleOwner lifecycleOwner) {
        this.db = db;
        this.lifecycleOwner = lifecycleOwner;
    }

    public void setSesiones(List<Sesion> sesiones) {
        this.listaSesiones = sesiones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sesion, parent, false);
        return new SesionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        Sesion sesion = listaSesiones.get(position);

        // Formato de fecha
        String fecha = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("es"))
                .format(new Date(sesion.fecha));
        holder.tvFecha.setText(fecha);

        // Al pulsar expande/colapsa las series
        holder.itemView.setOnClickListener(v -> {
            if (holder.rvSeries.getVisibility() == View.GONE) {
                holder.rvSeries.setVisibility(View.VISIBLE);
                cargarSeries(sesion.id, holder);
            } else {
                holder.rvSeries.setVisibility(View.GONE);
            }
        });

        // Cargar número de series
        db.sesionDao().obtenerSeriesDeSesionCompleta(sesion.id).observe(lifecycleOwner, series -> {
            if (series != null) {
                holder.tvNumSeries.setText(series.size() + " series");
            }
        });
    }

    private void cargarSeries(int sesionId, SesionViewHolder holder) {
        db.sesionDao().obtenerSeriesDeSesionCompleta(sesionId).observe(lifecycleOwner, series -> {
            if (series != null && !series.isEmpty()) {
                AdaptadorSeriesResumen adaptador = new AdaptadorSeriesResumen();
                adaptador.setSeries(series);
                holder.rvSeries.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                holder.rvSeries.setAdapter(adaptador);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaSesiones.size();
    }

    static class SesionViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNumSeries;
        RecyclerView rvSeries;

        public SesionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tv_fecha_sesion);
            tvNumSeries = itemView.findViewById(R.id.tv_num_series_sesion);
            rvSeries = itemView.findViewById(R.id.rv_series_sesion);
        }
    }
}