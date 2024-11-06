package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intellihome.adapter.AdapterPropiedad;
import com.example.intellihome.pojo.Propiedad;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePage extends AppCompatActivity {


    DatabaseReference reference;
    ArrayList<Propiedad> propiedadArrayList;
    RecyclerView rv;
    SearchView busqueda;
    AdapterPropiedad adapterPropiedad;

    LinearLayoutManager layoutManager;

    public String montoPorPagar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        reference = FirebaseDatabase.getInstance().getReference().child("Propiedades");
        rv = findViewById(R.id.rv);
        busqueda = findViewById(R.id.busquedaInput);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        propiedadArrayList = new ArrayList<>();
        adapterPropiedad = new AdapterPropiedad(propiedadArrayList);
        rv.setAdapter(adapterPropiedad);


        adapterPropiedad.setOnItemClickListener(new AdapterPropiedad.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(HomePage.this, ReservacionPropiedad.class);
                intent.putExtra("montoPorPagar", propiedadArrayList.get(position).getPrecio());
                startActivity(intent);
                montoPorPagar = propiedadArrayList.get(position).getPrecio();
            }
        });

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        propiedadArrayList.clear(); // Clear list before adding new elements
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            // Check for null values and ensure correct structure
                            Propiedad propiedad = snapshot1.getValue(Propiedad.class);
                            if (propiedad != null && propiedad.getNombre() != null) { // Example check
                                propiedadArrayList.add(propiedad);
                            } else {
                                Log.e("HomePage", "Propiedad is null or missing fields.");
                            }
                        }
                        adapterPropiedad.notifyDataSetChanged();
                    } else {
                        Log.e("HomePage", "No data found at the specified path.");
                    }
                } catch (Exception e) {
                    Log.e("HomePage", "Error in onDataChange: ", e);
                    Toast.makeText(HomePage.this, "Error loading data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomePage", "Error al acceder a la base de datos: ", error.toException());
                Toast.makeText(HomePage.this, "Error al acceder a la base de datos: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        busqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                buscar(s);
                return true;
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
                    startActivity(new Intent(HomePage.this, Perfil.class));
                    finish();
                }
            });

            domoticaMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomePage.this, Domotica.class));
                    finish();
                }
            });

            historialMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomePage.this, Historial.class));
                    finish();
                }
            });

            busquedaMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(HomePage.this, Domotica.class));
                    finish();
                }
            });

        } catch (Exception e) {
            // Muestra un mensaje en caso de error
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace(); // Imprime la traza de la excepción en Logcat
        }


    }

    private void buscar(String s) {
        ArrayList<Propiedad> listaPropiedades = new ArrayList<>();
        for(Propiedad obj: propiedadArrayList){
            if(obj.getNombre().toLowerCase().contains(s.toLowerCase()) || obj.getAmenidades().toLowerCase().contains(s.toLowerCase())
            || obj.getPrecio().toLowerCase().contains(s.toLowerCase()) || obj.getUbicacion().toLowerCase().contains(s.toLowerCase())){
                listaPropiedades.add(obj);
            }
        }
        AdapterPropiedad adapterPropiedad = new AdapterPropiedad(listaPropiedades);
        rv.setAdapter(adapterPropiedad);
    }
}
