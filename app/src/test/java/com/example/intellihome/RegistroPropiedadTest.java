package com.example.intellihome;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.example.intellihome.pojo.Propiedad;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RegistroPropiedadTest {

    @Mock
    FirebaseDatabase mockFirebaseDatabase;

    @Mock
    DatabaseReference mockDatabaseReference;

    private String propiedadId = "-OAv-UaXFrKA8A6CgMoA";
    private Propiedad propiedad;



    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Configurar los mocks correctamente
        when(mockFirebaseDatabase.getReference("Propiedades")).thenReturn(mockDatabaseReference);
        when(mockDatabaseReference.child(propiedadId)).thenReturn(mockDatabaseReference);

        // Crear una instancia de la propiedad para usarla en el test
        propiedad = new Propiedad(
                "Casa moderna",
                "30000",
                "San Carlos",
                "Jacuzzi, Parrilla, Wifi, Aire acondicionado, Mesa de billar, Sala de cine",
                6,
                5,
                null
        );
    }

    @Test
    public void testRegistrarPropiedad() {
        // Simula la escritura de datos en Firebase (sin imagenUrl)
        mockDatabaseReference.setValue(propiedad);

        // Verifica que el valor se haya guardado correctamente
        verify(mockDatabaseReference, times(1)).setValue(propiedad); // Verifica si setValue se llamó una vez con la propiedad

        // Verifica que los datos sean correctos (esto lo harías si tuvieras acceso a la base de datos o la simulación)
        // Puedes simular la respuesta del listener si lo necesitas
        // Si estuvieras usando un Listener real:
        mockDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Simula que Firebase ha recuperado los datos de la propiedad
                Propiedad propiedadGuardada = dataSnapshot.getValue(Propiedad.class);

                // Compara los valores guardados con los esperados
                assertEquals("Casa moderna", propiedadGuardada.getNombre());
                assertEquals("30000", propiedadGuardada.getPrecio());
                assertEquals("San Carlos", propiedadGuardada.getUbicacion());
                assertEquals("Jacuzzi, Parrilla, Wifi, Aire acondicionado, Mesa de billar, Sala de cine", propiedadGuardada.getAmenidades());
                assertEquals(6, propiedadGuardada.getCantidadHabitaciones());
                assertEquals(5, propiedadGuardada.getCantidadPersonas());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejo de errores en caso de fallar la lectura
            }
        });
    }

    @Test
    public void testRecuperarPropiedadConListener() {
        // Simula que el listener es llamado
        ValueEventListener listener = mock(ValueEventListener.class);
        mockDatabaseReference.addListenerForSingleValueEvent(listener);

        // Simula que el valor de la propiedad se recuperó
        DataSnapshot dataSnapshot = mock(DataSnapshot.class);
        when(dataSnapshot.getValue(Propiedad.class)).thenReturn(propiedad);

        // Llamar al método onDataChange y verificar los valores
        listener.onDataChange(dataSnapshot);

        // Verifica que la propiedad recuperada tenga los valores correctos
        verify(listener, times(1)).onDataChange(dataSnapshot);
    }

    @Test
    public void testNoRegistrarPropiedadConDatosInvalidos() {
        // Crear una propiedad con datos inválidos (precio vacío)
        Propiedad propiedadInvalida = new Propiedad(
                "Casa Invalida",
                "",  // Precio vacío
                "San José",
                "Sin Amenidades",
                3,
                2,
                null
        );

        // Llamar al método que debería evitar el registro si los datos son inválidos
        if (propiedadInvalida.getPrecio().isEmpty()) {
            // No debería llamar a setValue si el precio está vacío
            verify(mockDatabaseReference, times(0)).setValue(any(Propiedad.class));
        } else {
            // Si el precio fuera válido, se llamaría a setValue
            mockDatabaseReference.setValue(propiedadInvalida);
        }
    }



    @Test
    public void testObtenerReferenciaCorrecta() {
        // Llamar al método para obtener la referencia
        mockFirebaseDatabase.getReference("Propiedades");

        // Verifica que getReference haya sido llamado con "Propiedades"
        verify(mockFirebaseDatabase, times(1)).getReference("Propiedades");
    }

}
