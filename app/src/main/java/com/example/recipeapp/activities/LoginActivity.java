package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends BaseActivity {

    //Buttons Login and Signup
    Button btnSignUp, btnUserLogin;
    EditText userEmail, userPassword;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Login buttons with id
        btnUserLogin = findViewById(R.id.btnUserLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(i);
        });

        btnUserLogin.setOnClickListener(view -> {
            if (TextUtils.isEmpty(userEmail.getText().toString())) {
                userEmail.setError("Kindly enter a valid email address");
                return;
            }
            if (TextUtils.isEmpty(userPassword.getText().toString()) ||
                    userPassword.getText().toString().length() <= 6) {
                userPassword.setError("Fill at least 6 character");
                return;
            }

            fetchData();
        });
    }

    public void fetchData() {
        showProgressDialog();

        //Getting username from the text box and getting the document id from database
        String userDoc = userEmail.getText().toString();
        String checkUserPass = userPassword.getText().toString();

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
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(), "You are not registered. Please register", Toast.LENGTH_SHORT).show();
                });
    }
}