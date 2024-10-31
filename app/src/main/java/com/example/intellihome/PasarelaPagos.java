
package com.example.intellihome;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

public class PasarelaPagos extends AppCompatActivity {

    Button stripeButton;
    EditText amountEditText;
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret, amount;
    PaymentSheet.CustomerConfiguration customerConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasarela_pagos);
        stripeButton = findViewById(R.id.stripeButton);
        amountEditText = findViewById(R.id.amountEditText);

        stripeButton.setOnClickListener(view ->{
            if (TextUtils.isEmpty(amountEditText.getText()
                    .toString())){
                Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                amount = amountEditText.getText().toString() + "00";
                getDetails();
            }
        });

        // Encuentra el botón de regreso en el layout
        ImageView btnBack = findViewById(R.id.botonRegresar);

        // Asigna el evento onClick al botón de regreso
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirige a HomePage
                Intent intent = new Intent(PasarelaPagos.this, HomePage.class);
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            }
        });

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

    }

    void getDetails(){
        Fuel.INSTANCE.post("https://stripepayment-owmtflr4ia-uc.a.run.app?amt="
                + amount, null).responseString(new Handler<String>() {
            @Override
            public void success(String s) {
                try {
                    JSONObject result = new JSONObject(s);
                    customerConfig = new PaymentSheet.CustomerConfiguration(
                            result.getString("customer"),
                            result.getString("ephemeralKey")
                    );
                    paymentIntentClientSecret = result.getString("paymentIntent");
                    PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showStripePaymentSheet();
                        }
                    });



                } catch (JSONException e){
                    Toast.makeText(PasarelaPagos.this, e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void failure(@NonNull FuelError fuelError) {

            }
        });
    }

    void showStripePaymentSheet(){
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("coDeR")
                .customer(customerConfig)
                .allowsDelayedPaymentMethods(true)
                .build();
        paymentSheet.presentWithPaymentIntent(
                paymentIntentClientSecret,
                configuration
        );

    }

    void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult){

        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, ((PaymentSheetResult.Failed) paymentSheetResult).getError().toString() , Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
        }

    }
}
