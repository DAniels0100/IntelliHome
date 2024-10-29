package com.example.intellihome;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import java.io.IOException;
import java.util.List;

public class Domotica extends AppCompatActivity {

    private UsbSerialPort serialPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domotica);

        Button buttonLed1 = findViewById(R.id.buttonLed1);
        Button buttonLed2 = findViewById(R.id.buttonLed2);
        Button buttonLed3 = findViewById(R.id.buttonLed3);
        Button buttonLed4 = findViewById(R.id.buttonLed4);

        // Configurar la conexión USB con el Arduino
        UsbManager manager = (UsbManager) getSystemService(USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (!availableDrivers.isEmpty()) {
            UsbSerialDriver driver = availableDrivers.get(0);
            serialPort = driver.getPorts().get(0);
        }

        // Configurar los botones para enviar comandos al Arduino
        buttonLed1.setOnClickListener(v -> toggleLed(buttonLed1, "LuzDormitorio1"));
        buttonLed2.setOnClickListener(v -> toggleLed(buttonLed2, "LuzDormitorio2"));
        buttonLed3.setOnClickListener(v -> toggleLed(buttonLed3, "LuzBaño"));
        buttonLed4.setOnClickListener(v -> toggleLed(buttonLed4, "LuzCocina"));

        // Encuentra el botón de regreso en el layout
        ImageView btnBack = findViewById(R.id.botonRegresar);
        btnBack.setOnClickListener(v -> {
            // Redirige a HomePage
            Intent intent = new Intent(Domotica.this, HomePage.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });
    }

    private void toggleLed(Button button, String ledCommand) {
        // Determinar el estado actual del botón
        boolean isOn = button.getText().toString().contains("ON");
        String newCommand = ledCommand + (isOn ? " OFF" : " ON");

        // Cambiar el texto y el color del botón
        button.setText(isOn ? ledCommand + " OFF" : ledCommand + " ON");
        button.setBackgroundColor(isOn ? getColor(android.R.color.holo_red_dark) : getColor(android.R.color.holo_green_dark));

        // Enviar el comando al Arduino
        sendCommandToArduino(newCommand);
    }

    private void sendCommandToArduino(String command) {
        if (serialPort != null) {
            UsbManager usbManager = (UsbManager) getSystemService(USB_SERVICE);
            UsbDevice device = serialPort.getDriver().getDevice();
            UsbDeviceConnection connection = usbManager.openDevice(device);

            if (connection == null) {
                // No se pudo abrir la conexión USB
                Log.e("Domotica", "No se pudo abrir la conexión USB");
                return;
            }

            try {
                serialPort.open(connection);
                serialPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                serialPort.write(command.getBytes(), 1000);
            } catch (IOException e) {
                Log.e("Domotica", "Error al comunicarse con el Arduino", e);
            } finally {
                try {
                    serialPort.close();
                } catch (IOException e) {
                    Log.e("Domotica", "Error al cerrar el puerto", e);
                }
            }
        }
    }
}
