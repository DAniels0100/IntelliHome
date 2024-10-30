package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class PestanaDos extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla el layout para esta pestaña
        View view = inflater.inflate(R.layout.pestana_dos, container, false);

        // Encuentra el botón en el layout
        Button button = view.findViewById(R.id.button);

        // Asigna el evento onClick al botón
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para iniciar la actividad RegistroPropiedad
                Intent intent = new Intent(getActivity(), HomePage.class);
                startActivity(intent);
            }
        });

        return view; // Devuelve la vista inflada
    }
}