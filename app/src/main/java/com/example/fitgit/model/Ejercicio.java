package com.example.fitgit.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

@Entity(tableName = "tabla_ejercicios")
public class Ejercicio implements Serializable {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String nombre;

    @ColumnInfo(name = "bodyPart")
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

    public Ejercicio() {}

    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getParteCuerpo() { return parteCuerpo; }
    public void setParteCuerpo(String parteCuerpo) { this.parteCuerpo = parteCuerpo; }

    public String getMusculoObjetivo() { return musculoObjetivo; }
    public void setMusculoObjetivo(String musculoObjetivo) { this.musculoObjetivo = musculoObjetivo; }

    public String getEquipamiento() { return equipamiento; }
    public void setEquipamiento(String equipamiento) { this.equipamiento = equipamiento; }

    public List<String> getInstrucciones() { return instrucciones; }
    public void setInstrucciones(List<String> instrucciones) { this.instrucciones = instrucciones; }

    public List<String> getMusculosSecundarios() { return musculosSecundarios; }
    public void setMusculosSecundarios(List<String> musculosSecundarios) { this.musculosSecundarios = musculosSecundarios; }

    public String getUrlGif() {
        return "https://exercisedb.p.rapidapi.com/image?exerciseId=" + id + "&resolution=360";
    }

    @Override
    public String toString() {
        return "Ejercicio{" + "id='" + id + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}