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
import java.util.concurrent.Executor;

public class Domotica extends AppCompatActivity {

    private UsbSerialPort serialPort;
    private boolean isPortonAbierto = false; // Estado inicial de la puerta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domotica);

        // Inicializar conexión USB
        UsbManager manager = (UsbManager) getSystemService(USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (!availableDrivers.isEmpty()) {
            UsbSerialDriver driver = availableDrivers.get(0);
            serialPort = driver.getPorts().get(0);
        }

        // Configuración del portón con autenticación biométrica
        Executor executor = ContextCompat.getMainExecutor(this);
        BiometricPrompt biometricPrompt = new BiometricPrompt(Domotica.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(Domotica.this, "Autenticación exitosa, abriendo puerta", Toast.LENGTH_SHORT).show();
                sendCommandToArduino("ABRIR_PUERTA");
                isPortonAbierto = true; // Actualizar estado de la puerta
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(Domotica.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación requerida")
                .setSubtitle("Coloca tu huella dactilar para abrir la puerta")
                .setNegativeButtonText("Cancelar")
                .build();

        // Botón para abrir/cerrar la puerta
        ImageView porton = findViewById(R.id.porton);
        porton.setOnClickListener(v -> {
            if (isPortonAbierto) {
                // Si la puerta ya está abierta, se cierra sin autenticación
                Toast.makeText(Domotica.this, "Cerrando puerta", Toast.LENGTH_SHORT).show();
                sendCommandToArduino("CERRAR_PUERTA");
                isPortonAbierto = false; // Actualizar estado de la puerta
            } else {
                // Si la puerta está cerrada, se solicita autenticación para abrirla
                biometricPrompt.authenticate(promptInfo);
            }
        });
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
        } else {
            Toast.makeText(this, "No hay conexión con el Arduino", Toast.LENGTH_SHORT).show();
        }
    }
}

