package com.example.fitgit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.fitgit.model.RutinaConConteo;
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

        adaptador = new AdaptadorRutinas();
        binding.rvRutinas.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        binding.rvRutinas.setAdapter(adaptador);

        binding.fabAddRutina.setOnClickListener(v -> mostrarDialogoNuevaRutina());

        cargarRutinas();

        adaptador.setOnRutinaClickListener(rutina -> {
            Fragment detalle = DetalleRutinaFragment.newInstance(rutina.id, rutina.nombre);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, detalle)
                    .addToBackStack(null)
                    .commit();
        });

        adaptador.setOnEliminarRutinaListener(rutina -> {
            new MaterialAlertDialogBuilder(requireContext(), R.style.DialogRedondeado)
                    .setTitle("Eliminar rutina")
                    .setMessage("¿Seguro que quieres eliminar \"" + rutina.nombre + "\"? Se perderán todos sus ejercicios.")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            Rutina r = new Rutina(rutina.nombre, "");
                            r.setId(rutina.id);
                            db.rutinaDao().eliminarRutina(r);
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void mostrarDialogoNuevaRutina() {
        final EditText input = new EditText(requireContext());
        input.setHint("Rutina pierna");

        new MaterialAlertDialogBuilder(requireContext(), R.style.DialogRedondeado)
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
            getActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "¡Carpeta '" + nombre + "' creada!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void cargarRutinas() {
        db.rutinaDao().obtenerRutinasConConteo().observe(getViewLifecycleOwner(), rutinas -> {
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