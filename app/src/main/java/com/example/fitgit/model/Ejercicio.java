package com.example.fitgit.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * Modelo de datos para un Ejercicio.
 * Implementa Serializable para permitir el paso de objetos entre Activities.
 */
public class Ejercicio implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String nombre;

    @SerializedName("bodyPart")
    private String parteCuerpo;

    @SerializedName("target")
    private String musculoObjetivo;

    @SerializedName("equipment")
    private String equipamiento;

    @SerializedName("instructions")
    private List<String> instrucciones;

    @SerializedName("secondaryMuscles")
    private List<String> musculosSecundarios;

    // Constructor vacío requerido por GSON para la deserialización
    public Ejercicio() {
    }

    // --- GETTERS ---

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getParteCuerpo() {
        return parteCuerpo;
    }

    public String getMusculoObjetivo() {
        return musculoObjetivo;
    }

    public String getEquipamiento() {
        return equipamiento;
    }

    public List<String> getInstrucciones() {
        return instrucciones;
    }

    public List<String> getMusculosSecundarios() {
        return musculosSecundarios;
    }

    /**
     * Construye la URL del GIF de forma dinámica.
     * Según la nueva estructura de la API, la imagen se obtiene mediante el ID.
     */
    public String getUrlGif() {
        return "https://exercisedb.p.rapidapi.com/image?exerciseId=" + id;
    }

    @Override
    public String toString() {
        return "Ejercicio{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}