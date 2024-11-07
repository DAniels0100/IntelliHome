package com.example.intellihome;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.ArrayList;

public class FiltrosDialog extends AppCompatDialogFragment {

    public interface FiltrosDialogListener {
        void onFiltrosAplicados(Bundle bundle);
    }

    private FiltrosDialogListener listener;
    private EditText inputUbicacion;
    private EditText inputPersonasMin, inputPersonasMax;
    private EditText inputPrecioMin, inputPrecioMax;
    private EditText inputHabitacionesMin, inputHabitacionesMax;
    private Button aplicarFiltros;

    // Checkboxes de las nuevas amenidades
    private CheckBox checkBoxWifi, checkBoxAireAcondicionado, checkBoxCocinaEquipada,
            checkBoxLavadora, checkBoxEstacionamiento, checkBoxTV, checkBoxPatio,
            checkBoxTerraza, checkBoxPiscina, checkBoxCalefaccion, checkBoxParrilla,
            checkBoxJacuzzi, checkBoxCajaSeguridad, checkBoxSecadora, checkBoxGimnasio,
            checkBoxChimenea, checkBoxCine, checkBoxJuegos, checkBoxBillar, checkBoxPingpong,
            checkBoxBar, checkBoxFogata, checkBoxJardin, checkBoxPlaya;

