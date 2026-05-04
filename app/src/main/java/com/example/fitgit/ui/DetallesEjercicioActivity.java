package com.example.fitgit.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.fitgit.R;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.databinding.ActivityDetalleEjercicioBinding;
import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.model.Rutina;
import com.example.fitgit.model.RutinaEjercicioCrossRef;

import java.util.List;
import java.util.concurrent.Executors;

public class DetallesEjercicioActivity extends AppCompatActivity {

    private ActivityDetalleEjercicioBinding binding;
    private static final String API_KEY = "84a7879aafmshd2ebff39f76e114p1e4397jsn321495fa09a5";
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetalleEjercicioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getDatabase(this);

        Ejercicio ejercicio = (Ejercicio) getIntent().getSerializableExtra("ejercicio_seleccionado");
        boolean yaEnRutina = getIntent().getBooleanExtra("ya_en_rutina", false); // ← nuevo

        if (ejercicio != null) {
            setupToolbar();
            cargarDatos(ejercicio);

            if (yaEnRutina) {
                // Modo "ya está en la rutina"
                binding.btnGuardarEnRutinaDetalle.setText("✓ Ya está en la rutina");
                binding.btnGuardarEnRutinaDetalle.setEnabled(false);
                binding.btnGuardarEnRutinaDetalle.setIcon(null);
                binding.btnGuardarEnRutinaDetalle.setAlpha(0.6f);
                binding.btnGuardarEnRutinaDetalle.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                androidx.core.content.ContextCompat.getColor(this, android.R.color.darker_gray)
                        )
                );
            } else {
                // Modo normal: permitir añadir
                binding.btnGuardarEnRutinaDetalle.setOnClickListener(v -> {
                    mostrarSelectorDeRutinas(ejercicio);
                });
            }
        }
        }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarDetalle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        binding.toolbarDetalle.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void cargarDatos(Ejercicio ejercicio) {
        binding.tvDetalleNombre.setText(ejercicio.getNombre());
        binding.chipDetalleMusculo.setText(ejercicio.getParteCuerpo());
        binding.chipDetalleEquipo.setText(ejercicio.getEquipamiento());

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

        if (ejercicio.getInstrucciones() != null) {
            StringBuilder sb = new StringBuilder();
            for (String paso : ejercicio.getInstrucciones()) {
                sb.append("• ").append(paso).append("\n\n");
            }
            binding.tvDetalleInstrucciones.setText(sb.toString().trim());
        }
    }

    private void mostrarSelectorDeRutinas(Ejercicio ejercicio) {
        // Observamos las rutinas disponibles en la base de datos
        db.rutinaDao().obtenerTodasLasRutinas().observe(this, rutinas -> {
            if (rutinas == null || rutinas.isEmpty()) {
                Toast.makeText(this, "No tienes rutinas creadas. Ve a 'Mis Rutinas' primero.", Toast.LENGTH_LONG).show();
                return;
            }

            // Crear lista de nombres para el diálogo
            String[] nombres = new String[rutinas.size()];
            for (int i = 0; i < rutinas.size(); i++) {
                nombres[i] = rutinas.get(i).getNombre();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Añadir a una rutina")
                    .setItems(nombres, (dialog, which) -> {
                        Rutina seleccionada = rutinas.get(which);
                        guardarVinculoEnDB(ejercicio, seleccionada);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void guardarVinculoEnDB(Ejercicio ejercicio, Rutina rutina) {
        Executors.newSingleThreadExecutor().execute(() -> {
            RutinaEjercicioCrossRef union = new RutinaEjercicioCrossRef();
            union.rutinaId = rutina.getId();
            union.ejercicioId = ejercicio.getId();

            db.rutinaDao().añadirEjercicioARutina(union);

            runOnUiThread(() -> {
                Toast.makeText(this, "¡Añadido a " + rutina.getNombre() + "!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}