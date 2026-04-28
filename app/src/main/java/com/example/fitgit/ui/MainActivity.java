package com.example.fitgit.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider; // IMPORTANTE: Nueva importación

import com.example.fitgit.R;
import com.example.fitgit.adapter.AdaptadorEjercicios;
import com.example.fitgit.viewmodel.EjercicioViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView navegacionInferior;
    private Toolbar toolbar;
    private EjercicioViewModel viewModel;
    private RecyclerView rvEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de insets (bordes del sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vista_principal), (v, insets) -> {
            Insets barrasSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barrasSistema.left, barrasSistema.top, barrasSistema.right, barrasSistema.bottom);
            return insets;
        });

        // 1. Configurar Toolbar
        toolbar = findViewById(R.id.toolbar_principal);
        setSupportActionBar(toolbar);

        // 2. Configurar RecyclerView (Solo la estructura, sin datos aún)
        rvEjercicios = findViewById(R.id.rvEjercicios);
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));

        // 3. Inicializar el ViewModel (El cerebro de la pantalla)
        viewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);

        // 4. SUSCRIPCIÓN A DATOS REALES (El corazón de la conexión)
        // Observamos el LiveData. Cuando la API responda, este bloque se ejecutará automáticamente.
        viewModel.getEjercicios().observe(this, ejercicios -> {
            if (ejercicios != null && !ejercicios.isEmpty()) {
                // Si hay datos, los ponemos
                AdaptadorEjercicios adaptador = new AdaptadorEjercicios(ejercicios);
                rvEjercicios.setAdapter(adaptador);
                android.util.Log.d("FITGIT_OK", "Ejercicios cargados: " + ejercicios.size());
            } else {
                // Si no hay datos, evitamos que el programa explote
                Toast.makeText(this, "No se han podido cargar los ejercicios. Revisa tu conexión.", Toast.LENGTH_LONG).show();
                android.util.Log.e("FITGIT_ERROR", "La lista de ejercicios llegó nula o vacía");
            }
        });

        // 5. Navegación Inferior
        navegacionInferior = findViewById(R.id.navegacion_inferior);
        navegacionInferior.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_ejercicios) {
                toolbar.setTitle("Buscador de Ejercicios");
                return true;
            } else if (id == R.id.nav_rutinas) {
                toolbar.setTitle("Mis Rutinas");
                return true;
            } else if (id == R.id.nav_progreso) {
                toolbar.setTitle("Mi Progreso");
                return true;
            }
            return false;
        });

        // 6. Chips de Filtro (Visuales por ahora)
        ChipGroup grupoFiltros = findViewById(R.id.grupo_filtros);
        grupoFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int idSeleccionado = checkedIds.get(0);
                Chip chip = findViewById(idSeleccionado);
                Toast.makeText(this, "Filtrando por: " + chip.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        // 1. Definir las categorías (según la API de ExerciseDB)
        String[] categorias = {"todos", "back", "cardio", "chest", "lower arms", "lower legs", "neck", "shoulders", "upper arms", "upper legs", "waist"};

// 2. Configurar el Spinner en el onCreate
        Spinner spinner = findViewById(R.id.spinnerFiltro);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

// 3. Escuchar cuando el usuario cambia la selección
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = categorias[position];
                // Llamamos al ViewModel para que filtre (tienes que añadir el método en el ViewModel que llame al repo)
                viewModel.filtrar(seleccion);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}