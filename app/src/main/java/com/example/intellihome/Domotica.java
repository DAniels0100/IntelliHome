package com.example.intellihome;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

public class Domotica extends AppCompatActivity {

    private UsbSerialPort serialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domotica);

        // Botón de regreso
        ImageView btnBack = findViewById(R.id.botonRegresar);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(Domotica.this, HomePage.class));
            finish();
        });

        // Configuración de navegación del menú
        try {
            ImageView perfilMenu = findViewById(R.id.perfilmenu);
            ImageView domoticaMenu = findViewById(R.id.domotica);
            ImageView historialMenu = findViewById(R.id.historial);
            ImageView busquedaMenu = findViewById(R.id.busquedamenu);

            perfilMenu.setOnClickListener(view -> {
                startActivity(new Intent(Domotica.this, Perfil.class));
                finish();
            });

            domoticaMenu.setOnClickListener(view -> {
                startActivity(new Intent(Domotica.this, Domotica.class));
                finish();
            });

            historialMenu.setOnClickListener(view -> {
                startActivity(new Intent(Domotica.this, RegistroPropiedad.class));
                finish();
            });

            busquedaMenu.setOnClickListener(view -> {
                startActivity(new Intent(Domotica.this, Busqueda.class));
                finish();
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Inicializar conexión USB
        UsbManager manager = (UsbManager) getSystemService(USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (!availableDrivers.isEmpty()) {
            UsbSerialDriver driver = availableDrivers.get(0);
            serialPort = driver.getPorts().get(0);
        }

        // Configuración de los ImageView de los bombillos
        ImageView bombillo1 = findViewById(R.id.bombillodor1);
        ImageView bombillo2 = findViewById(R.id.bombillodor2);
        ImageView bombillo3 = findViewById(R.id.bombillococina);
        ImageView bombillo4 = findViewById(R.id.bombillobaño1);

        bombillo1.setOnClickListener(v -> toggleBombillo(bombillo1, "LuzDormitorio1"));
        bombillo2.setOnClickListener(v -> toggleBombillo(bombillo2, "LuzDormitorio2"));
        bombillo3.setOnClickListener(v -> toggleBombillo(bombillo3, "LuzCocina"));
        bombillo4.setOnClickListener(v -> toggleBombillo(bombillo4, "LuzBaño"));

        // Configuración del portón con huella
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(Domotica.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(Domotica.this, "Autenticación exitosa, portón abierto", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Domotica.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación requerida")
                .setSubtitle("Coloca tu huella dactilar para abrir el portón")
                .setNegativeButtonText("Cancelar")
                .build();

        ImageView porton = findViewById(R.id.porton);
        porton.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));

        // Sensor de humedad
        ImageView sensorHumedad = findViewById(R.id.sensorHumedad);
        sensorHumedad.setOnClickListener(v -> toggleSensor(sensorHumedad, "humedad"));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toggleBombillo(ImageView bombillo, String ledCommand) {
        boolean isOn;
        if (Objects.equals(bombillo.getDrawable().getConstantState(), getResources().getDrawable(R.drawable.do_bombap).getConstantState())) {
            bombillo.setImageResource(R.drawable.do_bombpren);  // Bombillo encendido
            isOn = true;
        } else {
            bombillo.setImageResource(R.drawable.do_bombap);  // Bombillo apagado
            isOn = false;
        }

        // Enviar comando al Arduino
        sendCommandToArduino(ledCommand + (isOn ? " ON" : " OFF"));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toggleSensor(ImageView sensor, String tipo) {
        if (tipo.equals("humedad")) {
            if (Objects.equals(sensor.getDrawable().getConstantState(), getResources().getDrawable(R.drawable.do_senshumap).getConstantState())) {
                sensor.setImageResource(R.drawable.do_senshumpren);  // Sensor de humedad encendido
            } else {
                sensor.setImageResource(R.drawable.do_senshumap);  // Sensor de humedad apagado
            }
        }
    }

    private void sendCommandToArduino(String command) {
        if (serialPort != null) {
            UsbManager usbManager = (UsbManager) getSystemService(USB_SERVICE);
            UsbDevice device = serialPort.getDriver().getDevice();
            UsbDeviceConnection connection = usbManager.openDevice(device);

            if (connection == null) {
                return;
            }

            try {
                serialPort.open(connection);
                serialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                serialPort.write(command.getBytes(), 1000);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    serialPort.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
