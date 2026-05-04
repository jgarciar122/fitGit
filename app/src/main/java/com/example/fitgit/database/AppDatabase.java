package com.example.fitgit.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.fitgit.model.Ejercicio;
import com.example.fitgit.model.Rutina;
import com.example.fitgit.model.RutinaEjercicioCrossRef;

@Database(entities = {
        Ejercicio.class,
        Rutina.class,
        RutinaEjercicioCrossRef.class
}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract EjercicioDao ejercicioDao();
    public abstract RutinaDao rutinaDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "fitgit_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}