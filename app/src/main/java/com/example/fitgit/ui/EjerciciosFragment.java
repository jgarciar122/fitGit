package com.example.fitgit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgit.adapter.AdaptadorEjercicios;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.databinding.FragmentEjerciciosBinding;
import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.model.Rutina;
import com.example.fitgit.model.RutinaEjercicioCrossRef;
import com.example.fitgit.viewmodel.EjercicioViewModel;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EjerciciosFragment extends Fragment {

    private FragmentEjerciciosBinding binding;
    private EjercicioViewModel viewModel;
    private AdaptadorEjercicios adaptador;
    private AppDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEjerciciosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getDatabase(requireContext());

        // 1. Configurar RecyclerView
        adaptador = new AdaptadorEjercicios();
        binding.rvEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEjercicios.setAdapter(adaptador);

        // --- NUEVO: Escuchar el clic en un ejercicio ---
        adaptador.setOnEjercicioClickListener(ejercicio -> {
            mostrarSelectorDeRutinas(ejercicio);
        });

        // 2. ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(EjercicioViewModel.class);
        viewModel.getEjercicios().observe(getViewLifecycleOwner(), ejercicios -> {
            if (ejercicios != null) {
                adaptador.setEjercicios(ejercicios);
            }
        });

        configurarFiltros();
    }

    private void mostrarSelectorDeRutinas(Ejercicio ejercicio) {
        // Obtenemos las rutinas que el usuario ha creado en la otra pestaña
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.rutinaDao().obtenerTodasLasRutinas(userId).observe(getViewLifecycleOwner(), rutinas -> {
            if (rutinas == null || rutinas.isEmpty()) {
                Toast.makeText(getContext(), "Crea primero una rutina en 'Mis Rutinas'", Toast.LENGTH_LONG).show();
                return;
            }

            // Preparamos la lista de nombres para el diálogo
            String[] nombresRutinas = new String[rutinas.size()];
            for (int i = 0; i < rutinas.size(); i++) {
                nombresRutinas[i] = rutinas.get(i).getNombre();
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("¿A qué rutina lo añadimos?")
                    .setItems(nombresRutinas, (dialog, which) -> {
                        Rutina rutinaSeleccionada = rutinas.get(which);
                        vincularEjercicioConRutina(ejercicio, rutinaSeleccionada);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void vincularEjercicioConRutina(Ejercicio ejercicio, Rutina rutina) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Creamos la unión en la tabla puente
            RutinaEjercicioCrossRef union = new RutinaEjercicioCrossRef();
            union.rutinaId = rutina.getId();
            union.ejercicioId = ejercicio.getId();

            db.rutinaDao().añadirEjercicioARutina(union);

            // Volvemos al hilo de la interfaz para avisar al usuario
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), ejercicio.getNombre() + " añadido a " + rutina.getNombre(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void configurarFiltros() {
        binding.grupoFiltros.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;

            int idSeleccionado = checkedIds.get(0);
            Chip chip = binding.getRoot().findViewById(idSeleccionado);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}