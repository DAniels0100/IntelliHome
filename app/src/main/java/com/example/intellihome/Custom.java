package com.example.intellihome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Custom extends AppCompatActivity {

    // Declaración de variables de instancia (fields)
    private ImageView btnColor1, btnColor2, btnColor3, btnColor4, btnColor5;

    // Lista de colores en hexadecimal
    private String[] colorPalette = {"#477993", "#99DBE0", "#7BD47B", "#E44847", "#FFAF67"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        // Inicializamos las ImageView
        btnColor1 = findViewById(R.id.btnColor1);
        btnColor2 = findViewById(R.id.btnColor2);
        btnColor3 = findViewById(R.id.btnColor3);
        btnColor4 = findViewById(R.id.btnColor4);
        btnColor5 = findViewById(R.id.btnColor5);

        // Asignamos los colores de la paleta a los ImageView utilizando un filtro de color
        btnColor1.setBackgroundColor(Color.parseColor(colorPalette[0]));
        btnColor2.setBackgroundColor(Color.parseColor(colorPalette[1]));
        btnColor3.setBackgroundColor(Color.parseColor(colorPalette[2]));
        btnColor4.setBackgroundColor(Color.parseColor(colorPalette[3]));
        btnColor5.setBackgroundColor(Color.parseColor(colorPalette[4]));

        btnColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAColorDisplay(colorPalette[0]);
            }
        });

        btnColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAColorDisplay(colorPalette[1]);
            }
        });

        btnColor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAColorDisplay(colorPalette[2]);
            }
        });

        btnColor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAColorDisplay(colorPalette[3]);
            }
        });

        btnColor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAColorDisplay(colorPalette[4]);
            }
        });
    }

    // Método para iniciar la nueva actividad con el color seleccionado
    private void irAColorDisplay(String color) {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra("SELECTED_COLOR", color);
        startActivity(intent);
    }
}
