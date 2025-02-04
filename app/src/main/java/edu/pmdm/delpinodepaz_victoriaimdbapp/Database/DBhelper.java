package edu.pmdm.delpinodepaz_victoriaimdbapp.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoriteMovies.db";
    private static final int DATABASE_VERSION = 1;
    private static DBhelper instance;

    // Schema mejorado (release_date como TEXT)
    private static final String SQL_CREATE_FAVORITES =
            "CREATE TABLE favorites (" +
                    "user_id TEXT NOT NULL," +
                    "movie_id TEXT NOT NULL," +
                    "title TEXT NOT NULL," +
                    "description TEXT," +
                    "release_date TEXT," +  // Cambiado a TEXT
                    "url_photo TEXT," +
                    "PRIMARY KEY (user_id, movie_id))";

    private DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DBhelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBhelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Lógica de migración si es necesario
    }
}