package com.example.recipeapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recipeapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends BaseActivity {

    ImageView back_img;
    EditText textName;
    TextView textEmail;
    Button edit,save;
    FirebaseFirestore dbroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back_img = findViewById(R.id.back_img);
        textEmail = findViewById(R.id.textEmail);
        textName = findViewById(R.id.textName);
        edit = findViewById(R.id.edit);
        save = findViewById(R.id.save);
        dbroot = FirebaseFirestore.getInstance();

        back_img.setOnClickListener(view -> {
            finish();

            displayUserData();



        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

    }


    public void displayUserData(){




    }

}