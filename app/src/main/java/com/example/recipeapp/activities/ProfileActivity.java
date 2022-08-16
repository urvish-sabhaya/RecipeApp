package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.USERS;
import static com.example.recipeapp.utils.FireStoreConstants.USER_IMAGE;
import static com.example.recipeapp.utils.FireStoreConstants.USER_NAME;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends BaseActivity {

    ImageView back_img;
    EditText edt_user_name, edt_user_email;
    RelativeLayout profile_image_rel;
    Button update_profile_btn;
    FirebaseFirestore db;
    CircleImageView profile_image;
    User user;
    Bitmap selectedProfileImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();

        back_img.setOnClickListener(view -> {
            finish();
        });

        update_profile_btn.setOnClickListener(view -> {
            updateProfile();
        });

        profile_image_rel.setOnClickListener(view -> {
            if (checkAndRequestPermissions(this)) {
                chooseImage(this);
            }
        });
    }

    private void updateProfile() {
        String userName = edt_user_name.getText().toString();
        String userImage = "";

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> fieldsToUpdateMap = new HashMap<>();
        fieldsToUpdateMap.put(USER_NAME, userName);

        if (selectedProfileImage != null) {
            userImage = generateBitmapToBase64(selectedProfileImage);
            fieldsToUpdateMap.put(USER_IMAGE, userImage);
        }

        showProgressDialog();
        DocumentReference documentReference = db.collection(USERS).document(user.getUser_email());
        String finalUserImage = userImage;
        documentReference
                .update(fieldsToUpdateMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Your profile has been updated successfully", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    user.setUser_name(userName);
                    if (!TextUtils.isEmpty(finalUserImage)) {
                        user.setUser_image(finalUserImage);
                    }
                    appSharedPreference.setUserInfo(user);
                })
                .addOnFailureListener(e -> {
                    Log.e("failureListener", e.toString());
                    Toast.makeText(ProfileActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                });
    }

    private void initViews() {
        db = FirebaseFirestore.getInstance();

        user = appSharedPreference.getUserInfo();

        back_img = findViewById(R.id.back_img);
        edt_user_name = findViewById(R.id.edt_user_name);
        edt_user_email = findViewById(R.id.edt_user_email);
        profile_image_rel = findViewById(R.id.profile_image_rel);
        update_profile_btn = findViewById(R.id.update_profile_btn);
        profile_image = findViewById(R.id.profile_image);

        setUserDetails();
    }

    private void setUserDetails() {
        if (!TextUtils.isEmpty(user.getUser_image())) {
            Glide.with(this)
                    .load(Base64.decode(user.getUser_image(), Base64.DEFAULT))
                    .into(profile_image);
        }
        edt_user_email.setText(user.getUser_email());
        edt_user_name.setText(user.getUser_name());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        setImage(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap
                                        = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(),
                                        selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (selectedImageBitmap != null) {
                                setImage(selectedImageBitmap);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void setImage(Bitmap selectedImage) {
        profile_image.setImageBitmap(selectedImage);
        selectedProfileImage = selectedImage;
    }
}