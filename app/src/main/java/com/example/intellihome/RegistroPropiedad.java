package com.example.intellihome;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.ArrayAdapter;
import android.view.ViewGroup;


public class RegistroPropiedad extends AppCompatActivity {

    private TextInputEditText nombrePropiedadInput, cantidadPersonasInput, cantidadHabitacionesInput, precioInput, ubicacionInput, amenidadesCasaInput;
    private Button registrarCasaBtn;
    private DatabaseReference databaseReference;
    private ImageView imagenPropiedad;
    private StorageReference storageReference;
    private Uri imageUri; // Guardará la URI de la imagen seleccionada o tomada


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_propiedad);

        // Inicializa los componentes
        nombrePropiedadInput = findViewById(R.id.nombrePropiedadInput);
        cantidadPersonasInput = findViewById(R.id.cantidadPersonasInput);
        cantidadHabitacionesInput = findViewById(R.id.cantidadHabitacionesInput);
        precioInput = findViewById(R.id.precioInput);
        ubicacionInput = findViewById(R.id.ubicacionInput);
        amenidadesCasaInput = findViewById(R.id.amenidadesCasaInput);
        registrarCasaBtn = findViewById(R.id.registrarCasaBtn);
        imagenPropiedad = findViewById(R.id.subirImagen);

        // Inicializa la referencia de la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Propiedades");
        storageReference = FirebaseStorage.getInstance().getReference("ImagenesPropiedades");

        // Configura la acción del botón Registrar
        registrarCasaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarPropiedad();
                //sendMessage();
            }
        });

        //Configuracion del campo de foto
        imagenPropiedad.setOnClickListener(view -> showImageOptionsDialog());

        // Configuración del campo de amenidades con selección múltiple
        amenidadesCasaInput.setOnClickListener(v -> showAmenitiesDialog(amenidadesCasaInput));

        // Encuentra el botón de regreso en el layout
        ImageView btnBack = findViewById(R.id.botonRegresar);

        // Asigna el evento onClick al botón de regreso
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige a HomePage
                startActivity(new Intent(RegistroPropiedad.this, Historial.class));
                finish(); // Finaliza la actividad actual
            }
        });
    }

    //subir foto
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    // Mostrar opciones de "Tomar Foto" o "Seleccionar desde Galería"
    private void showImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción");
        String[] options = {"Tomar Foto", "Seleccionar desde Galería"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Tomar foto
                if (ContextCompat.checkSelfPermission(RegistroPropiedad.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegistroPropiedad.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                } else {
                    openCamera();
                }
            } else if (which == 1) {
                // Seleccionar desde galería
                openGallery();
            }
        });
        builder.show();
    }

    // Abrir cámara
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Abrir galería
    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imagenPropiedad.setImageBitmap(imageBitmap);

                // Convierte el Bitmap en URI
                imageUri = getImageUriFromBitmap(imageBitmap);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                imageUri = data.getData();
                imagenPropiedad.setImageURI(imageUri); // Muestra la imagen seleccionada
            }
        }
    }

    // Convierte el Bitmap en una URI temporal para subirlo
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Foto", null);
        return Uri.parse(path);
    }

    private void subirImagenYRegistrarPropiedad(Propiedad propiedad) {
        if (imageUri != null) {
            // Crear referencia en Firebase Storage con un nombre único
            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");

            // Subir la imagen a Firebase Storage
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Obtener la URL de descarga
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    propiedad.setImagenUrl(uri.toString());  // Guardar URL en la propiedad
                    registrarPropiedadEnDatabase(propiedad); // Guardar la propiedad en la DB
                }).addOnFailureListener(e ->
                        Toast.makeText(this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show()
                );
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            );
        } else {
            Toast.makeText(this, "Por favor selecciona una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void registrarPropiedadEnDatabase(Propiedad propiedad) {
        String propiedadId = databaseReference.push().getKey();  // Genera un ID único
        databaseReference.child(propiedadId).setValue(propiedad).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegistroPropiedad.this, "Propiedad registrada exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            } else {
                Toast.makeText(RegistroPropiedad.this, "Error al registrar la propiedad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarPropiedad() {
        String nombrePropiedad = nombrePropiedadInput.getText().toString().trim();
        String cantidadPersonasStr = cantidadPersonasInput.getText().toString().trim();
        String cantidadHabitacionesStr = cantidadHabitacionesInput.getText().toString().trim();
        String precio = precioInput.getText().toString().trim();
        String ubicacion = ubicacionInput.getText().toString().trim();
        String amenidadesCasa = amenidadesCasaInput.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (TextUtils.isEmpty(nombrePropiedad) || TextUtils.isEmpty(cantidadPersonasStr) ||
                TextUtils.isEmpty(cantidadHabitacionesStr) || TextUtils.isEmpty(precio) ||
                TextUtils.isEmpty(ubicacion) || TextUtils.isEmpty(amenidadesCasa)) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las cantidades sean números enteros
        int cantidadPersonas, cantidadHabitaciones;
        try {
            cantidadPersonas = Integer.parseInt(cantidadPersonasStr);
            cantidadHabitaciones = Integer.parseInt(cantidadHabitacionesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad de personas y habitaciones deben ser números enteros", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que el nombre no contenga caracteres prohibidos en Firebase
        if (nombrePropiedad.contains(".") || nombrePropiedad.contains("#") ||
                nombrePropiedad.contains("$") || nombrePropiedad.contains("[") ||
                nombrePropiedad.contains("]")) {
            Toast.makeText(this, "El nombre de la propiedad no puede contener . # $ [ ]", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear un objeto de propiedad temporal sin la URL de la imagen
        Propiedad propiedad = new Propiedad(nombrePropiedad, precio, ubicacion, amenidadesCasa, cantidadPersonas, cantidadHabitaciones, null);
        try{
            subirImagenYRegistrarPropiedad(propiedad);
            if (imageUri != null){
                Toast.makeText(RegistroPropiedad.this, "Propiedad registrada exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }
        }catch (Exception e){
            Toast.makeText(RegistroPropiedad.this, "Error al registrar la propiedad"+e, Toast.LENGTH_SHORT).show();
        }


    }

    private void limpiarCampos() {
        nombrePropiedadInput.setText("");
        cantidadPersonasInput.setText("");
        cantidadHabitacionesInput.setText("");
        precioInput.setText("");
        ubicacionInput.setText("");
        amenidadesCasaInput.setText("");
    }

    // Método para mostrar el cuadro de seleccion múltiple para amenidades
    private void showAmenitiesDialog(TextInputEditText amenidadesEditText) {
        String[] amenidadesArray = {"Wifi", "Aire acondicionado", "Calefaccion", "Cocina equipada", "Parrilla", "Jacuzzi",
                "Caja de Seguridad", "Lavadora", "Secadora", "Secadora de pelo", "Gimnasio",
                "Estacionamiento", "TV", "Patio", "Terraza", "Piscina", "Chimenea", "Sala de cine", "Sala de juegos", "Mesa de billar",
                "Mesa de ping-pong", "Bar", "Zona para fogata", "Jardin", "Acceso a la playa"};
        boolean[] seleccionados = new boolean[amenidadesArray.length];
        ArrayList<String> amenidadesSeleccionadas = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona las amenidades");

        // Crear un ListView y configurarlo para permitir selección múltiple
        ListView listView = new ListView(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, amenidadesArray);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            seleccionados[position] = !seleccionados[position];
            if (seleccionados[position]) {
                amenidadesSeleccionadas.add(amenidadesArray[position]);
            } else {
                amenidadesSeleccionadas.remove(amenidadesArray[position]);
            }
        });

        // Crea un ScrollView que contenga un LinearLayout, dentro del cual está el ListView
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        scrollView.addView(layout);

        // Limitar la altura del ListView dentro del ScrollView
        listView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1500 // Altura máxima del ListView para permitir desplazamiento en la vista completa
        ));

        builder.setView(scrollView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            amenidadesEditText.setText(TextUtils.join(", ", amenidadesSeleccionadas));
        });

        builder.setNegativeButton("Cancelar", null);


        // Crear el diálogo y ajustar su tamaño
        AlertDialog dialog = builder.create();
        dialog.show();

        // Ajusta el tamaño del diálogo
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 1900);
    }
}