package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class Historial extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        // Encuentra el ViewPager2 en el layout
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        // Crea una instancia del adaptador
        AdaptadorViewPager adapter = new AdaptadorViewPager(this);
        // Asigna el adaptador al ViewPager2
        viewPager.setAdapter(adapter);

        // Encuentra el TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        // Configura el TabLayout con el ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Arrendador");
                                break;
                            case 1:
                                tab.setText("Arrendatario");
                                break;
                        }
                    }
                }).attach();

        // Encuentra el botón de regreso en el layout
        ImageView btnBack = findViewById(R.id.botonRegresar);

        // Asigna el evento onClick al botón de regreso
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige a HomePage
                Intent intent = new Intent(Historial.this, HomePage.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            }
        });
        try {
            // Encuentra el ImageView de perfil
            ImageView perfilMenu = findViewById(R.id.perfilmenu);
            ImageView domoticaMenu = findViewById(R.id.domotica);
            ImageView historialMenu = findViewById(R.id.historial);
            ImageView busquedaMenu = findViewById(R.id.busquedamenu);

            perfilMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Historial.this, Perfil.class));
                    finish();
                }
            });

            domoticaMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Historial.this, Domotica.class));
                    finish();
                }
            });

            historialMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Historial.this, RegistroPropiedad.class));
                    finish();
                }
            });

            busquedaMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Historial.this, Historial.class));
                    finish();
                }
            });

        } catch (Exception e) {
            // Muestra un mensaje en caso de error
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace(); // Imprime la traza de la excepción en Logcat
        }

    }
}
