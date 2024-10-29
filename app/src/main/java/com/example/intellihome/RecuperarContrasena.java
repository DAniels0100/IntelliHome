package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContrasena extends AppCompatActivity {

    FirebaseAuth auth;
    EditText recuperarEmail;
    Button recuperarEmailBtn;
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar_contrasena);

        EditText recuperarEmail = findViewById(R.id.recuperarContrasena);
        Button recuperarEmailBtn = findViewById(R.id.recuperarContrasenaBtn);

        auth = FirebaseAuth.getInstance();

        recuperarContrasenaEmail();
    }

    public void recuperarContrasenaEmail(){
        recuperarEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = recuperarEmail.getText().toString().trim();
                if (!email.isEmpty()){
                    enviarCorreo();
                }
            }
        });
    }

    public void enviarCorreo(){
        auth.setLanguageCode("es");
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(RecuperarContrasena.this, LogIn.class));
                    Toast.makeText(RecuperarContrasena.this, "Por favor revisar su correo", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RecuperarContrasena.this, "Error al enviar el correo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}