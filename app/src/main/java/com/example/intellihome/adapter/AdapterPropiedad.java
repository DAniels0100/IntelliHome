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

        holder.nombre.setText(propiedad.getNombre());
        holder.ubicacion.setText(propiedad.getUbicacion());
        holder.amenidades.setText(propiedad.getAmenidades());
        holder.precio.setText(propiedad.getPrecio());
        holder.cantidadHabitaciones.setText(String.valueOf(propiedad.getCantidadHabitaciones()));
        holder.cantidadPersonas.setText(String.valueOf(propiedad.getCantidadPersonas()));
    }

    @Override
    public int getItemCount() {
        return propiedadList.size();
    }

    public class viewHolderPropiedad extends RecyclerView.ViewHolder {

        TextView nombre, ubicacion, amenidades, precio, cantidadHabitaciones, cantidadPersonas;

        public viewHolderPropiedad(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            nombre = itemView.findViewById(R.id.nombreTv);
            ubicacion = itemView.findViewById(R.id.ubicacionTv);
            amenidades = itemView.findViewById(R.id.amenidadesCasaTv);
            precio = itemView.findViewById(R.id.precioTv);
            cantidadHabitaciones = itemView.findViewById(R.id.cantidadHabitacionesTv);
            cantidadPersonas = itemView.findViewById(R.id.cantidadPersonasTv);

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
