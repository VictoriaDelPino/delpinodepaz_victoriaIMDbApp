package edu.pmdm.delpinodepaz_victoriaimdbapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;

public class DBManager {

    private static DBhelper dBhelper;

    public static void init(Context context) {
        if (dBhelper == null) {
            dBhelper = DBhelper.getInstance(context);
        }
    }

    public static List<Movie> getUserFavorites(String userEmail) {
        List<Movie> movieList = new ArrayList<>();
        SQLiteDatabase db = dBhelper.getReadableDatabase();

        // Query con parámetros seguros
        String SQL = "SELECT * FROM favorites WHERE user_id = ?";

        try (Cursor cursor = db.rawQuery(SQL, new String[]{userEmail})) {
            while (cursor.moveToNext()) {
                Movie movie = new Movie();
                movie.setId(cursor.getString(cursor.getColumnIndexOrThrow("movie_id")));
                movie.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
                movie.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                movie.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow("release_date")));
                movie.setPhoto(cursor.getString(cursor.getColumnIndexOrThrow("url_photo")));
                movieList.add(movie);
            }
        } catch (Exception e) {
            Log.e("Database_", "Error al obtener favoritos", e);
        }
        return movieList;
    }

    public static void setUserFavorite(String userEmail, Movie movie) {
        if (userEmail == null || userEmail.isEmpty() || movie == null) {
            Log.e("Database_", "Datos inválidos para favorito");
            return;
        }

        try {
            // Query con parámetros (?) y sin concatenación
            String SQL = "INSERT OR IGNORE INTO favorites VALUES (?, ?, ?, ?, ?, ?)";

            SQLiteDatabase db = dBhelper.getWritableDatabase();
            db.execSQL(SQL, new Object[]{
                    userEmail,
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDescription(),
                    movie.getReleaseDate(),
                    movie.getPhoto()
            });

            Log.d("Database_", "Película añadida: " + movie.getTitle());
        } catch (Exception e) {
            Log.e("Database_", "Error al insertar favorito: " + e.getMessage(), e);
            throw e;
        }
    }

    public static void deleteUserFavorite(Context context, String userEmail, String movieId) {
        SQLiteDatabase db = null;
        try {
            db = dBhelper.getWritableDatabase();
            String SQL = "DELETE FROM favorites WHERE user_id = ? AND movie_id = ?";
            db.execSQL(SQL, new Object[]{userEmail, movieId});

            Toast.makeText(context, "Película eliminada de favoritos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Database_", "Error al eliminar favorito", e);
            Toast.makeText(context, "Error al eliminar la película", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }
}