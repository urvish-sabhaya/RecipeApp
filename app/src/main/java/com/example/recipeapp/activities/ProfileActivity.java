package com.example.recipeapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.recipeapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    ImageView back_img;
    EditText textName;
    TextView textEmail;
    Button edit,save;
    FirebaseFirestore dbroot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back_img = findViewById(R.id.back_img);
        textEmail = findViewById(R.id.textEmail);
        textName = findViewById(R.id.textName);
        edit = findViewById(R.id.edit);
        dbroot = FirebaseFirestore.getInstance();

        back_img.setOnClickListener(view -> {
            finish();
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editData();

            }
        });
    }


    public void editData(){
            //Using the email id displayed on the screen(Temporary)
        //Replace with Models to get data
            String useremail = textEmail.getText().toString();


            //Document To GET data from database
        DocumentReference document = dbroot.collection("users").document(useremail);
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists()){
                    textEmail.setText(documentSnapshot.getString("user_email"));

                }else  Toast.makeText(getApplicationContext(),"Wrong email",Toast.LENGTH_LONG).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed to fetch data",Toast.LENGTH_LONG).show();
            }
        });


            //To PUT data into Database
            Map<String,String> items = new HashMap<>();
            items.put("user_name",textName.getText().toString().trim());
            //To send data into the USERS collection and change username
            dbroot.collection("users").document(useremail).set(items).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    textName.setText("");
                    Toast.makeText(getApplicationContext(),"Username Updated Successfully",Toast.LENGTH_LONG).show();
                }
            });




    }

}