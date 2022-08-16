package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.SECURITY_QUESTION;
import static com.example.recipeapp.utils.FireStoreConstants.SECURITY_QUESTION_ANSWER;
import static com.example.recipeapp.utils.FireStoreConstants.USERS;
import static com.example.recipeapp.utils.FireStoreConstants.USER_EMAIL;
import static com.example.recipeapp.utils.FireStoreConstants.USER_NAME;
import static com.example.recipeapp.utils.FireStoreConstants.USER_SECURITY;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity {

    //Button SIgnUp and Login
    Button btnSignUp, btnLogin;

    //Edit text fields
    EditText userName, userEmail, userPassword, securityQuestion, securityAnswer;
    FirebaseFirestore db;
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //register buttons with ids
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPassword = findViewById(R.id.userPassword);
        securityQuestion = findViewById(R.id.securityQuestion);
        securityAnswer = findViewById(R.id.securityAnswer);
        db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(view -> {
            // store the returned value of the dedicated function which checks
            // whether the entered data is valid or if any fields are left blank.
            isAllFieldsChecked = CheckAllFields();
            // the boolean variable turns to be true then
            // only the user must be proceed to the activity2
            if (isAllFieldsChecked) {
                checkUserExist();
            }
        });

        btnLogin.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    public void checkUserExist() {
        showProgressDialog();

        String userID = userEmail.getText().toString();

        //Setting document reference
        DocumentReference document = db.collection(USERS).document(userID);
        document.get().addOnSuccessListener(documentSnapshot -> {
                    hideProgressDialog();

                    if (documentSnapshot.exists()) {
                        userEmail.setText("");
                        Toast.makeText(getApplicationContext(), "User with " + userID + " already exist! Please user other email", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    registerUser();
                })
                .addOnFailureListener(e -> {
                    hideProgressDialog();
                    registerUser();
                });
    }

    public void registerUser() {
        showProgressDialog();

        String userEmailAddress = userEmail.getText().toString();

        Map<String, String> items = new HashMap<>();
        items.put(USER_NAME, userEmailAddress);
        items.put(USER_EMAIL, userEmail.getText().toString());
        items.put(USER_SECURITY, encodeBase64(userPassword.getText().toString()));
        items.put(SECURITY_QUESTION, securityQuestion.getText().toString());
        items.put(SECURITY_QUESTION_ANSWER, securityAnswer.getText().toString());

        db.collection(USERS).document(userEmailAddress).set(items).addOnCompleteListener(task -> {
            hideProgressDialog();

            User currentUser = new User();
            currentUser.setUser_name(userName.getText().toString());
            currentUser.setUser_email(userEmail.getText().toString());
            currentUser.setSecurity_question(securityQuestion.getText().toString());
            currentUser.setSecurity_question_answer(securityAnswer.getText().toString());
            currentUser.setUser_security(encodeBase64(userPassword.getText().toString()));
            appSharedPreference.setUserInfo(currentUser);

            Toast.makeText(getApplicationContext(), "Registration Successfully", Toast.LENGTH_LONG).show();
            Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }).addOnFailureListener(e -> {
            hideProgressDialog();
            Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
        });
    }

    private boolean CheckAllFields() {
        if (userName.length() == 0) {
            userName.setError("Full name is Required");
            return false;
        }
        if (!isValidEmail(userEmail.getText().toString())) {
            userEmail.setError("Please enter valid email address");
            return false;
        }
        if (userPassword.length() == 0 || userPassword.length() < 6) {
            userPassword.setError("Password should be at least 6 character");
            return false;
        }
        if (securityQuestion.length() == 0) {
            securityQuestion.setError("Security question is Required");
            return false;
        }
        if (securityAnswer.length() == 0) {
            securityAnswer.setError("Security question answer is Required");
            return false;
        }
        return true;
    }
}