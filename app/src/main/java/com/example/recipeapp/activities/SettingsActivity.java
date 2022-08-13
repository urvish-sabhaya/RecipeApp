package com.example.recipeapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.recipeapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends BaseActivity {

    ImageView back_img;
    Button changePassword,btnSave;
    EditText securityQuestion,securityAnswer,newPassword;
    FirebaseFirestore dbroot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        back_img = findViewById(R.id.back_img);

        back_img.setOnClickListener(view -> {
            finish();
        });

        changePassword = findViewById(R.id.btnChangePassword);
        btnSave = findViewById(R.id.btnSave);

        securityQuestion = findViewById(R.id.txtsecurityQuestion);
        securityAnswer = findViewById(R.id.txtsecurityQuestionAnswer);
        newPassword = findViewById(R.id.txtNewPassword);
        dbroot = FirebaseFirestore.getInstance();

        securityQuestion.setVisibility(View.GONE);
        securityAnswer.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);



        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                securityQuestion.setVisibility(View.VISIBLE);
                securityAnswer.setVisibility(View.VISIBLE);
                newPassword.setVisibility(View.VISIBLE);
                btnSave.setVisibility(View.VISIBLE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            changePassword();


            }
        });


    }

    public void changePassword(){
//        String userDoc = userEmail.getText().toString();
//
//        DocumentReference document = dbroot.collection("users").document(userDoc)

    }

}