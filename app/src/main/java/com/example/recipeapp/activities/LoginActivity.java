package com.example.recipeapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    //Buttons Login and Signup
    Button btnSignUp,btnUserLogin;
    EditText userEmail,userPassword;
    FirebaseFirestore dbroot;
    TextView textView2;
    boolean isAllFieldsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Login buttons with id
        btnUserLogin=findViewById(R.id.btnUserLogin);
        btnSignUp=findViewById(R.id.btnSignUp);
        userEmail=findViewById(R.id.userEmail);
        userPassword=findViewById(R.id.userPassword);
        textView2=findViewById(R.id.textView2);
        dbroot=FirebaseFirestore.getInstance();

        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
                isAllFieldsChecked = CheckAllFields();


                if(isAllFieldsChecked){
                    Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(i);
                }

            }
        });

    }
    private boolean CheckAllFields(){

        if(userEmail.length()==0){
            userEmail.setError("Kindly enter a valid email address");
            return false;
        }
        if(userPassword.length()==0 || userPassword.length()<=6){
            userPassword.setError("Fill atleast 6 character");
            return false;
        }

        return true;
    }

    public void fetchData(){
        String userDoc = userEmail.getText().toString();
        DocumentReference document = dbroot.collection("users").document(userDoc);
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    textView2.setText(documentSnapshot.getString("user_name")+" "+documentSnapshot.getString("user_email"));
                }else
                    Toast.makeText(getApplicationContext(),"Row not found",Toast.LENGTH_LONG).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failedf to fetch data",Toast.LENGTH_LONG).show();
                    }
                });
    }
}