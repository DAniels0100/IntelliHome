package com.example.intellihome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Perfil extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    TextInputEditText nombrePerfil, apellidosPerfil, edadPerfil, telefonoPerfil, nicknamePerfil, correoPerfil, domicilioPerfil, pasatiemposPerfil, preferenciasPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Encuentra el botón de regreso en el layout
        ImageView btnBack = findViewById(R.id.botonRegresar);
        Button btnMetodoPagos = findViewById(R.id.metodosPagoBtn);
        Button btnLogOut = findViewById(R.id.logOutBtn);

        // Declaracion de los textViews donde se muestran los datos del usuario
        nombrePerfil = findViewById(R.id.nombreInput);
        apellidosPerfil = findViewById(R.id.apellidoInput);
        edadPerfil = findViewById(R.id.edadInput);
        telefonoPerfil = findViewById(R.id.numeroTelefonoInput);
        nicknamePerfil = findViewById(R.id.nicknameInput);
        correoPerfil = findViewById(R.id.emailInput);
        domicilioPerfil = findViewById(R.id.domicilioInput);
        pasatiemposPerfil = findViewById(R.id.pasatiemposInput);
        preferenciasPerfil = findViewById(R.id.preferenciasCasaInput);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        nombrePerfil.setEnabled(false);
        apellidosPerfil.setEnabled(false);
        edadPerfil.setEnabled(false);
        telefonoPerfil.setEnabled(false);
        nicknamePerfil.setEnabled(false);
        correoPerfil.setEnabled(false);
        domicilioPerfil.setEnabled(false);
        pasatiemposPerfil.setEnabled(false);
        preferenciasPerfil.setEnabled(false);

        // Obtener el nickname desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedNickname = sharedPreferences.getString("nickname", null);

        // Cargar los datos del usuario si hay un nickname guardado
        if (savedNickname != null) {
            //Toast.makeText(this, "el nickname usado"+savedNickname,Toast.LENGTH_SHORT).show();
            cargarDatosUsuario(savedNickname);
        } else {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            // Redirigir a la pantalla de inicio de sesión si no hay usuario
        }


        // Asigna el evento onClick al botón de regreso
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige a HomePage
                startActivity(new Intent(Perfil.this, HomePage.class));
                finish(); // Finaliza la actividad actual
            }
        });



        btnMetodoPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Perfil.this, PasarelaPagos.class));
                finish(); // Finaliza la actividad actual
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        try {
            // Encuentra el ImageView
            ImageView domoticaMenu = findViewById(R.id.domotica);
            ImageView historialMenu = findViewById(R.id.historial);
            ImageView busquedaMenu = findViewById(R.id.busquedamenu);


            domoticaMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Perfil.this, Domotica.class));
                    finish();
                }
            });

            historialMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Perfil.this, RegistroPropiedad.class));
                    finish();
                }
            });

            busquedaMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Perfil.this, Historial.class));
                    finish();
                }
            });

        } catch (Exception e) {
            // Muestra un mensaje en caso de error
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace(); // Imprime la traza de la excepción en Logcat
        }


    }

    private void cargarDatosUsuario(String nickname) {
        Toast.makeText(this, "El nickname usado: " + nickname, Toast.LENGTH_SHORT).show();

        // Consulta a la base de datos para encontrar el usuario que coincide con el nickname
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound = false; // Bandera para verificar si se encontró el usuario
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String dbNickname = userSnapshot.child("nickname").getValue(String.class);
                    if (dbNickname != null && dbNickname.equals(nickname)) {
                        userFound = true; // Usuario encontrado
                        // Recuperar los datos del snapshot y mostrarlos en los campos
                        nombrePerfil.setText(userSnapshot.child("nombre").getValue(String.class));
                        apellidosPerfil.setText(userSnapshot.child("apellido").getValue(String.class));
                        edadPerfil.setText(userSnapshot.child("edad").getValue(String.class));
                        telefonoPerfil.setText(userSnapshot.child("numeroTelefono").getValue(String.class));
                        correoPerfil.setText(userSnapshot.child("email").getValue(String.class));
                        nicknamePerfil.setText(dbNickname);

                        // Verificar y cargar otros datos si están disponibles
                        String domicilio = userSnapshot.child("domicilio").getValue(String.class);
                        if (domicilio != null) {
                            domicilioPerfil.setText(domicilio);
                        }

                        String pasatiempos = userSnapshot.child("pasatiempos").getValue(String.class);
                        if (pasatiempos != null) {
                            pasatiemposPerfil.setText(pasatiempos);
                        }

                        String preferencias = userSnapshot.child("preferencias").getValue(String.class);
                        if (preferencias != null) {
                            preferenciasPerfil.setText(preferencias);
                        }
                        break; // Salir del bucle si se encuentra el usuario
                    }
                }

                if (!userFound) {
                    Toast.makeText(Perfil.this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PerfilActivity", "Error al cargar los datos: " + error.getMessage());
                Toast.makeText(Perfil.this, "Error al cargar los datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Cierra la sesión de Firebase
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("nickname"); // Eliminar el nickname guardado
        editor.apply();

        // Redirigir a la pantalla de login
        Intent intent = new Intent(Perfil.this, LogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finaliza la actividad actual
    }
}
