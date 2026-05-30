package com.example.fitgit.util;

import java.util.HashMap;
import java.util.Map;

public class TraductorLocal {

    private static final Map<String, String> PARTES_CUERPO = new HashMap<>();
    private static final Map<String, String> EQUIPAMIENTO = new HashMap<>();

    static {
        PARTES_CUERPO.put("back",       "Espalda");
        PARTES_CUERPO.put("cardio",     "Cardio");
        PARTES_CUERPO.put("chest",      "Pecho");
        PARTES_CUERPO.put("lower arms", "Antebrazos");
        PARTES_CUERPO.put("lower legs", "Gemelos");
        PARTES_CUERPO.put("neck",       "Cuello");
        PARTES_CUERPO.put("shoulders",  "Hombros");
        PARTES_CUERPO.put("upper arms", "Brazos");
        PARTES_CUERPO.put("upper legs", "Piernas");
        PARTES_CUERPO.put("waist",      "Cintura");

        EQUIPAMIENTO.put("assisted",              "Asistido");
        EQUIPAMIENTO.put("band",                  "Banda elástica");
        EQUIPAMIENTO.put("barbell",               "Barra");
        EQUIPAMIENTO.put("body weight",           "Peso corporal");
        EQUIPAMIENTO.put("bosu ball",             "Bosu");
        EQUIPAMIENTO.put("cable",                 "Polea");
        EQUIPAMIENTO.put("dumbbell",              "Mancuerna");
        EQUIPAMIENTO.put("elliptical machine",    "Elíptica");
        EQUIPAMIENTO.put("ez barbell",            "Barra EZ");
        EQUIPAMIENTO.put("hammer",                "Martillo");
        EQUIPAMIENTO.put("kettlebell",            "Kettlebell");
        EQUIPAMIENTO.put("leverage machine",      "Máquina de palanca");
        EQUIPAMIENTO.put("medicine ball",         "Balón medicinal");
        EQUIPAMIENTO.put("olympic barbell",       "Barra olímpica");
        EQUIPAMIENTO.put("resistance band",       "Banda de resistencia");
        EQUIPAMIENTO.put("roller",                "Rodillo");
        EQUIPAMIENTO.put("rope",                  "Cuerda / Polea");
        EQUIPAMIENTO.put("skierg machine",        "Máquina SkiErg");
        EQUIPAMIENTO.put("sled machine",          "Trineo");
        EQUIPAMIENTO.put("smith machine",         "Máquina Smith");
        EQUIPAMIENTO.put("stability ball",        "Pelota de estabilidad");
        EQUIPAMIENTO.put("stationary bike",       "Bicicleta estática");
        EQUIPAMIENTO.put("stepmill machine",      "Stepmill");
        EQUIPAMIENTO.put("tire",                  "Neumático");
        EQUIPAMIENTO.put("trap bar",              "Barra trampa");
        EQUIPAMIENTO.put("upper body ergometer",  "Ergómetro de tren superior");
        EQUIPAMIENTO.put("weighted",              "Con peso");
        EQUIPAMIENTO.put("wheel roller",          "Rueda abdominal");
    }

    public static String traducirParteCuerpo(String en) {
        if (en == null) return "";
        if (java.util.Locale.getDefault().getLanguage().equals("en")) {
            return capitalizar(en);
        }
        String traduccion = PARTES_CUERPO.get(en.toLowerCase().trim());
        return traduccion != null ? traduccion : capitalizar(en);
    }

    /** Convierte el texto del chip (en español o inglés) al valor en inglés que espera la API */
    public static String parteCuerpoAIngles(String chipText) {
        if (chipText == null) return "todos";
        String lower = chipText.toLowerCase().trim();
        // Si ya es una clave inglesa válida (app en inglés)
        if (PARTES_CUERPO.containsKey(lower)) return lower;
        // Buscar por valor español → clave inglesa
        for (Map.Entry<String, String> entrada : PARTES_CUERPO.entrySet()) {
            if (entrada.getValue().toLowerCase().equals(lower)) {
                return entrada.getKey();
            }
        }
        return "todos";
    }

    public static String traducirEquipamiento(String en) {
        if (en == null) return "";
        if (java.util.Locale.getDefault().getLanguage().equals("en")) {
            return capitalizar(en);
        }
        String traduccion = EQUIPAMIENTO.get(en.toLowerCase().trim());
        return traduccion != null ? traduccion : capitalizar(en);
    }

    private static String capitalizar(String texto) {
        if (texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
