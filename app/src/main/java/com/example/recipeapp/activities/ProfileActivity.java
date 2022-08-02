package com.example.recipeapp.activities;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.recipeapp.R;

public class ProfileActivity extends BaseActivity {

    ImageView back_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back_img = findViewById(R.id.back_img);

        back_img.setOnClickListener(view -> {
            finish();
        });
    }
}