package com.example.intellihome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intellihome.R;
import com.example.intellihome.pojo.Propiedad;

import java.util.List;

public class AdapterPropiedad extends RecyclerView.Adapter<AdapterPropiedad.viewHolderPropiedad> {

    List<Propiedad> propiedadList;

    public AdapterPropiedad(List<Propiedad> propiedadList) {
        this.propiedadList = propiedadList;
    }

    @NonNull
    @Override
    public viewHolderPropiedad onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_propiedades,parent,false);
        viewHolderPropiedad holder = new viewHolderPropiedad(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderPropiedad holder, int position) {
        Propiedad propiedad = propiedadList.get(position);

        holder.nombre.setText(propiedad.getNombre());
        holder.ubicacion.setText(propiedad.getUbicacion());
        holder.amenidades.setText(propiedad.getAmenidades());
        holder.precio.setText(propiedad.getPrecio());

        // Convertir int a String antes de asignar al TextView
        holder.cantidadHabitaciones.setText(String.valueOf(propiedad.getCantidadHabitaciones()));
        holder.cantidadPersonas.setText(String.valueOf(propiedad.getCantidadPersonas()));
    }

    @Override
    public int getItemCount() {
        return propiedadList.size();
    }

    public class viewHolderPropiedad extends RecyclerView.ViewHolder {

        TextView nombre, ubicacion, amenidades, precio, cantidadHabitaciones, cantidadPersonas;


        public viewHolderPropiedad(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombreTv);
            ubicacion = itemView.findViewById(R.id.ubicacionTv);
            amenidades = itemView.findViewById(R.id.amenidadesCasaTv);
            precio = itemView.findViewById(R.id.precioTv);
            cantidadHabitaciones = itemView.findViewById(R.id.cantidadHabitacionesTv);
            cantidadPersonas = itemView.findViewById(R.id.cantidadPersonasTv);

        }
    }
}
