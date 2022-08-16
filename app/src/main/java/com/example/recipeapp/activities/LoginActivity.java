package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends BaseActivity {

    //Buttons Login and Signup
    Button btnSignUp, login_btn;
    EditText user_email_edt, user_password_edt;
    FirebaseFirestore db;
    TextView forgot_password_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Login buttons with id
        login_btn = findViewById(R.id.login_btn);
        btnSignUp = findViewById(R.id.btnSignUp);
        user_email_edt = findViewById(R.id.user_email_edt);
        user_password_edt = findViewById(R.id.user_password_edt);
        forgot_password_txt = findViewById(R.id.forgot_password_txt);
        db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(view -> {
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        });
        forgot_password_txt.setOnClickListener(view -> {
            Intent i = new Intent(this, ForgotPasswordActivity.class);
            startActivity(i);
        });

        login_btn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(user_email_edt.getText().toString())) {
                user_email_edt.setError("Kindly enter a valid email address");
                return;
            }
            if (TextUtils.isEmpty(user_password_edt.getText().toString())) {
                user_password_edt.setError("Please enter your password");
                return;
            }

            fetchData();
        });
    }

    public void fetchData() {
        showProgressDialog();

        //Getting username from the text box and getting the document id from database
        String userDoc = user_email_edt.getText().toString();
        String checkUserPass = user_password_edt.getText().toString();

        //Setting document reference
        DocumentReference document = db.collection(USERS).document(userDoc);
        document.get().addOnSuccessListener(documentSnapshot -> {
                    hideProgressDialog();

                    if (!documentSnapshot.exists()) {
                        Toast.makeText(getApplicationContext(), "You are not registered. Please register", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User currentUser = documentSnapshot.toObject(User.class);

                    if (!decodeBase64(currentUser.getUser_security()).equals(checkUserPass)) {
                        Toast.makeText(this, "Your password is incorrect", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    appSharedPreference.setUserInfo(currentUser);

                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                })
                .addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(), "You are not registered. Please register", Toast.LENGTH_SHORT).show();
                });
    }
}