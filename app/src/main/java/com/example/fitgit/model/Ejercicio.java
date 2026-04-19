package com.example.fitgit.model;

import java.io.Serializable;
import java.util.List;

public class Ejercicio implements Serializable {
    private String id;
    private String nombre;
    private String parteCuerpo;
    private String musculoObjetivo;
    private String equipamiento;
    private String urlGif;
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