package com.example.fitgit.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class RepositorioFirestore {

    private static RepositorioFirestore instancia;
    private FirebaseFirestore db;

    private RepositorioFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    public static RepositorioFirestore getInstance() {
        if (instancia == null) instancia = new RepositorioFirestore();
        return instancia;
    }


    public void guardarRutina(String userId, int rutinaId, String nombre, String descripcion, long fecha) {
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);
        data.put("descripcion", descripcion);
        data.put("fechaCreacion", fecha);

        db.collection("usuarios").document(userId)
                .collection("rutinas").document(String.valueOf(rutinaId))
                .set(data, SetOptions.merge());
    }

    public void eliminarRutina(String userId, int rutinaId) {
        db.collection("usuarios").document(userId)
                .collection("rutinas").document(String.valueOf(rutinaId))
                .delete();
    }

    public void guardarEjercicioEnRutina(String userId, int rutinaId, String ejercicioId) {
        Map<String, Object> data = new HashMap<>();
        data.put("añadido", true);

        db.collection("usuarios").document(userId)
                .collection("rutinas").document(String.valueOf(rutinaId))
                .collection("ejercicios").document(ejercicioId)
                .set(data);
    }

    public void eliminarEjercicioDeRutina(String userId, int rutinaId, String ejercicioId) {
        db.collection("usuarios").document(userId)
                .collection("rutinas").document(String.valueOf(rutinaId))
                .collection("ejercicios").document(ejercicioId)
                .delete();
    }


    public void guardarSesion(String userId, int sesionId, int rutinaId, long fecha) {
        Map<String, Object> data = new HashMap<>();
        data.put("rutinaId", rutinaId);
        data.put("fecha", fecha);

        db.collection("usuarios").document(userId)
                .collection("sesiones").document(String.valueOf(sesionId))
                .set(data, SetOptions.merge());
    }

    public void eliminarSesion(String userId, int sesionId) {
        db.collection("usuarios").document(userId)
                .collection("sesiones").document(String.valueOf(sesionId))
                .delete();
    }

    public void guardarSerie(String userId, int sesionId, int serieId,
                             String ejercicioId, float kg, int repeticiones) {
        Map<String, Object> data = new HashMap<>();
        data.put("ejercicioId", ejercicioId);
        data.put("kg", kg);
        data.put("repeticiones", repeticiones);

        db.collection("usuarios").document(userId)
                .collection("sesiones").document(String.valueOf(sesionId))
                .collection("series").document(String.valueOf(serieId))
                .set(data);
    }

    public void eliminarSeriesDeSesion(String userId, int sesionId) {
        db.collection("usuarios").document(userId)
                .collection("sesiones").document(String.valueOf(sesionId))
                .collection("series")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }

    public void eliminarEjercicioDeSesion(String userId, int sesionId, String ejercicioId) {
        db.collection("usuarios").document(userId)
                .collection("sesiones").document(String.valueOf(sesionId))
                .collection("series")
                .whereEqualTo("ejercicioId", ejercicioId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }
}