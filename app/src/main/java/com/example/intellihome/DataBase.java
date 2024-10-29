package com.example.intellihome;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DataBase {
    //crear objeto de la DataBaseReference para acceder a la base de datos de FireBase realtime database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://intellihome-293ec-default-rtdb.firebaseio.com/");
}
