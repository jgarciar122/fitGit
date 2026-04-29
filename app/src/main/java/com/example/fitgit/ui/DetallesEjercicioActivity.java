package com.example.fitgit.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.fitgit.R;
import com.example.fitgit.databinding.ActivityDetalleEjercicioBinding;
import com.example.fitgit.model.Ejercicio;

public class DetallesEjercicioActivity extends AppCompatActivity {

    private ActivityDetalleEjercicioBinding binding;
    private static final String API_KEY = "84a7879aafmshd2ebff39f76e114p1e4397jsn321495fa09a5";
    private boolean esFavorito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inflar vista con ViewBinding
        binding = ActivityDetalleEjercicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Recuperar el ejercicio enviado desde el Adaptador
        Ejercicio ejercicio = (Ejercicio) getIntent().getSerializableExtra("ejercicio_seleccionado");

        if (ejercicio != null) {
            setupToolbar(ejercicio.getNombre());
            cargarDatos(ejercicio);
            setupFavorito();
        }
    }

    private void setupToolbar(String nombre) {
        // Configuramos la Toolbar dentro del CollapsingToolbar
        setSupportActionBar(binding.toolbarDetalle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // El título se puede mostrar en la Toolbar o dejar que lo maneje el TextView grande
            getSupportActionBar().setTitle("");
        }

        // Botón atrás usando el dispatcher moderno
        binding.toolbarDetalle.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void cargarDatos(Ejercicio ejercicio) {
        // Textos básicos
        binding.tvDetalleNombre.setText(ejercicio.getNombre());
        binding.chipDetalleMusculo.setText(ejercicio.getMusculoObjetivo());
        binding.chipDetalleEquipo.setText(ejercicio.getEquipamiento());

        // Carga del GIF con Glide + Headers de seguridad
        GlideUrl glideUrl = new GlideUrl(ejercicio.getUrlGif(), new LazyHeaders.Builder()
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
                .addHeader("User-Agent", "Mozilla/5.0")
                .build());

        Glide.with(this)
                .asGif()
                .load(glideUrl)
                .placeholder(R.drawable.imagen_ejemplo)
                .into(binding.ivDetalleGif);

        // Formateo de instrucciones (Limpieza de lista a texto plano)
        if (ejercicio.getInstrucciones() != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ejercicio.getInstrucciones().size(); i++) {
                sb.append("• ").append(ejercicio.getInstrucciones().get(i)).append("\n\n");
            }
            binding.tvDetalleInstrucciones.setText(sb.toString().trim());
        }
    }

    private void setupFavorito() {
        // En el futuro, aquí deberías consultar a Room:
        // isFavorito = repositorio.esFavorito(ejercicio.getId());
        // actualizarIconoFavorito();

        binding.fabFavorito.setOnClickListener(v -> {
            // 1. Cambiamos el estado (si era true pasa a false, y viceversa)
            esFavorito = !esFavorito;

            // 2. Actualizamos la interfaz
            if (esFavorito) {
                binding.fabFavorito.setImageResource(R.drawable.ic_heart_filled);
                android.widget.Toast.makeText(this, "Añadido a favoritos", android.widget.Toast.LENGTH_SHORT).show();
                // Aquí irá la llamada a Room para INSERTAR
            } else {
                binding.fabFavorito.setImageResource(R.drawable.ic_heart_outline);
                android.widget.Toast.makeText(this, "Eliminado de favoritos", android.widget.Toast.LENGTH_SHORT).show();
                // Aquí irá la llamada a Room para ELIMINAR
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}