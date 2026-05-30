package com.example.fitgit.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.fitgit.BuildConfig;
import com.example.fitgit.R;
import com.example.fitgit.ui.DetallesEjercicioActivity;
import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.util.TraductorLocal;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorEjercicios extends RecyclerView.Adapter<AdaptadorEjercicios.ViewHolder> {

    private List<Ejercicio> listaEjercicios;
    private static final String API_KEY = BuildConfig.RAPIDAPI_KEY;
    private boolean esModoQuitar = false;
    private int rutinaId = -1;

    public interface OnEjercicioClickListener {
        void onEjercicioClick(Ejercicio ejercicio);
    }

    private OnEjercicioClickListener listener;

    public void setOnEjercicioClickListener(OnEjercicioClickListener listener) {
        this.listener = listener;
    }

    public AdaptadorEjercicios() {
        this.listaEjercicios = new ArrayList<>();
    }

    public void setEsModoQuitar(boolean esModoQuitar) {
        this.esModoQuitar = esModoQuitar;
    }

    public void setRutinaId(int rutinaId) {
        this.rutinaId = rutinaId;
    }

    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.listaEjercicios = ejercicios;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return esModoQuitar ? 1 : 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio_rutina, parent, false);
            return new ViewHolderRutina(vista);
        } else {
            View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ejercicio, parent, false);
            return new ViewHolderCompleto(vista);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ejercicio ejercicio = listaEjercicios.get(position);

        if (holder instanceof ViewHolderRutina) {
            ViewHolderRutina h = (ViewHolderRutina) holder;
            h.tvNombre.setText(ejercicio.getNombreMostrar());
            h.tvMusculo.setText(TraductorLocal.traducirParteCuerpo(ejercicio.getParteCuerpo()));

            h.btnQuitar.setOnClickListener(v -> {
                if (listener != null) listener.onEjercicioClick(ejercicio);
            });

            h.itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), DetallesEjercicioActivity.class);
                intent.putExtra("ejercicio_seleccionado", ejercicio);
                intent.putExtra("ya_en_rutina", true);
                intent.putExtra("rutina_id", rutinaId);
                v.getContext().startActivity(intent);
            });

        } else {
            ViewHolderCompleto h = (ViewHolderCompleto) holder;
            h.tvNombre.setText(ejercicio.getNombreMostrar());
            h.chipMusculo.setText(TraductorLocal.traducirParteCuerpo(ejercicio.getParteCuerpo()));
            h.chipEquipamiento.setText(TraductorLocal.traducirEquipamiento(ejercicio.getEquipamiento()));

            GlideUrl glideUrl = new GlideUrl(ejercicio.getUrlGif(), new LazyHeaders.Builder()
                    .addHeader("x-rapidapi-key", API_KEY)
                    .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build());

            Glide.with(h.itemView.getContext())
                    .asGif()
                    .load(glideUrl)
                    .placeholder(R.drawable.imagen_ejemplo)
                    .error(R.drawable.imagen_ejemplo)
                    .into(h.ivImagen);

            h.itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), DetallesEjercicioActivity.class);
                intent.putExtra("ejercicio_seleccionado", ejercicio);
                intent.putExtra("ya_en_rutina", false);
                intent.putExtra("rutina_id", rutinaId);
                v.getContext().startActivity(intent);
            });

            h.btnAdd.setOnClickListener(v -> {
                if (listener != null) listener.onEjercicioClick(ejercicio);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaEjercicios != null ? listaEjercicios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolderCompleto extends ViewHolder {
        TextView tvNombre;
        ImageView ivImagen;
        Chip chipMusculo, chipEquipamiento;
        Button btnAdd;

        public ViewHolderCompleto(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            ivImagen = itemView.findViewById(R.id.ivGifEjercicio);
            chipMusculo = itemView.findViewById(R.id.chipMusculo);
            chipEquipamiento = itemView.findViewById(R.id.chipEquipamiento);
            btnAdd = itemView.findViewById(R.id.btn_add_ejercicio_lista);
        }
    }

    public static class ViewHolderRutina extends ViewHolder {
        TextView tvNombre, tvMusculo;
        ImageButton btnQuitar;

        public ViewHolderRutina(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            tvMusculo = itemView.findViewById(R.id.tvMusculo);
            btnQuitar = itemView.findViewById(R.id.btnQuitar);
        }
    }
}