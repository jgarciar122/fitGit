package com.example.fitgit;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

public class FitGitApp extends Application {

    public static final String PREFS_NAME = "fitgit_prefs";
    public static final String KEY_DARK_MODE = "dark_mode";
    public static final String KEY_IDIOMA = "idioma";

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        aplicarTema(prefs);
        aplicarIdioma(prefs);
    }

    public static void aplicarTema(SharedPreferences prefs) {
        if (prefs.contains(KEY_DARK_MODE)) {
            boolean darkMode = prefs.getBoolean(KEY_DARK_MODE, false);
            AppCompatDelegate.setDefaultNightMode(
                    darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    public static void aplicarIdioma(SharedPreferences prefs) {
        String idioma = prefs.getString(KEY_IDIOMA, null);
        if (idioma != null) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(idioma));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList());
        }
    }
}
