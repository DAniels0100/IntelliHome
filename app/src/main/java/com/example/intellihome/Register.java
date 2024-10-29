package com.example.intellihome;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Register extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private ImageView subirImagen;

    // Lista de palabras prohibidas
    private static final String[] PROHIBITED_WORDS = {
            "sexo", "muerte", "puta", "estúpido", "estúpida", "mierda", "pene", "vagina",
            "cabrón", "tonto", "idiota", "coño", "maldito", "imbecil", "bastardo", "nazi",
            "estupido", "estupida", "imbécil"
    };

    //crear objeto de la DataBaseReference para acceder a la base de datos de FireBase realtime database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://intellihome-293ec-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText nombre = findViewById(R.id.nombreInput);
        final EditText apellido = findViewById(R.id.apellidoInput);
        final EditText nickName = findViewById(R.id.nicknameInput);
        final EditText numeroTelefono = findViewById(R.id.numeroTelefonoInput);
        final EditText email = findViewById(R.id.emailInput);
        final EditText domicilio = findViewById(R.id.domicilioInput);
        final EditText contrasena = findViewById(R.id.contrasenaInput);
        final EditText contrasenaVerificacion = findViewById(R.id.contrasenaVerificacionInput);
        final Button registroBtn = findViewById(R.id.registrarseBtn);
        final RadioButton aceptoRadioButton = findViewById(R.id.acepto);

        // Configuración del campo de edad con DatePicker
        EditText edad = findViewById(R.id.edadInput);
        edad.setOnClickListener(v -> showDatePickerDialog(edad));

        // Configuración del campo de pasatiempos con selección múltiple
        TextInputEditText pasatiempos= findViewById(R.id.pasatiemposInput);
        pasatiempos.setOnClickListener(v -> showHobbiesDialog(pasatiempos));

        // Configuración del campo de preferencias de casa con selección múltiple
        TextInputEditText preferenciasCasa = findViewById(R.id.preferenciasCasaInput);
        preferenciasCasa.setOnClickListener(v -> showHousePreferencesDialog(preferenciasCasa));


        registroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //recuperar la data del usuario
                final String nombreTxt = nombre.getText().toString();
                final String apellidoTxt = apellido.getText().toString();
                final String nickNameTxt = nickName.getText().toString();
                final String numeroTelefonoTxt = numeroTelefono.getText().toString();
                final String emailTxt = email.getText().toString();
                final String domicilioTxt = domicilio.getText().toString();
                final String contrasenaTxt = contrasena.getText().toString();
                final String contrasenaVerificacionTxt = contrasenaVerificacion.getText().toString();
                final String edadTxt = edad.getText().toString();

                //verificar que se llenaron todas las casillas
                if(nombreTxt.isEmpty() || apellidoTxt.isEmpty() || numeroTelefonoTxt.isEmpty() ||
                        emailTxt.isEmpty() || contrasenaTxt.isEmpty() || contrasenaVerificacionTxt.isEmpty() ||
                        edadTxt.isEmpty()){
                    Toast.makeText(Register.this,"Se necesitan llenar todas las casillas", Toast.LENGTH_SHORT).show();
                }else if (containsProhibitedWords(nickNameTxt)) {
                    nickName.setError("El nickname contiene palabras prohibidas");
                    return;
                }
                //verificar que las contrasenas respeten los criterios
                else if (contrasenaValida(contrasenaTxt)==false) {
                    contrasena.setError("Se requiere 8 caracteres, una mayúscula y una minúscula en la contraseña");
                }
                //Verificar que las 2 contrasenas coincidan
                else if (!contrasenaTxt.equals(contrasenaVerificacionTxt)) {
                    contrasenaVerificacion.setError("Las contraseñas no coinciden");
                    return;
                }
                else if (!terminosAceptados()) {
                    // El usuario aceptó los términos, continuar con el proceso
                    // Mostrar un mensaje indicando que debe aceptar los términos
                    aceptoRadioButton.setError("El nickname contiene palabras prohibidas");
                }
                else if (!telefonoCorrecto(numeroTelefonoTxt)) {
                    numeroTelefono.setError("El numero debe contener 8 digitos");
                }
                else if (!edadCorrecta(edadTxt)) {
                    edad.setError("Se necesita una edad superior o igual a 18");
                }
                //se completo el registro exitosamente
                else{

                    databaseReference.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String emailFormatted = emailTxt.replace(".", ",");
                            //verificar que no exista el usuario
                            if (snapshot.hasChild(nickNameTxt)) {
                                nickName.setError("El nickname ya existe");
                            }if (snapshot.hasChild(emailFormatted)){
                                    email.setError("El correo ya existe");
                            }else{
                                //se usa como identificador/raiz el nickname y correo del usuario
                                //se envia la data a la base de datos
                                databaseReference.child("usuarios").child(nickNameTxt).child("nombre").setValue(nombreTxt);
                                databaseReference.child("usuarios").child(nickNameTxt).child("apellido").setValue(apellidoTxt);
                                databaseReference.child("usuarios").child(nickNameTxt).child("numeroTelefono").setValue(numeroTelefonoTxt);
                                databaseReference.child("usuarios").child(nickNameTxt).child("email").setValue(emailTxt);
                                databaseReference.child("usuarios").child(nickNameTxt).child("contrasena").setValue(contrasenaTxt);
                                databaseReference.child("usuarios").child(nickNameTxt).child("edad").setValue(edadTxt);
                                databaseReference.child("usuarios").child(nickNameTxt).child("domicilio").setValue(domicilioTxt);
                                // Guardar por correo
                                databaseReference.child("usuarios").child(emailFormatted).child("nombre").setValue(nombreTxt);
                                databaseReference.child("usuarios").child(emailFormatted).child("apellido").setValue(apellidoTxt);
                                databaseReference.child("usuarios").child(emailFormatted).child("numeroTelefono").setValue(numeroTelefonoTxt);
                                databaseReference.child("usuarios").child(emailFormatted).child("nickname").setValue(nickNameTxt);  // Incluir el nickname aquí
                                databaseReference.child("usuarios").child(emailFormatted).child("contrasena").setValue(contrasenaTxt);
                                databaseReference.child("usuarios").child(emailFormatted).child("edad").setValue(edadTxt);
                                databaseReference.child("usuarios").child(emailFormatted).child("domicilio").setValue(domicilioTxt);
                                //mostrar mensaje de que se logro guardar la data en la base de datos
                                Toast.makeText(Register.this, "Se ha registrado de manera exitosa", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Custom.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
        // Configuración para la subida de imagen
        subirImagen = findViewById(R.id.subirImagen);
        subirImagen.setOnClickListener(view -> showImageOptionsDialog());
    }

    public boolean edadCorrecta(String edad){
        try {
            int numeroEdad = Integer.parseInt(edad);
            return (numeroEdad>=18);
        }catch (NumberFormatException e){
            return false;
        }
    }

    public boolean telefonoCorrecto(String telefono) {
        // Verificar que la longitud sea 8 y que todos los caracteres sean dígitos
        if (telefono.length() == 8 && telefono.matches("\\d{8}")) {
            return true;
        }
        return false;
    }
    
    private boolean terminosAceptados() {
        RadioButton aceptoRadioButton = findViewById(R.id.acepto);

        // Verifica si el RadioButton "Acepto" está marcado
        return aceptoRadioButton.isChecked();
    }

    // Mostrar opciones de "Tomar Foto" o "Seleccionar desde Galería"
    private void showImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción");
        String[] options = {"Tomar Foto", "Seleccionar desde Galería"};
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Tomar foto
                if (ContextCompat.checkSelfPermission(Register.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Register.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
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
                subirImagen.setImageBitmap(imageBitmap); // Mostrar la foto tomada
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri imageUri = data.getData();
                subirImagen.setImageURI(imageUri); // Mostrar la imagen seleccionada de la galería
            }
        }
    }

    // Método para verificar si el nickname contiene palabras prohibidas
    private boolean containsProhibitedWords(String nickname) {
        for (String word : PROHIBITED_WORDS) {
            if (nickname.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    //verificar que la contrasena es valida
    public static boolean contrasenaValida(String password) {

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpecialChar = false;

        // Verificar que la contraseña tenga al menos 8 caracteres
        if (password.length() < 8) {
            return false;
        }

        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecialChar = true;
            }

            // Si ya encontramos las tres condiciones, no necesitamos seguir buscando
            if (hasUpperCase && hasLowerCase && hasSpecialChar) {
                return true;
            }
        }

        // Si alguna condición no se cumple, la contraseña no es válida
        return false;
    }

    // Método para mostrar el DatePicker para la edad
    private void showDatePickerDialog(EditText edadEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                    int age = calculateAge(selectedCalendar.getTimeInMillis());
                    edadEditText.setText(String.valueOf(age));
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Método para calcular la edad
    private int calculateAge(long birthDateInMillis) {
        Calendar now = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTimeInMillis(birthDateInMillis);

        int age = now.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    // Método para mostrar el cuadro de selección múltiple para pasatiempos
    private void showHobbiesDialog(TextInputEditText pasatiemposEditText) {
        String[] pasatiemposArray = {"Leer", "Deportes", "Viajar", "Cine", "Música", "Danza", "Cantar", "Fotografía", "Cine y series", "Manualidades", "Voluntariado", "Cocina", "Videojuegos"};
        boolean[] seleccionados = new boolean[pasatiemposArray.length];
        ArrayList<String> pasatiemposSeleccionados = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tus pasatiempos");

        builder.setMultiChoiceItems(pasatiemposArray, seleccionados, (dialog, which, isChecked) -> {
            if (isChecked) {
                pasatiemposSeleccionados.add(pasatiemposArray[which]);
            } else {
                pasatiemposSeleccionados.remove(pasatiemposArray[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            pasatiemposEditText.setText(android.text.TextUtils.join(", ", pasatiemposSeleccionados));
        });

        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    // Método para mostrar el cuadro de selección múltiple para preferencias de casa
    private void showHousePreferencesDialog(TextInputEditText preferenciasCasaEditText) {
        String[] houseTypes = {"Apartamento", "Casa", "Casa de campo", "Estudio", "Mansión", "Villa", "Penthouse", "Cabaña"};
        boolean[] seleccionados = new boolean[houseTypes.length];
        ArrayList<String> housePreferencesSeleccionados = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tus preferencias de casa");

        builder.setMultiChoiceItems(houseTypes, seleccionados, (dialog, which, isChecked) -> {
            if (isChecked) {
                housePreferencesSeleccionados.add(houseTypes[which]);
            } else {
                housePreferencesSeleccionados.remove(houseTypes[which]);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            preferenciasCasaEditText.setText(TextUtils.join(", ", housePreferencesSeleccionados));
        });

        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

}


