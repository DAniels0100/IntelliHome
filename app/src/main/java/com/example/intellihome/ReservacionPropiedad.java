package com.example.intellihome;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.result.Result;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ReservacionPropiedad extends AppCompatActivity {

    Button stripeButton;
    TextView montoPagar;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret, amount;
    PaymentSheet.CustomerConfiguration customerConfig;

    Button startDateButton, endDateButton;
    TextView selectedStartDate, selectedEndDate;
    Calendar startDate, endDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservacion_propiedad);
        stripeButton = findViewById(R.id.stripeButton);
        montoPagar = findViewById(R.id.montoPagarTxt);

        // Botón de Stripe
        stripeButton.setOnClickListener(view -> {
            if (!isDateSelectionComplete()) {
                Toast.makeText(this, "Por favor, seleccionar las fechas para realizar la reservacion", Toast.LENGTH_SHORT).show();
            } else {
                amount = montoPagar.getText().toString() + "00";
                getDetails();

                // Llamada a Fuel.post con lista de parámetros vacía
                Fuel.INSTANCE.post("http://127.0.0.1:5000/send-whatsapp", new ArrayList<>())
                        .responseString((request, response, result) -> {
                            result.fold(
                                    success -> {
                                        runOnUiThread(() ->
                                                Toast.makeText(ReservacionPropiedad.this, "Mensaje de reservación enviado", Toast.LENGTH_SHORT).show()
                                        );
                                        return null; // Ensure there’s a return statement here
                                    },
                                    failure -> {
                                        runOnUiThread(() ->
                                                Toast.makeText(ReservacionPropiedad.this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                                        );
                                        return null; // Ensure there’s a return statement here as well
                                    }
                            );
                            return null; // Needed to satisfy lambda return type for `responseString`
                        });
            }
        });


        // Seleccionar las fechas de la reservacion
        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);
        selectedStartDate = findViewById(R.id.selectedStartDate);
        selectedEndDate = findViewById(R.id.selectedEndDate);

        // Configura el botón para seleccionar la fecha de inicio
        startDateButton.setOnClickListener(v -> showDatePickerDialog(true));

        // Configura el botón para seleccionar la fecha de fin
        endDateButton.setOnClickListener(v -> showDatePickerDialog(false));

        // Botón de regreso
        ImageView btnBack = findViewById(R.id.botonRegresar);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ReservacionPropiedad.this, HomePage.class);
            startActivity(intent);
            finish();
        });

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;

            // Crear un calendario para la fecha seleccionada
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(selectedYear, selectedMonth, selectedDay);

            if (isStartDate) {
                startDate = selectedDate;
                selectedStartDate.setText("Fecha de inicio: " + date);
                endDate = null;
                selectedEndDate.setText("Fecha de fin: ");
                montoPagar.setText("0");
            } else {
                if (startDate != null && selectedDate.before(startDate)) {
                    Toast.makeText(this, "La fecha de fin debe ser posterior a la fecha de inicio", Toast.LENGTH_SHORT).show();
                } else {
                    endDate = selectedDate;
                    selectedEndDate.setText("Fecha de fin: " + date);

                    // Si ambas fechas están seleccionadas, calcular y mostrar el monto total
                    if (isDateSelectionComplete()) {
                        int cantidadDias = calculateDaysBetween(startDate, endDate);
                        int montoTotal = calcularMontoTotal(cantidadDias, Integer.parseInt(getIntent().getStringExtra("montoPorPagar")));
                        montoPagar.setText(String.valueOf(montoTotal));
                    }
                }
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    // Método para calcular los días entre dos fechas
    private int calculateDaysBetween(Calendar startDate, Calendar endDate) {
        long startMillis = startDate.getTimeInMillis();
        long endMillis = endDate.getTimeInMillis();
        long diffMillis = endMillis - startMillis;
        return (int) (diffMillis / (1000 * 60 * 60 * 24)); // Convertir de milisegundos a días
    }

    private int calcularMontoTotal(int cantidadDias, int montoNoche) {
        return cantidadDias * montoNoche;
    }

    private boolean isDateSelectionComplete() {
        return startDate != null && endDate != null;
    }

    void getDetails() {
        Fuel.INSTANCE.post("https://stripepayment-owmtflr4ia-uc.a.run.app?amt=" + amount, null).responseString(new Handler<String>() {
            @Override
            public void success(String s) {
                try {
                    JSONObject result = new JSONObject(s);
                    customerConfig = new PaymentSheet.CustomerConfiguration(result.getString("customer"), result.getString("ephemeralKey"));
                    paymentIntentClientSecret = result.getString("paymentIntent");
                    PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));
                    runOnUiThread(() -> showStripePaymentSheet());
                } catch (JSONException e) {
                    Toast.makeText(ReservacionPropiedad.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(@NonNull FuelError fuelError) {
                // Handle failure
            }
        });
    }

    void showStripePaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("coDeR")
                .customer(customerConfig)
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, ((PaymentSheetResult.Failed) paymentSheetResult).getError().toString(), Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
        }
    }
}
