package com.example.taskmanagement.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextEmail , editTextPassword , editTextName , editTextLastName , editTextTel;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    FirebaseFirestore db;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
            Intent intent = new Intent(getApplicationContext(),TasksActivity.class);
            startActivity(intent);
            finish();
        }else{
            updateUI(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        editTextName = findViewById(R.id.name);
        editTextLastName = findViewById(R.id.lastName);
        editTextTel = findViewById(R.id.tel);

        textView.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext() , AuthActivity.class);
            startActivity(intent);
            finish();
        });

        buttonReg.setOnClickListener(view -> {

            progressBar.setVisibility(View.VISIBLE);
            String email,password , name ,lastName , tel;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            name = String.valueOf(editTextName.getText());
            lastName = String.valueOf(editTextLastName.getText());
            tel = String.valueOf(editTextTel.getText());


            if(TextUtils.isEmpty(email)){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(password)){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "password email", Toast.LENGTH_SHORT).show();
                return;
            }
            signUp(email,password,name,lastName,tel);

        });

    }

    private void updateUI(FirebaseUser currentUser) {

        if(currentUser != null){

            Intent intent = new Intent(getApplicationContext(),TasksActivity.class);
            startActivity(intent);
            finish();

        }

    }

    private void signUp(String email, String password , String name , String lastName , String tel ) {

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Authentication Success.", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            });

    // Create a new user with a first, middle, and last name
        Map<String, Object> docUser = new HashMap<>();
        docUser.put("nom", name);
        docUser.put("prenom", lastName);
        docUser.put("tel", tel);

    // Add a new document with a generated ID
        db.collection("user").document(email)
                .set(docUser)
                .addOnSuccessListener(aVoid -> {
                    String Tag = "Register";
                    Log.d(Tag, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    String Tag = "Register";
                    Log.w(Tag, "Error writing document", e);
                });

    }
}