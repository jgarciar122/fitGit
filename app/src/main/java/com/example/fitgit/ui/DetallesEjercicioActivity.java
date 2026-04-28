package com.example.fitgit.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.fitgit.R;
import com.example.fitgit.model.Ejercicio;
import com.google.android.material.chip.Chip;

public class DetallesEjercicioActivity extends AppCompatActivity {

    // Constante para la API Key (idealmente debería estar en una clase de constantes)
    private static final String API_KEY = "84a7879aafmshd2ebff39f76e114p1e4397jsn321495fa09a5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        // 1. Configurar Toolbar para poder volver atrás
        Toolbar toolbar = findViewById(R.id.toolbar_detalle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del Ejercicio");
        }

        // 2. Recuperar el objeto Ejercicio
        Ejercicio ejercicio = (Ejercicio) getIntent().getSerializableExtra("ejercicio_seleccionado");

        if (ejercicio != null) {
            // Vincular vistas
            TextView tvNombre = findViewById(R.id.tvDetalleNombre);
            TextView tvInstrucciones = findViewById(R.id.tvDetalleInstrucciones);
            ImageView ivGif = findViewById(R.id.ivDetalleGif); // Asegúrate de que este ID existe en tu XML
            Chip chipMusculo = findViewById(R.id.chipDetalleMusculo);
            Chip chipEquipo = findViewById(R.id.chipDetalleEquipo);

            // Setear textos simples
            tvNombre.setText(ejercicio.getNombre());
            chipMusculo.setText(ejercicio.getMusculoObjetivo());
            chipEquipo.setText(ejercicio.getEquipamiento());

            // 3. CARGA DEL GIF (Usando la lógica de seguridad que funcionó)
            GlideUrl glideUrl = new GlideUrl(ejercicio.getUrlGif(), new LazyHeaders.Builder()
                    .addHeader("x-rapidapi-key", API_KEY)
                    .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .build());

            Glide.with(this)
                    .asGif()
                    .load(glideUrl)
                    .placeholder(R.drawable.imagen_ejemplo)
                    .into(ivGif);

            // 4. Formatear Instrucciones
            if (ejercicio.getInstrucciones() != null && !ejercicio.getInstrucciones().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ejercicio.getInstrucciones().size(); i++) {
                    sb.append("Step ").append(i + 1).append("\n")
                            .append(ejercicio.getInstrucciones().get(i)).append("\n\n");
                }
                tvInstrucciones.setText(sb.toString());
            } else {
                tvInstrucciones.setText("No instructions available for this exercise.");
            }
        }
    }

    // Método para que el botón de atrás de la Toolbar funcione
    @Override
    public boolean onSupportNavigateUp() {
        // Esta es la forma moderna de decir "ve hacia atrás"
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}