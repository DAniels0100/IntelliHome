package com.example.intellihome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://intellihome-293ec-default-rtdb.firebaseio.com/");
    private GoogleSignInClient googleSignInClient;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);


        //recuperacion de los inputs del usuario al intentar logear o necesitar crear/recuperar cuenta
        final EditText nombreUsuario = findViewById(R.id.nombreUsuarioLogIn);
        final EditText contrasena = findViewById(R.id.contrasenaLogIn);
        final Button logInBtn = findViewById(R.id.logInBtn);
        final Button logInWithGoogle = findViewById(R.id.googleBtn);
        final Button logInWithFacebook = findViewById(R.id.facebookBtn);
        final TextView noCuentaBtn = findViewById(R.id.noCuentaBtn);
        final TextView olvidoContrasena = findViewById(R.id.olvidoContraseñaBtn);

        // Referencia al ImageView del ícono de About
        final ImageView aboutImageView = findViewById(R.id.about);

        //click sobre btn log in
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String nicknameOcorreoTxt = nombreUsuario.getText().toString().trim();
                final String contrasenaTxt = contrasena.getText().toString().trim();

                if (nicknameOcorreoTxt.isEmpty()){
                    nombreUsuario.setError("Debe rellenar correo");
                } else if (contrasenaTxt.isEmpty()) {
                    contrasena.setError("Debe rellenar la contrasena");
                } else {
                    // Reemplazar los puntos con comas si el usuario ingresó un correo
                    String nicknameOcorreo = nicknameOcorreoTxt.contains("@") ? nicknameOcorreoTxt.replace(".", ",") : nicknameOcorreoTxt;

                    databaseReference.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Verificar si el nickname o correo formateado existe
                            if (snapshot.hasChild(nicknameOcorreo)) {
                                // Obtener la contraseña del usuario
                                String getContrasena = snapshot.child(nicknameOcorreo).child("contrasena").getValue(String.class);

                                // Comprobar si la contraseña ingresada coincide
                                if (getContrasena.equals(contrasenaTxt)) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("nickname", nicknameOcorreo); // nicknameOcorreo es el nickname formateado
                                    editor.apply();
                                    startActivity(new Intent(LogIn.this, HomePage.class));
                                } else {
                                    contrasena.setError("Contrasena incorrecta");                                }
                            } else {
                                nombreUsuario.setError("Nickname o correo no existe");                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LogIn.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        //registrarse con google
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        logInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(googleSignInClient.getSignInIntent(),1234);
            }
        });

        // se abre el layout de recuperar contrasena
        olvidoContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LogIn.this, Recuperar2.class));
            }
        });

        // se abre el layout de registrarse
        noCuentaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LogIn.this, Register.class));

            }
        });

        // Agregar un click listener al ImageView para mostrar la ventana emergente
        aboutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear el AlertDialog con la información requerida
                AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                builder.setTitle("Información de la Aplicación");
                builder.setMessage("Creadores: Systec Enterprise Technology\n"
                        + "Versión de la app: 1.1.0\n"
                        + "Dónde se realiza: Costa Rica\n"
                        + "Dónde tiene vigencia: Costa Rica");

                // Agregar un botón de 'Cerrar' para que el usuario pueda cerrar el diálogo
                builder.setPositiveButton("Cerrar", null);

                // Mostrar el diálogo
                builder.show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        startActivity(new Intent(LogIn.this, HomePage.class));
                                        finish(); // Finaliza la pantalla de login
                                    } else {
                                        Toast.makeText(LogIn.this,
                                                "Error al conectar con Google",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(this, "Cuenta de Google no encontrada", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Error Conectando: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String savedNickname = sharedPreferences.getString("nickname", null);

        if (savedNickname != null) {
            // Si hay un nickname guardado, redirigir a HomePage o Perfil
            startActivity(new Intent(this, HomePage.class));
            finish(); // Evita que el usuario vuelva a la pantalla de login
        }
    }
}