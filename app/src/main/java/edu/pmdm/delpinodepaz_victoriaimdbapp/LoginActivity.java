package edu.pmdm.delpinodepaz_victoriaimdbapp;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQ_ONE_TAP = 2;

    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar One Tap Sign-In
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id)) // Reemplaza con tu client ID
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .build();

        // Botón para iniciar sesión
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Botón pulsado");
                startSignIn();

            }
        });
    }

    private void startSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(),
                                REQ_ONE_TAP, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "One Tap Sign-In Error: " + e.getMessage());
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Autenticar con Firebase
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this, task -> {
                                if (task.isSuccessful()) {
                                    // Inicio de sesión exitoso
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d(TAG, "signInWithCredential:success, User: " + user.getDisplayName());
                                    updateUI(user);
                                } else {
                                    // Error en el inicio de sesión
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    updateUI(null);
                                }
                            });
                }
            } catch (ApiException e) {
                Log.e(TAG, "Error al obtener las credenciales de Google", e);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Verificar si el usuario ya ha iniciado sesión
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Redirigir a la actividad principal si el usuario está autenticado
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Mostrar mensaje o mantener la pantalla de inicio de sesión
            Log.d(TAG, "Usuario no autenticado");
        }
    }
}