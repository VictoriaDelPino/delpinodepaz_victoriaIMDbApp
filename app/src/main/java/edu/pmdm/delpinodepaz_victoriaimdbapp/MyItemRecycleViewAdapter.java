package edu.pmdm.delpinodepaz_victoriaimdbapp;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import  java.net.URL;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.pmdm.delpinodepaz_victoriaimdbapp.Movies.Movie;

public class MyItemRecycleViewAdapter extends RecyclerView.Adapter<MyItemRecycleViewAdapter.ViewHolder> {

    private List<Movie> movieList;
    private Context context;
    private OnItemClickListener listener;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4); // Para carga eficiente


    public interface OnItemClickListener {
        void onItemClick(Movie movie);
        void onItemLongClick(Movie movie);
    }

    public MyItemRecycleViewAdapter(List<Movie> movieList, Context context, OnItemClickListener listener) {
        this.movieList = movieList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);


        String imageUrl = movie.getPhoto();//String con la url
        // Asignar una imagen por defecto mientras se carga la imagen real
        holder.imageButton.setImageResource(R.drawable.ic_launcher_foreground);

        // Cargar la imagen en un hilo de fondo
        executorService.execute(() -> {
            Bitmap bitmap = downloadImage(imageUrl);
            if (bitmap != null) {
                holder.imageButton.post(() -> holder.imageButton.setImageBitmap(bitmap)); // Asignar en la UI
            }
        });

        // URL de la imagen // Crear un hilo para cargar la imagen de forma asíncrona
       /* new Thread(() -> {
            try {
                System.out.println("Cargando imagen...");
                // Descargar la imagen
                URL url = new URL(imageUrl);
                BufferedImage image = ImageIO.read(url);
                if (image != null) {
                    System.out.println("Imagen cargada con éxito: " + image.getWidth() + "x" + image.getHeight());
                } else {
                    System.out.println("Error: No se pudo cargar la imagen.");
                }
            } catch (Exception e) { System.err.println("Error al cargar la imagen: " + e.getMessage()); }
        }).start();     */
        holder.imageButton.setImageResource(R.drawable.ic_launcher_foreground);//asignarlo la imagen cargada


        // Manejo de clics
        holder.imageButton.setOnClickListener(v -> listener.onItemClick(movie));

        holder.imageButton.setOnLongClickListener(v -> {
            listener.onItemLongClick(movie);
            return true;
        });
    }

    private Bitmap downloadImage(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

        @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageButton);
        }
    }
}
