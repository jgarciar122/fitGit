package com.example.fitgit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.fitgit.R;
import com.example.fitgit.adapter.AdaptadorRutinas;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.databinding.FragmentRutinasBinding;
import com.example.fitgit.model.Rutina;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RutinasFragment extends Fragment {

    private FragmentRutinasBinding binding;
    private AppDatabase db;
    private AdaptadorRutinas adaptador;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRutinasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getDatabase(requireContext());

        binding.fabAddRutina.setOnClickListener(v -> mostrarDialogoNuevaRutina());

        adaptador = new AdaptadorRutinas();
        binding.rvRutinas.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        binding.rvRutinas.setAdapter(adaptador);

        binding.fabAddRutina.setOnClickListener(v -> mostrarDialogoNuevaRutina());

        cargarRutinas();

        adaptador.setOnRutinaClickListener(rutina -> {
            Fragment detalle = DetalleRutinaFragment.newInstance(rutina.getId(), rutina.getNombre());

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, detalle)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void mostrarDialogoNuevaRutina() {

        final EditText input = new EditText(requireContext());
        input.setHint("Rutina pierna");

        new AlertDialog.Builder(requireContext())
                .setTitle("Nueva Rutina")
                .setMessage("¿Qué nombre le quieres poner a tu carpeta de ejercicios?")
                .setView(input)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = input.getText().toString().trim();
                    if (!nombre.isEmpty()) {
                        guardarRutina(nombre);
                    } else {
                        Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarRutina(String nombre) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Rutina nuevaRutina = new Rutina(nombre, "Mi rutina personalizada");
            db.rutinaDao().insertarRutina(nuevaRutina);

            // Volvemos al hilo principal para avisar al usuario
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "¡Carpeta '" + nombre + "' creada!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void cargarRutinas() {
        db.rutinaDao().obtenerTodasLasRutinas().observe(getViewLifecycleOwner(), rutinas -> {
            if (rutinas != null) {
                adaptador.setRutinas(rutinas);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}