    private ImageView busquedaMaps;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FiltrosDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FiltrosDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.filtros, null);

        // Inicializando los campos de ubicación
        inputUbicacion = view.findViewById(R.id.input_ubicacion);

        // Inicializando el ImageView para abrir Google Maps
        busquedaMaps = view.findViewById(R.id.busqueda_maps);
        busquedaMaps.setOnClickListener(v -> {
        });

        // Inicializando los campos de rango para precio, personas y habitaciones
        inputPrecioMin = view.findViewById(R.id.input_precio_min);
        inputPrecioMax = view.findViewById(R.id.input_precio_max);
        inputPersonasMin = view.findViewById(R.id.input_personas_min);
        inputPersonasMax = view.findViewById(R.id.input_personas_max);
        inputHabitacionesMin = view.findViewById(R.id.input_habitaciones_min);
        inputHabitacionesMax = view.findViewById(R.id.input_habitaciones_max);

        // Inicializando los CheckBox de amenidades
        checkBoxWifi = view.findViewById(R.id.checkbox_wifi);
        checkBoxAireAcondicionado = view.findViewById(R.id.checkbox_aire_acondicionado);
        checkBoxCocinaEquipada = view.findViewById(R.id.checkbox_cocina_equipada);
        checkBoxLavadora = view.findViewById(R.id.checkbox_lavadora);
        checkBoxEstacionamiento = view.findViewById(R.id.checkbox_estacionamiento);
        checkBoxTV = view.findViewById(R.id.checkbox_tv);
        checkBoxPatio = view.findViewById(R.id.checkbox_patio);
        checkBoxTerraza = view.findViewById(R.id.checkbox_terraza);
        checkBoxPiscina = view.findViewById(R.id.checkbox_piscina);
        checkBoxCalefaccion = view.findViewById(R.id.checkbox_calefaccion);
        checkBoxParrilla = view.findViewById(R.id.checkbox_parrilla);
        checkBoxJacuzzi = view.findViewById(R.id.checkbox_jacuzzi);
        checkBoxCajaSeguridad = view.findViewById(R.id.checkbox_caja_seguridad);
        checkBoxSecadora = view.findViewById(R.id.checkbox_secadora);
        checkBoxGimnasio = view.findViewById(R.id.checkbox_gimnasio);
        checkBoxChimenea = view.findViewById(R.id.checkbox_chimenea);
        checkBoxCine = view.findViewById(R.id.checkbox_cine);
        checkBoxJuegos = view.findViewById(R.id.checkbox_juegos);
        checkBoxBillar = view.findViewById(R.id.checkbox_billar);
        checkBoxPingpong = view.findViewById(R.id.checkbox_pingpong);
        checkBoxBar = view.findViewById(R.id.checkbox_bar);
        checkBoxFogata = view.findViewById(R.id.checkbox_fogata);
        checkBoxJardin = view.findViewById(R.id.checkbox_jardin);
        checkBoxPlaya = view.findViewById(R.id.checkbox_playa);

        // Inicializando el botón de aplicar filtros
        aplicarFiltros = view.findViewById(R.id.boton_aplicar_filtros);

        aplicarFiltros.setOnClickListener(v -> {
            // Obteniendo valores de los filtros
            String ubicacion = inputUbicacion.getText().toString();
            String precioMin = inputPrecioMin.getText().toString();
            String precioMax = inputPrecioMax.getText().toString();
            String personasMin = inputPersonasMin.getText().toString();
            String personasMax = inputPersonasMax.getText().toString();
            String habitacionesMin = inputHabitacionesMin.getText().toString();
            String habitacionesMax = inputHabitacionesMax.getText().toString();

            // Obteniendo las amenidades seleccionadas
            ArrayList<String> amenidadesSeleccionadas = new ArrayList<>();
            if (checkBoxWifi.isChecked()) amenidadesSeleccionadas.add("Wifi");
            if (checkBoxAireAcondicionado.isChecked()) amenidadesSeleccionadas.add("Aire acondicionado");
            if (checkBoxCocinaEquipada.isChecked()) amenidadesSeleccionadas.add("Cocina equipada");
            if (checkBoxLavadora.isChecked()) amenidadesSeleccionadas.add("Lavadora");
            if (checkBoxEstacionamiento.isChecked()) amenidadesSeleccionadas.add("Estacionamiento");
            if (checkBoxTV.isChecked()) amenidadesSeleccionadas.add("TV");
            if (checkBoxPatio.isChecked()) amenidadesSeleccionadas.add("Patio");
            if (checkBoxTerraza.isChecked()) amenidadesSeleccionadas.add("Terraza");
            if (checkBoxPiscina.isChecked()) amenidadesSeleccionadas.add("Piscina");
            if (checkBoxCalefaccion.isChecked()) amenidadesSeleccionadas.add("Calefacción");
            if (checkBoxParrilla.isChecked()) amenidadesSeleccionadas.add("Parrilla");
            if (checkBoxJacuzzi.isChecked()) amenidadesSeleccionadas.add("Jacuzzi");
            if (checkBoxCajaSeguridad.isChecked()) amenidadesSeleccionadas.add("Caja de Seguridad");
            if (checkBoxSecadora.isChecked()) amenidadesSeleccionadas.add("Secadora");
            if (checkBoxGimnasio.isChecked()) amenidadesSeleccionadas.add("Gimnasio");
            if (checkBoxChimenea.isChecked()) amenidadesSeleccionadas.add("Chimenea");
            if (checkBoxCine.isChecked()) amenidadesSeleccionadas.add("Sala de cine");
            if (checkBoxJuegos.isChecked()) amenidadesSeleccionadas.add("Sala de juegos");
            if (checkBoxBillar.isChecked()) amenidadesSeleccionadas.add("Billar");
            if (checkBoxPingpong.isChecked()) amenidadesSeleccionadas.add("Ping Pong");
            if (checkBoxBar.isChecked()) amenidadesSeleccionadas.add("Bar");
            if (checkBoxFogata.isChecked()) amenidadesSeleccionadas.add("Fogata");
            if (checkBoxJardin.isChecked()) amenidadesSeleccionadas.add("Jardín");
            if (checkBoxPlaya.isChecked()) amenidadesSeleccionadas.add("Playa");

            // Creando un Bundle con los filtros y enviarlo a la muestra de propiedades
            Bundle bundle = new Bundle();
            bundle.putString("ubicacion", ubicacion);
            bundle.putString("precioMin", precioMin);
            bundle.putString("precioMax", precioMax);
            bundle.putString("personasMin", personasMin);
            bundle.putString("personasMax", personasMax);
            bundle.putString("habitacionesMin", habitacionesMin);
            bundle.putString("habitacionesMax", habitacionesMax);
            bundle.putStringArrayList("amenidadesSeleccionadas", amenidadesSeleccionadas);

            // Enviando los filtros a través de la interfaz
            listener.onFiltrosAplicados(bundle);
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }

}
