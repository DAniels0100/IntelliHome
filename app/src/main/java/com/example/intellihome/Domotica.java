package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Domotica extends AppCompatActivity {

    private static final String SERVER_IP = "192.168.0.237"; // IP del servidor intermedio
    private static final int SERVER_PORT = 8888; // Puerto configurado en el servidor para los Sockets
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domotica);

        Button buttonLed1 = findViewById(R.id.buttonLed1);
        Button buttonLed2 = findViewById(R.id.buttonLed2);
        Button buttonLed3 = findViewById(R.id.buttonLed3);
        Button buttonLed4 = findViewById(R.id.buttonLed4);

        // Configurar los botones para enviar comandos al Arduino a través del servidor
        buttonLed1.setOnClickListener(v -> toggleLed(buttonLed1, "1", "2"));
        buttonLed2.setOnClickListener(v -> toggleLed(buttonLed2, "3", "4"));
        buttonLed3.setOnClickListener(v -> toggleLed(buttonLed3, "5", "6"));
        buttonLed4.setOnClickListener(v -> toggleLed(buttonLed4, "7", "8"));

        // Encuentra el botón de regreso en el layout
        ImageView btnBack = findViewById(R.id.botonRegresar);
        btnBack.setOnClickListener(v -> {
            // Redirige a HomePage
            Intent intent = new Intent(Domotica.this, HomePage.class);
            startActivity(intent);
            finish(); // Finaliza la actividad actual
        });
    }

    private void toggleLed(Button button, String commandOn, String commandOff) {
        // Determinar el estado actual del botón
        boolean isOn = button.getText().toString().contains("ON");
        String newCommand = isOn ? commandOff : commandOn;

        // Cambiar el texto y el color del botón
        button.setText(isOn ? button.getText().toString().replace("ON", "OFF") : button.getText().toString().replace("OFF", "ON"));
        button.setBackgroundColor(isOn ? getColor(android.R.color.holo_red_dark) : getColor(android.R.color.holo_green_dark));

        // Enviar el comando al Arduino mediante el servidor intermedio
        sendCommandToServer(newCommand);
    }

    private void sendCommandToServer(String command) {
        executorService.execute(() -> {
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(command);
            } catch (IOException e) {
                Log.e("Domotica", "Error al enviar el comando al servidor", e);
            }
        });
    }
}

