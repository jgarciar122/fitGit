package com.example.fitgit.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitgit.R;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

    private static final int[] IMAGENES = {
            R.drawable.onboarding_1,
            R.drawable.onboarding_2,
            R.drawable.onboarding_3
    };

    private static final String[] TITULOS = {
            "Bienvenido a FitGit",
            "Organiza tus rutinas",
            "Sigue tu progreso"
    };

    private static final String[] DESCRIPCIONES = {
            "Tu compañero de entrenamiento personal. Registra cada sesión y alcanza tus objetivos.",
            "Crea y gestiona tus rutinas de ejercicio con todo el detalle que necesitas.",
            "Visualiza tu evolución y mantente motivado con tus estadísticas de entrenamiento."
    };

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding_slide, parent, false);
        return new SlideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        holder.imgSlide.setImageResource(IMAGENES[position]);
        holder.tvTitulo.setText(TITULOS[position]);
        holder.tvDescripcion.setText(DESCRIPCIONES[position]);
    }

    @Override
    public int getItemCount() {
        return IMAGENES.length;
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSlide;
        TextView tvTitulo;
        TextView tvDescripcion;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSlide = itemView.findViewById(R.id.imgSlide);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }
}
