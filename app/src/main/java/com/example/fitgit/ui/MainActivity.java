package com.example.fitgit.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.fitgit.adapter.AdaptadorEjercicios;
import com.example.fitgit.databinding.ActivityMainBinding;
import com.example.fitgit.viewmodel.EjercicioViewModel;
import com.google.android.material.chip.Chip;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EjercicioViewModel viewModel;
    private AdaptadorEjercicios adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. RecyclerView
        adaptador = new AdaptadorEjercicios();
        binding.rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEjercicios.setAdapter(adaptador);

        // 3. ViewModel
        viewModel = new ViewModelProvider(this).get(EjercicioViewModel.class);

        viewModel.getEjercicios().observe(this, ejercicios -> {
            if (ejercicios != null) {
                adaptador.setEjercicios(ejercicios);
            }
        });

        configurarFiltros();

        configurarNavegacion();
    }

    private void configurarFiltros() {
        binding.grupoFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int idSeleccionado = checkedIds.get(0);
            Chip chip = findViewById(idSeleccionado);
            String texto = chip.getText().toString().toLowerCase();

            String filtro;
            switch (texto) {
                case "pecho":      filtro = "chest"; break;
                case "espalda":    filtro = "back"; break;
                case "hombros":    filtro = "shoulders"; break;
                case "brazos":     filtro = "upper arms"; break;
                case "antebrazos": filtro = "lower arms"; break;
                case "piernas":    filtro = "upper legs"; break;
                case "gemelos":    filtro = "lower legs"; break;
                case "cintura":    filtro = "waist"; break;
                case "cuello":     filtro = "neck"; break;
                case "cardio":     filtro = "cardio"; break;
                default:           filtro = "todos"; break;
            }

            viewModel.filtrar(filtro);

        });
    }

    private void configurarNavegacion() {
        binding.navegacionInferior.setOnItemSelectedListener(item -> {
            // En lugar de cambiar un título de Toolbar, cambiamos el hint del SearchBar
            String titulo = item.getTitle().toString();
            binding.searchBar.setHint("Buscar en " + titulo + "...");

            // Aquí es donde más adelante cargarás los Fragments si quieres
            return true;
        });
    }
}