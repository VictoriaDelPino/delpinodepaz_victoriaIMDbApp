package edu.pmdm.delpinodepaz_victoriaimdbapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import edu.pmdm.delpinodepaz_victoriaimdbapp.databinding.ActivityMainBinding;
import edu.pmdm.delpinodepaz_victoriaimdbapp.test.TestApi;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button btnLogOut;
    private TextView txtEmail;
    private TextView txtUserName;
    private ImageView imgUserPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TestApi.test();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Usuario autenticado
            Log.d("NavigationDrawer", "Usuario: " + currentUser.getEmail());
        } else {
            // Redirigir al SignInActivity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);

        // Buscamos el botón dentro del header
        btnLogOut = headerView.findViewById(R.id.btnLogOut);
        txtEmail = headerView.findViewById(R.id.txtEmail);
        txtUserName = headerView.findViewById(R.id.txtUserName);
        imgUserPhoto = headerView.findViewById(R.id.imgUserPhoto);

        if(currentUser!=null) {
            //meter nombre e imagen

            txtEmail.setText(currentUser.getEmail());
            txtUserName.setText(currentUser.getDisplayName());
            // Descargar y establecer la imagen de forma asincrónica
            new Thread(() -> {
                Bitmap bitmap = downloadImage(currentUser.getPhotoUrl().toString());
                runOnUiThread(() -> {
                    if (bitmap != null) {
                        imgUserPhoto.setImageBitmap(bitmap);
                    } else {
                        imgUserPhoto.setImageResource(R.drawable.ic_launcher_foreground); // Imagen por defecto
                    }
                });
            }).start();
        }
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private Bitmap downloadImage(String urlString) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}