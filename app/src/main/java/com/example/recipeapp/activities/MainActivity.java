package com.example.recipeapp.activities;

import android.os.Bundle;

import com.example.recipeapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends BaseActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        showProgressDialog();

        hideProgressDialog();
    }
}