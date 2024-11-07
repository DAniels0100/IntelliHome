package com.example.intellihome.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.intellihome.R;
import com.example.intellihome.pojo.Propiedad;

import java.util.List;

public class AdapterPropiedad extends RecyclerView.Adapter<AdapterPropiedad.viewHolderPropiedad> {

    private List<Propiedad> propiedadList;
    private OnItemClickListener listener;

    public AdapterPropiedad(List<Propiedad> propiedadList) {
        this.propiedadList = propiedadList;
    }

    // Define an interface to handle clicks
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Set the listener from the activity
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolderPropiedad onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_propiedades, parent, false);
        return new viewHolderPropiedad(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderPropiedad holder, int position) {
        Propiedad propiedad = propiedadList.get(position);

        holder.nombre.setText("Nombre: "+propiedad.getNombre());
        holder.ubicacion.setText("Ubicacion: "+propiedad.getUbicacion());
        holder.amenidades.setText("Amenidades disponibles: "+propiedad.getAmenidades());
        holder.precio.setText("Costo por noche: "+propiedad.getPrecio());
        holder.cantidadHabitaciones.setText("Habitaciones: "+ String.valueOf(propiedad.getCantidadHabitaciones()));
        holder.cantidadPersonas.setText("Cantidad de personas: "+ String.valueOf(propiedad.getCantidadPersonas()));

        // Load background image with Glide
        Glide.with(holder.itemView.getContext())
                .load(propiedad.getImagenUrl())
                .into(holder.imagenFondo);
    }

    @Override
    public int getItemCount() {
        return propiedadList.size();
    }

    // Método para actualizar los datos en el adaptador
    public void updateData(List<Propiedad> nuevaLista) {
        propiedadList = nuevaLista;
        notifyDataSetChanged();
    }

    public class viewHolderPropiedad extends RecyclerView.ViewHolder {

        TextView nombre, ubicacion, amenidades, precio, cantidadHabitaciones, cantidadPersonas;
        ImageView imagenFondo;

        public viewHolderPropiedad(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombreTv);
            ubicacion = itemView.findViewById(R.id.ubicacionTv);
            amenidades = itemView.findViewById(R.id.amenidadesCasaTv);
            precio = itemView.findViewById(R.id.precioTv);
            cantidadHabitaciones = itemView.findViewById(R.id.cantidadHabitacionesTv);
            cantidadPersonas = itemView.findViewById(R.id.cantidadPersonasTv);
            imagenFondo = itemView.findViewById(R.id.imagenCargadaPropiedad);

            // Set the click listener on the entire item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}