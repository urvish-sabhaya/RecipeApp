package com.example.recipeapp.activities;

import static com.example.recipeapp.utils.FireStoreConstants.USERS;
import static com.example.recipeapp.utils.FireStoreConstants.USER_SECURITY;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPasswordActivity extends BaseActivity {

    ImageView back_img;
    EditText edt_new_password, edt_confirm_password, edt_email,
            edt_security_question, edt_security_answer;
    Button forgot_password_btn, check_email_btn;
    LinearLayout question_lay_lin;
    FirebaseFirestore db;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
    }

    private void initViews() {
        db = FirebaseFirestore.getInstance();

        back_img = findViewById(R.id.back_img);
        check_email_btn = findViewById(R.id.check_email_btn);
        edt_new_password = findViewById(R.id.edt_new_password);
        edt_confirm_password = findViewById(R.id.edt_confirm_password);
        forgot_password_btn = findViewById(R.id.forgot_password_btn);
        edt_email = findViewById(R.id.edt_email);
        edt_security_question = findViewById(R.id.edt_security_question);
        edt_security_answer = findViewById(R.id.edt_security_answer);
        question_lay_lin = findViewById(R.id.question_lay_lin);

        back_img.setOnClickListener(view -> {
            finish();
        });
        forgot_password_btn.setOnClickListener(view -> {
            validateAndChangePass();
        });
        check_email_btn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(edt_email.getText().toString())) {
                Toast.makeText(this, "Please enter email attached with account", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchUserDetails(edt_email.getText().toString());
        });
    }

    private void fetchUserDetails(String email) {
        showProgressDialog();
        db.collection(USERS)
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    hideProgressDialog();
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(getApplicationContext(), "You are not registered. Please register", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    currentUser = documentSnapshot.toObject(User.class);

                    if (currentUser == null) {
                        Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    question_lay_lin.setVisibility(View.VISIBLE);
                    check_email_btn.setVisibility(View.GONE);
                    edt_email.setEnabled(false);
                    edt_security_question.setText(currentUser.getSecurity_question());
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                });
    }

    private void validateAndChangePass() {
        if (currentUser == null) {
            Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show();
            return;
        }

        String securityAnswer = edt_security_answer.getText().toString();
        String newPass = edt_new_password.getText().toString();
        String confirmPass = edt_confirm_password.getText().toString();

        if (TextUtils.isEmpty(securityAnswer)) {
            Toast.makeText(this, "Please enter security question answer", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!currentUser.getSecurity_question_answer().equalsIgnoreCase(securityAnswer)) {
            Toast.makeText(this, "Your security question answer is wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPass)) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() <= 6) {
            Toast.makeText(this, "Password should be more than 6 characters", Toast.LENGTH_SHORT).show();
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

        updateNewPassword(confirmPass);
    }

    private void updateNewPassword(String confirmPass) {
        showProgressDialog();
        String encoded = encodeBase64(confirmPass);
        DocumentReference documentReference = db.collection(USERS).document(currentUser.getUser_email());
        documentReference
                .update(USER_SECURITY, encoded)
                .addOnSuccessListener(aVoid -> {
//                    currentUser.setUser_security(encoded);
//                    appSharedPreference.setUserInfo(currentUser);
                    Toast.makeText(ForgotPasswordActivity.this, "Your password has been changed successfully", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();

                   /* question_lay_lin.setVisibility(View.GONE);
                    check_email_btn.setVisibility(View.VISIBLE);
                    edt_email.setEnabled(true);

                    edt_email.setText("");
                    edt_security_question.setText("");
                    edt_security_answer.setText("");
                    edt_new_password.setText("");
                    edt_confirm_password.setText("");*/

                    Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                })
                .addOnFailureListener(e -> {
                    Log.e("failureListener", e.toString());
                    Toast.makeText(ForgotPasswordActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                });
    }
}