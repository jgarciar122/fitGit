package com.example.fitgit.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.fitgit.util.TraductorLocal;
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

        adaptador = new AdaptadorEjercicios();
        binding.rvEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEjercicios.setAdapter(adaptador);

        adaptador.setOnEjercicioClickListener(ejercicio -> {
            mostrarSelectorDeRutinas(ejercicio);
        });

        viewModel = new ViewModelProvider(requireActivity()).get(EjercicioViewModel.class);
        viewModel.getEjercicios().observe(getViewLifecycleOwner(), ejercicios -> {
            if (ejercicios != null) {
                adaptador.setEjercicios(ejercicios);
            }
        });

        configurarFiltros();
        configurarBuscador();
    }

    private void configurarBuscador() {
        binding.etBuscarEjercicio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.buscarPorNombre(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void mostrarSelectorDeRutinas(Ejercicio ejercicio) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.rutinaDao().obtenerTodasLasRutinas(userId).observe(getViewLifecycleOwner(), rutinas -> {
            db.rutinaDao().obtenerTodasLasRutinas(userId).removeObservers(getViewLifecycleOwner());

            if (rutinas == null || rutinas.isEmpty()) {
                Toast.makeText(getContext(), "Crea primero una rutina en 'Mis Rutinas'", Toast.LENGTH_LONG).show();
                return;
            }

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
            RutinaEjercicioCrossRef union = new RutinaEjercicioCrossRef();
            union.rutinaId = rutina.getId();
            union.ejercicioId = ejercicio.getId();

            db.rutinaDao().añadirEjercicioARutina(union);

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

            Chip chip = binding.getRoot().findViewById(checkedIds.get(0));
            String texto = chip.getText().toString();
            String filtro = TraductorLocal.parteCuerpoAIngles(texto);
            viewModel.filtrar(filtro);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}