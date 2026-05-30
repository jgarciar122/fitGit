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

    @ColumnInfo(name = "nombre_es")
    private String nombreEs;

    @ColumnInfo(name = "instrucciones_es")
    private List<String> instruccionesEs;

    @ColumnInfo(name = "traducido")
    private boolean traducido = false;

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

    public String getNombreEs() { return nombreEs; }
    public void setNombreEs(String nombreEs) { this.nombreEs = nombreEs; }

    public List<String> getInstruccionesEs() { return instruccionesEs; }
    public void setInstruccionesEs(List<String> instruccionesEs) { this.instruccionesEs = instruccionesEs; }

    public boolean isTraducido() { return traducido; }
    public void setTraducido(boolean traducido) { this.traducido = traducido; }

    public String getNombreMostrar() {
        if (java.util.Locale.getDefault().getLanguage().equals("en")) {
            return nombre;
        }
        return (nombreEs != null && !nombreEs.isEmpty()) ? nombreEs : nombre;
    }

    public List<String> getInstruccionesMostrar() {
        if (java.util.Locale.getDefault().getLanguage().equals("en")) {
            return instrucciones;
        }
        return (instruccionesEs != null && !instruccionesEs.isEmpty()) ? instruccionesEs : instrucciones;
    }

    public String getUrlGif() {
        return "https://exercisedb.p.rapidapi.com/image?exerciseId=" + id + "&resolution=360";
    }

    @Override
    public String toString() {
        return "Ejercicio{" + "id='" + id + '\'' + ", nombre='" + nombre + '\'' + '}';
    }
}