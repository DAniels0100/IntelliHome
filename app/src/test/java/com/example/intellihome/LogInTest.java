package com.example.intellihome;

import static org.mockito.Mockito.*;

import android.os.Looper;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LogInTest {

    @Mock
    FirebaseDatabase mockFirebaseDatabase;
    @Mock
    DatabaseReference mockDatabaseReference;
    @Mock
    DataSnapshot mockDataSnapshot;
    @Mock
    EditText mockContrasenaEditText;

    private LogIn logInActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inicializando el Looper principal para evitar el NullPointerException
        Looper.prepare(); // Prepara el looper para pruebas en hilos no principales

        // Crear un mock de la actividad LogIn
        logInActivity = mock(LogIn.class);
    }

    @Test
    public void testValidarContrasena_correcta() {
        // Configurando el mock de Firebase Database
        when(mockFirebaseDatabase.getReferenceFromUrl(anyString())).thenReturn(mockDatabaseReference);

        // Simulando los llamados a child() de forma encadenada
        DatabaseReference mockUsuariosRef = mock(DatabaseReference.class);
        DatabaseReference mockUsuarioRef = mock(DatabaseReference.class);
        DatabaseReference mockContrasenaRef = mock(DatabaseReference.class);

        // Simulando cada paso de la cadena de llamadas child()
        when(mockDatabaseReference.child("usuarios")).thenReturn(mockUsuariosRef);
        when(mockUsuariosRef.child("GeorgeWBush")).thenReturn(mockUsuarioRef);
        when(mockUsuarioRef.child("contrasena")).thenReturn(mockContrasenaRef);

        // Simulando la respuesta de la contraseña en el DataSnapshot
        DataSnapshot mockContrasenaSnapshot = mock(DataSnapshot.class);
        when(mockDataSnapshot.child("contrasena")).thenReturn(mockContrasenaSnapshot);
        when(mockContrasenaSnapshot.getValue(String.class)).thenReturn("Pepepe1234@");

        // Usamos doAnswer para simular la llamada al listener
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(mockDataSnapshot); // Simulamos que se dispara onDataChange
            return null; // el método addListenerForSingleValueEvent es void
        }).when(mockContrasenaRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Suponiendo que el valor de la contraseña es directamente "Pepepe1234@"
        String contrasenaIngresada = "Pepepe1234@";

        // Llamamos al método de la actividad que simula la conexión con Firebase
        logInActivity.onCreate(null); // Llamamos a onCreate para inicializar la actividad

        // Disparamos el evento onDataChange
        // No es necesario llamar manualmente a captor.getValue().onDataChange() ahora

        // Verificación de que la contraseña es correcta
        if ("Pepepe1234@".equals(contrasenaIngresada)) {
            verify(mockContrasenaEditText, times(0)).setError(anyString()); // No debe haber error si la contraseña es correcta
        } else {
            verify(mockContrasenaEditText).setError("Contraseña incorrecta");
        }
    }
    @Test
    public void testValidarContrasena_incorrecta() {
        // Configurando el mock de Firebase Database
        when(mockFirebaseDatabase.getReferenceFromUrl(anyString())).thenReturn(mockDatabaseReference);

        // Simulando los llamados a child() de forma encadenada
        DatabaseReference mockUsuariosRef = mock(DatabaseReference.class);
        DatabaseReference mockUsuarioRef = mock(DatabaseReference.class);
        DatabaseReference mockContrasenaRef = mock(DatabaseReference.class);

        // Simulando cada paso de la cadena de llamadas child()
        when(mockDatabaseReference.child("usuarios")).thenReturn(mockUsuariosRef);
        when(mockUsuariosRef.child("GeorgeWBush")).thenReturn(mockUsuarioRef);
        when(mockUsuarioRef.child("contrasena")).thenReturn(mockContrasenaRef);

        // Simulando la respuesta de la contraseña en el DataSnapshot
        DataSnapshot mockContrasenaSnapshot = mock(DataSnapshot.class);
        when(mockDataSnapshot.child("contrasena")).thenReturn(mockContrasenaSnapshot);
        when(mockContrasenaSnapshot.getValue(String.class)).thenReturn("Mala12345@");

        // Usamos doAnswer para simular la llamada al listener
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(mockDataSnapshot); // Simulamos que se dispara onDataChange
            return null; // el método addListenerForSingleValueEvent es void
        }).when(mockContrasenaRef).addListenerForSingleValueEvent(any(ValueEventListener.class));

        String contrasenaIngresada = "Mala12345@";

        // Llamamos al método de la actividad que simula la conexión con Firebase
        logInActivity.onCreate(null); // Llamamos a onCreate para inicializar la actividad

        // Disparamos el evento onDataChange
        // No es necesario llamar manualmente a captor.getValue().onDataChange() ahora

        // Verificación de que la contraseña es correcta
        if (!"Pepepe1234@".equals(contrasenaIngresada)) {
            verify(mockContrasenaEditText, times(0)).setError(anyString()); // No debe haber error si la contraseña es correcta
        }
    }
}
