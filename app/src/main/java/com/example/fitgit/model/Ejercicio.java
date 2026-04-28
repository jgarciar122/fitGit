package com.example.fitgit.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Ejercicio implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("name") // La API envía "name"
    private String nombre;  // Tú usas "nombre"

    @SerializedName("bodyPart")
    private String parteCuerpo;

    @SerializedName("target")
    private String musculoObjetivo;

    @SerializedName("equipment")
    private String equipamiento;

    @SerializedName("gifUrl")
    private String urlGif;

    @SerializedName("instructions")
    private List<String> instrucciones;

    public Ejercicio(String id, String nombre, String parteCuerpo, String musculoObjetivo,
                     String equipamiento, String urlGif, List<String> instrucciones) {
        this.id = id;
        this.nombre = nombre;
        this.parteCuerpo = parteCuerpo;
        this.musculoObjetivo = musculoObjetivo;
        this.equipamiento = equipamiento;
        this.urlGif = urlGif;
        this.instrucciones = instrucciones;
    }

    public String getNombre() { return nombre; }
    public String getParteCuerpo() { return parteCuerpo; }
    public String getMusculoObjetivo() { return musculoObjetivo; }
    public String getEquipamiento() { return equipamiento; }
    public String getUrlGif() { return urlGif; }

    public List<String> getInstrucciones() {
        return instrucciones;
    }
}