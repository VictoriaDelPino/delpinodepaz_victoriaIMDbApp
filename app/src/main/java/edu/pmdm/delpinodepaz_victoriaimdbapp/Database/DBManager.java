package edu.pmdm.delpinodepaz_victoriaimdbapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;

/*Clase DBManager que gestiona las operaciones de la base de datos relacionadas con los favoritos del usuario.
 Proporciona métodos para inicializar la base de datos, obtener favoritos, agregar y eliminar películas favoritas.*/
public class DBManager {

    // Instancia del helper para manejar la base de datos
    private static DBhelper dBhelper;

    /*Inicializa la instancia de la base de datos.
    Este método se llama antes de usar cualquier otro método de DBManager.*/
    public static void init(Context context) {
        if (dBhelper == null) {
            dBhelper = DBhelper.getInstance(context);
        }
    }

    // Obtiene la lista de películas favoritas de un usuario específico.
    public static List<Movie> getUserFavorites(String userEmail) {
        List<Movie> movieList = new ArrayList<>();
        SQLiteDatabase db = dBhelper.getReadableDatabase();

        // Consulta SQL para obtener las películas favoritas del usuario
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
            Log.e("Error", "Error al obtener favoritos", e);
        }
        return movieList;
    }

    /*Agrega una película a la lista de favoritos de un usuario.
    Si la película ya existe en la base de datos, no se insertará de nuevo.*/
    public static void setUserFavorite(String userEmail, Movie movie) {
        if (userEmail == null || userEmail.isEmpty() || movie == null) {
            Log.e("Database_", "Datos inválidos para favorito");
            return;
        }
        try {
            // Inserta la película en la tabla de favoritos si no existe
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
        } catch (Exception e) {
            Log.e("Error", "Error al insertar favorito: " + e.getMessage(), e);
            throw e;
        }
    }

    //Elimina una película de la lista de favoritos del usuario.
    public static void deleteUserFavorite(Context context, String userEmail, String movieId) {
        SQLiteDatabase db;
        try {
            db = dBhelper.getWritableDatabase();
            // Consulta SQL para eliminar la película favorita del usuario
            String SQL = "DELETE FROM favorites WHERE user_id = ? AND movie_id = ?";
            db.execSQL(SQL, new Object[]{userEmail, movieId});

            // Notifica al usuario que la película ha sido eliminada
            Toast.makeText(context, "Película eliminada de favoritos", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("Error", "Error al eliminar favorito", e);
            Toast.makeText(context, "Error al eliminar la película", Toast.LENGTH_SHORT).show();
        }
    }
}