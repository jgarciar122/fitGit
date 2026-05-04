    package com.example.fitgit.adapter;

    import android.graphics.Color;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
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
    import com.google.android.material.button.MaterialButton;
    import com.google.android.material.chip.Chip;

    import java.util.ArrayList;
    import java.util.List;

    public class AdaptadorEjercicios extends RecyclerView.Adapter<AdaptadorEjercicios.ViewHolder> {

        private List<Ejercicio> listaEjercicios;
        private static final String API_KEY = "84a7879aafmshd2ebff39f76e114p1e4397jsn321495fa09a5";

        // 1. Variable para saber si estamos dentro de una rutina (modo borrar) o en el buscador (modo añadir)
        private boolean esModoQuitar = false;

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

        // 2. Método para cambiar el modo desde el Fragment
        public void setEsModoQuitar(boolean esModoQuitar) {
            this.esModoQuitar = esModoQuitar;
        }

        public void setEjercicios(List<Ejercicio> ejercicios) {
            this.listaEjercicios = ejercicios;
            notifyDataSetChanged();
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
            holder.chipMusculo.setText(ejercicio.getParteCuerpo());
            holder.chipEquipamiento.setText(ejercicio.getEquipamiento());

            // Configuración visual del botón según el modo
            if (esModoQuitar) {
                holder.btnAdd.setText("Quitar de la rutina");
                if (holder.btnAdd instanceof MaterialButton) {
                    ((MaterialButton) holder.btnAdd).setIconResource(android.R.drawable.ic_delete);
                    // Un tono rojo suave para indicar borrado
                    holder.btnAdd.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    holder.btnAdd.setTextColor(Color.RED);
                    ((MaterialButton) holder.btnAdd).setIconTint(android.content.res.ColorStateList.valueOf(Color.RED));
                }
            } else {
                holder.btnAdd.setText("Añadir a una rutina");
                // Aquí puedes resetear al color original de tu app si fuera necesario
            }

            GlideUrl glideUrl = new GlideUrl(ejercicio.getUrlGif(), new LazyHeaders.Builder()
                    .addHeader("x-rapidapi-key", API_KEY)
                    .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build());

            Glide.with(holder.itemView.getContext())
                    .asGif()
                    .load(glideUrl)
                    .placeholder(R.drawable.imagen_ejemplo)
                    .error(R.drawable.imagen_ejemplo)
                    .into(holder.ivImagen);

            holder.itemView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(v.getContext(), DetallesEjercicioActivity.class);
                intent.putExtra("ejercicio_seleccionado", ejercicio);
                intent.putExtra("ya_en_rutina", esModoQuitar); // ← usa el flag que ya tienes
                v.getContext().startActivity(intent);
            });

            holder.btnAdd.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEjercicioClick(ejercicio);
                }
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
            Button btnAdd;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
                ivImagen = itemView.findViewById(R.id.ivGifEjercicio);
                chipMusculo = itemView.findViewById(R.id.chipMusculo);
                chipEquipamiento = itemView.findViewById(R.id.chipEquipamiento);

                // Asegúrate de que este ID sea el que pusiste en item_ejercicio.xml
                btnAdd = itemView.findViewById(R.id.btn_add_ejercicio_lista);
            }
        }
    }