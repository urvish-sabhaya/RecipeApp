package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.USERS;
import static com.example.recipeapp.utils.FireStoreConstants.USER_SECURITY;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends BaseActivity {

    ImageView back_img;
    EditText edt_old_password, edt_new_password, edt_confirm_password;
    Button change_password_btn;
    FirebaseFirestore db;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();

        fetchUserDetails();
    }

    private void initViews() {
        db = FirebaseFirestore.getInstance();

        back_img = findViewById(R.id.back_img);
        edt_old_password = findViewById(R.id.edt_old_password);
        edt_new_password = findViewById(R.id.edt_new_password);
        edt_confirm_password = findViewById(R.id.edt_confirm_password);
        change_password_btn = findViewById(R.id.change_password_btn);

        back_img.setOnClickListener(view -> {
            finish();
        });
        change_password_btn.setOnClickListener(view -> {
            validateAndChangePass();
        });
    }

    private void fetchUserDetails() {
        // TODO: 13-08-2022 make email to dynamic
        showProgressDialog();
        db.collection(USERS)
                .document("urvishsabhaya@gmail.com")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentUser = documentSnapshot.toObject(User.class);
                    hideProgressDialog();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                });
    }

    private void validateAndChangePass() {
        if (currentUser == null) {
            Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldPass = edt_old_password.getText().toString();
        String newPass = edt_new_password.getText().toString();
        String confirmPass = edt_confirm_password.getText().toString();

        if (TextUtils.isEmpty(oldPass)) {
            Toast.makeText(this, "Please enter old password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPass)) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Please enter confirm new password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "New password and confirm password not matching", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!decodeBase64(currentUser.getUser_security()).equals(oldPass)) {
            Toast.makeText(this, "Your old password is wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        updateNewPassword(confirmPass);
    }

    private void updateNewPassword(String confirmPass) {
        showProgressDialog();
        String encoded = encodeBase64(confirmPass);
        DocumentReference documentReference = db.collection(USERS).document(currentUser.getUser_email());
        documentReference
                .update(USER_SECURITY, encoded)
                .addOnSuccessListener(aVoid -> {
                    currentUser.setUser_security(encoded);
                    Toast.makeText(ChangePasswordActivity.this, "Your password has been changed successfully", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    edt_old_password.setText("");
                    edt_new_password.setText("");
                    edt_confirm_password.setText("");
                })
                .addOnFailureListener(e -> {
                    Log.e("failureListener", e.toString());
                    Toast.makeText(ChangePasswordActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                });
    }
}