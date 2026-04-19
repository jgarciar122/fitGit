package com.example.fitgit.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitgit.R;
import com.example.fitgit.model.Ejercicio;
import com.google.android.material.chip.Chip;

public class DetallesEjercicioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        // Recuperar el ejercicio enviado
        Ejercicio ejercicio = (Ejercicio) getIntent().getSerializableExtra("ejercicio_seleccionado");

        if (ejercicio != null) {
            TextView tvNombre = findViewById(R.id.tvDetalleNombre);
            TextView tvInstrucciones = findViewById(R.id.tvDetalleInstrucciones);
            Chip chipMusculo = findViewById(R.id.chipDetalleMusculo);
            Chip chipEquipo = findViewById(R.id.chipDetalleEquipo);

            tvNombre.setText(ejercicio.getNombre());
            chipMusculo.setText(ejercicio.getMusculoObjetivo());
            chipEquipo.setText(ejercicio.getEquipamiento());

            // Convertir la lista de instrucciones en un texto con saltos de línea
            if (ejercicio.getInstrucciones() != null) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ejercicio.getInstrucciones().size(); i++) {
                    sb.append(i + 1).append(". ").append(ejercicio.getInstrucciones().get(i)).append("\n\n");
                }
                tvInstrucciones.setText(sb.toString());
            } else {
                tvInstrucciones.setText("Instrucciones no disponibles.");
            }
        }
    }
}