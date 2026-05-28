package com.example.fitgit.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgit.adapter.AdaptadorSerie;
import com.example.fitgit.database.AppDatabase;
import com.example.fitgit.databinding.FragmentRegistroSeriesBinding;
import com.example.fitgit.model.SerieRegistro;
import com.example.fitgit.model.Sesion;
import com.example.fitgit.repository.RepositorioSesion;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class RegistroSeriesBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_EJERCICIO_ID = "ejercicio_id";
    private static final String ARG_EJERCICIO_NOMBRE = "ejercicio_nombre";
    private static final String ARG_RUTINA_ID = "rutina_id";

    private FragmentRegistroSeriesBinding binding;
    private AdaptadorSerie adaptador;
    private String ejercicioId;
    private String ejercicioNombre;
    private int rutinaId;

    public static RegistroSeriesBottomSheet newInstance(String ejercicioId, String ejercicioNombre, int rutinaId) {
        RegistroSeriesBottomSheet sheet = new RegistroSeriesBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_EJERCICIO_ID, ejercicioId);
        args.putString(ARG_EJERCICIO_NOMBRE, ejercicioNombre);
        args.putInt(ARG_RUTINA_ID, rutinaId);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistroSeriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            ejercicioId = getArguments().getString(ARG_EJERCICIO_ID);
            ejercicioNombre = getArguments().getString(ARG_EJERCICIO_NOMBRE);
            rutinaId = getArguments().getInt(ARG_RUTINA_ID);
        }

        String fecha = new SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("es")).format(new Date());
        binding.tvNombreEjercicioSheet.setText(ejercicioNombre);
        binding.tvFechaSheet.setText(fecha);

        adaptador = new AdaptadorSerie();
        binding.rvSeries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSeries.setAdapter(adaptador);

        cargarUltimasSeries();

        binding.btnAAdirSerie.setOnClickListener(v -> adaptador.añadirSerie());
        binding.btnGuardarSesion.setOnClickListener(v -> guardarSesion());
    }

    private void cargarUltimasSeries() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AppDatabase db = AppDatabase.getDatabase(requireContext());

        Executors.newSingleThreadExecutor().execute(() -> {
            List<SerieRegistro> ultimas = db.sesionDao().obtenerUltimasSeries(ejercicioId, userId);
            requireActivity().runOnUiThread(() -> {
                if (ultimas != null && !ultimas.isEmpty()) {
                    for (SerieRegistro s : ultimas) {
                        adaptador.añadirSerieConDatos(s.kg, s.repeticiones);
                    }
                } else {
                    adaptador.añadirSerie();
                }
            });
        });
    }

    private void guardarSesion() {
        List<AdaptadorSerie.FilaSerie> filas = adaptador.getSeries();

        if (filas.isEmpty()) {
            Toast.makeText(getContext(), "Añade al menos una serie", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        AppDatabase db = AppDatabase.getDatabase(requireContext());
        RepositorioSesion repositorio = new RepositorioSesion(requireActivity().getApplication());

        Executors.newSingleThreadExecutor().execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long inicioDia = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            long finDia = cal.getTimeInMillis();

            Sesion sesionExistente = db.sesionDao().obtenerSesionDeHoy(rutinaId, userId, inicioDia, finDia);

            long sesionId;
            if (sesionExistente != null) {
                sesionId = sesionExistente.id;
            } else {
                Sesion nuevaSesion = new Sesion(rutinaId, userId);
                sesionId = repositorio.insertarSesionSincrono(nuevaSesion);
            }

            List<SerieRegistro> series = new ArrayList<>();
            for (AdaptadorSerie.FilaSerie fila : filas) {
                series.add(new SerieRegistro((int) sesionId, ejercicioId, fila.kg, fila.reps));
            }
            repositorio.insertarSeries(series, userId);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "¡Sesión guardada!", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}