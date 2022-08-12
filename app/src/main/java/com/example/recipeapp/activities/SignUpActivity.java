package com.example.recipeapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity {

    //Button SIgnUp and Login
    Button btnSignUp, btnLogin;

    //Edit text fields
    EditText userName,userEmail,userPassword,securityQuestion,securityAnswer;
    FirebaseFirestore dbroot;
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //register buttons with ids
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        userName=findViewById(R.id.userName);
        userEmail=findViewById(R.id.userEmail);
        userPassword=findViewById(R.id.userPassword);
        securityQuestion=findViewById(R.id.securityQuestion);
        securityAnswer=findViewById(R.id.securityAnswer);
        dbroot=FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// store the returned value of the dedicated function which checks
                // whether the entered data is valid or if any fields are left blank.
                isAllFieldsChecked = CheckAllFields();
                inserdata();
                // the boolean variable turns to be true then
                // only the user must be proceed to the activity2
                if(isAllFieldsChecked){
                    Intent i = new Intent(SignUpActivity.this,HomeActivity.class);
                    startActivity(i);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(ii);
            }
        });


    }

    public void inserdata(){

        Map<String,String> items = new HashMap<>();
        items.put("user_name",userName.getText().toString().trim());
        items.put("user_email",userEmail.getText().toString().trim());
        items.put("user_password",userPassword.getText().toString().trim());
        items.put("security_question",securityQuestion.getText().toString().trim());
        items.put("security_question_answer",securityAnswer.getText().toString().trim());

    String  user__name = userEmail.getText().toString();
//       const Object newId = dbroot.createId();

        dbroot.collection("users").document(user__name).set(items).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userName.setText("");
                userEmail.setText("");
                userPassword.setText("");
                Toast.makeText(getApplicationContext(),"Inserted Succesfully",Toast.LENGTH_LONG).show();
            }
        });
//        dbroot.collection("users").add(items).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//                userName.setText("");
//                userEmail.setText("");
//                userPassword.setText("");
//                Toast.makeText(getApplicationContext(),"Inserted Succesfully",Toast.LENGTH_LONG).show();
//            }
//        });
    }
    // function which checks all the text fields
    // are filled or not by the user.
    // when user clicks on the PROCEED button
    // this function is triggered.

    private boolean CheckAllFields(){
        if(userName.length()==0){
            userName.setError("This Field is Required");
            return false;
        }
        if(userEmail.length()==0){
            userEmail.setError("Kindly enter a valid email address");
            return false;
        }
        if(userPassword.length()==0 || userPassword.length()<=6){
            userPassword.setError("Fill atleast 6 character");
            return false;
        }
        if(securityQuestion.length()==0){
            securityQuestion.setError("This Field is Required");
            return false;
        }
        if(securityAnswer.length()==0){
            securityAnswer.setError("This Field is Required");
            return false;
        }
        return true;
    }


}