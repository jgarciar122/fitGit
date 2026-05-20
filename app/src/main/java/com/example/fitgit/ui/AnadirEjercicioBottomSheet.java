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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgit.adapter.AdaptadorEjercicios;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.databinding.FragmentAnadirEjercicioBinding;
import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.model.RutinaEjercicioCrossRef;
import com.example.fitgit.viewmodel.EjercicioViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.Executors;

public class AnadirEjercicioBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_RUTINA_ID = "rutina_id";
    private FragmentAnadirEjercicioBinding binding;
    private AdaptadorEjercicios adaptador;
    private EjercicioViewModel viewModel;
    private int rutinaId;
    private List<Ejercicio> todosLosEjercicios = new ArrayList<>();
    private Set<String> ejerciciosEnRutina = new HashSet<>();

    public static AnadirEjercicioBottomSheet newInstance(int rutinaId) {
        AnadirEjercicioBottomSheet sheet = new AnadirEjercicioBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_RUTINA_ID, rutinaId);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnadirEjercicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            rutinaId = getArguments().getInt(ARG_RUTINA_ID);
        }

        adaptador = new AdaptadorEjercicios();
        binding.rvEjerciciosAnadir.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEjerciciosAnadir.setAdapter(adaptador);

        adaptador.setOnEjercicioClickListener(ejercicio -> {
            añadirEjercicioARutina(ejercicio);
        });

        AppDatabase db = AppDatabase.getDatabase(requireContext());
        db.rutinaDao().obtenerEjerciciosDeRutina(rutinaId).observe(getViewLifecycleOwner(), ejerciciosEnRutinaLista -> {
            ejerciciosEnRutina.clear();
            if (ejerciciosEnRutinaLista != null) {
                for (Ejercicio e : ejerciciosEnRutinaLista) {
                    ejerciciosEnRutina.add(e.getId());
                }
            }
            filtrarEjercicios(binding.etBuscarEjercicio.getText().toString());
        });

        viewModel = new ViewModelProvider(requireActivity()).get(EjercicioViewModel.class);
        viewModel.getEjercicios().observe(getViewLifecycleOwner(), ejercicios -> {
            if (ejercicios != null) {
                todosLosEjercicios = ejercicios;
                filtrarEjercicios(binding.etBuscarEjercicio.getText().toString());
            }
        });

        binding.etBuscarEjercicio.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filtrarEjercicios(s.toString());
            }
        });
    }

    private void filtrarEjercicios(String query) {
        List<Ejercicio> filtrados = new ArrayList<>();
        for (Ejercicio e : todosLosEjercicios) {

            if (ejerciciosEnRutina.contains(e.getId())) continue;

            if (!query.isEmpty() && !e.getNombre().toLowerCase().contains(query.toLowerCase())) continue;
            filtrados.add(e);
        }
        adaptador.setEjercicios(filtrados);
    }

    private void añadirEjercicioARutina(Ejercicio ejercicio) {
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        Executors.newSingleThreadExecutor().execute(() -> {
            RutinaEjercicioCrossRef union = new RutinaEjercicioCrossRef();
            union.rutinaId = rutinaId;
            union.ejercicioId = ejercicio.getId();
            db.rutinaDao().añadirEjercicioARutina(union);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), ejercicio.getNombre() + " añadido", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}