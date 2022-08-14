package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity {

    //Button SIgnUp and Login
    Button btnSignUp, btnLogin;

    //Edit text fields
    EditText userName, userEmail, userPassword, securityQuestion, securityAnswer;
    FirebaseFirestore dbroot;
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
        dbroot = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(view -> {
// store the returned value of the dedicated function which checks
            // whether the entered data is valid or if any fields are left blank.
            isAllFieldsChecked = CheckAllFields();
            // the boolean variable turns to be true then
            // only the user must be proceed to the activity2
            if (isAllFieldsChecked) {
                registerUser();
            }
        });

        btnLogin.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    public void registerUser() {
        showProgressDialog();

        Map<String, String> items = new HashMap<>();
        items.put("user_name", userName.getText().toString().trim());
        items.put("user_email", userEmail.getText().toString().trim());
        items.put("user_security", encodeBase64(userPassword.getText().toString()));
        items.put("security_question", securityQuestion.getText().toString().trim());
        items.put("security_question_answer", securityAnswer.getText().toString().trim());

        String userEmailAddress = userEmail.getText().toString();

        dbroot.collection(USERS).document(userEmailAddress).set(items).addOnCompleteListener(task -> {
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
            userName.setError("This Field is Required");
            return false;
        }
        if (userEmail.length() == 0) {
            userEmail.setError("Kindly enter a valid email address");
            return false;
        }
        if (userPassword.length() == 0 || userPassword.length() <= 6) {
            userPassword.setError("Fill atleast 6 character");
            return false;
        }
        if (securityQuestion.length() == 0) {
            securityQuestion.setError("This Field is Required");
            return false;
        }
        if (securityAnswer.length() == 0) {
            securityAnswer.setError("This Field is Required");
            return false;
        }
        return true;
    }


}