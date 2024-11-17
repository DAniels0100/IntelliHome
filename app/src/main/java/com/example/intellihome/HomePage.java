package com.example.intellihome;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import java.util.List;

public class HomePage extends AppCompatActivity implements FiltrosDialog.FiltrosDialogListener {

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

        // Inicializar vistas y Firebase
        busqueda = findViewById(R.id.busqueda);
        reference = FirebaseDatabase.getInstance().getReference().child("Propiedades");
        rv = findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        // Configurar el adapter
        propiedadArrayList = new ArrayList<>();
        adapterPropiedad = new AdapterPropiedad(propiedadArrayList);
        rv.setAdapter(adapterPropiedad);

        // Configuración de búsqueda
        if (busqueda != null) {
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
        } else {
            Log.e("HomePage", "SearchView is null!");
        }

        // Configuración del filtro de búsqueda
        ImageView filtroBusqueda = findViewById(R.id.filtro_icono);
        filtroBusqueda.bringToFront();
        filtroBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("HomePage", "Filtro icon clicked");
                FiltrosDialog filtrosDialog = new FiltrosDialog();
                filtrosDialog.show(getSupportFragmentManager(), "filtros_dialog");
            }
        });

        // Configuración del clic en el adaptador
        adapterPropiedad.setOnItemClickListener(new AdapterPropiedad.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(HomePage.this, ReservacionPropiedad.class);
                intent.putExtra("montoPorPagar", propiedadArrayList.get(position).getPrecio());
                startActivity(intent);
                montoPorPagar = propiedadArrayList.get(position).getPrecio();
            }
        });

        // Listener para cargar datos de Firebase
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.exists()) {
                        propiedadArrayList.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Propiedad propiedad = snapshot1.getValue(Propiedad.class);
                            if (propiedad != null && propiedad.getNombre() != null) {
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

        // Configuración del menú de navegación
        try {
            ImageView perfilMenu = findViewById(R.id.perfilmenu);
            ImageView domoticaMenu = findViewById(R.id.domotica);
            ImageView historialMenu = findViewById(R.id.historial);
            ImageView busquedaMenu = findViewById(R.id.busquedamenu);

            perfilMenu.setOnClickListener(view -> startActivity(new Intent(HomePage.this, Perfil.class)));
            domoticaMenu.setOnClickListener(view -> startActivity(new Intent(HomePage.this, Domotica.class)));
            historialMenu.setOnClickListener(view -> startActivity(new Intent(HomePage.this, Historial.class)));
            busquedaMenu.setOnClickListener(view -> startActivity(new Intent(HomePage.this, Domotica.class)));

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void buscar(String s) {
        ArrayList<Propiedad> listaPropiedades = new ArrayList<>();
        for (Propiedad obj : propiedadArrayList) {
            if (obj.getNombre().toLowerCase().contains(s.toLowerCase()) ||
                    obj.getAmenidades().toLowerCase().contains(s.toLowerCase()) ||
                    obj.getPrecio().toLowerCase().contains(s.toLowerCase()) ||
                    obj.getUbicacion().toLowerCase().contains(s.toLowerCase())) {
                listaPropiedades.add(obj);
            }
        }
        adapterPropiedad.updateData(listaPropiedades);
    }

    // Método para aplicar filtros
    @Override
    public void onFiltrosAplicados(Bundle bundle) {
        // Extraer los filtros desde el bundle
        String ubicacion = bundle.getString("ubicacion", "");
        String precioMin = bundle.getString("precioMin", "");
        String precioMax = bundle.getString("precioMax", "");
        String personasMin = bundle.getString("personasMin", "");
        String personasMax = bundle.getString("personasMax", "");
        String habitacionesMin = bundle.getString("habitacionesMin", "");
        String habitacionesMax = bundle.getString("habitacionesMax", "");
        ArrayList<String> amenidadesSeleccionadas = bundle.getStringArrayList("amenidadesSeleccionadas");

        // Aplicar los filtros a la lista de propiedades
        aplicarFiltros(ubicacion, precioMin, precioMax, personasMin, personasMax, habitacionesMin, habitacionesMax, amenidadesSeleccionadas);
    }

    private void aplicarFiltros(String ubicacion, String precioMin, String precioMax, String personasMin,
                                String personasMax, String habitacionesMin, String habitacionesMax,
                                ArrayList<String> amenidadesSeleccionadas) {
        List<Propiedad> listaFiltrada = new ArrayList<>();

        for (Propiedad propiedad : propiedadArrayList) {
            boolean coincide = true;

            // Verificar filtros de ubicación, precio, etc.
            if (!ubicacion.isEmpty() && !propiedad.getUbicacion().equalsIgnoreCase(ubicacion)) coincide = false;
            if (!precioMin.isEmpty() && Integer.parseInt(propiedad.getPrecio()) < Integer.parseInt(precioMin)) coincide = false;
            if (!precioMax.isEmpty() && Integer.parseInt(propiedad.getPrecio()) > Integer.parseInt(precioMax)) coincide = false;
            if (!personasMin.isEmpty() && propiedad.getCantidadPersonas() < Integer.parseInt(personasMin)) coincide = false;
            if (!personasMax.isEmpty() && propiedad.getCantidadPersonas() > Integer.parseInt(personasMax)) coincide = false;
            if (!habitacionesMin.isEmpty() && propiedad.getCantidadHabitaciones() < Integer.parseInt(habitacionesMin)) coincide = false;
            if (!habitacionesMax.isEmpty() && propiedad.getCantidadHabitaciones() > Integer.parseInt(habitacionesMax)) coincide = false;

            if (coincide && amenidadesSeleccionadas != null && !amenidadesSeleccionadas.isEmpty()) {
                for (String amenidad : amenidadesSeleccionadas) {
                    if (!propiedad.getAmenidades().contains(amenidad)) {
                        coincide = false;
                        break;
                    }
                }
            }

            if (coincide) listaFiltrada.add(propiedad);
        }

        adapterPropiedad.updateData(listaFiltrada);
    }


}
