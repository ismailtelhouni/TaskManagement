package com.example.taskmanagement.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.user.ForgetPasswordFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener , ForgetPasswordFragment.OnNavigateToForgetPasswordListener {

    TextInputEditText editTextEmail , editTextPassword ;
    Button buttonLogin ;
    TextView registerNow;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

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

    private void updateUI(FirebaseUser currentUser) {
        
        if(currentUser != null){

            Intent intent = new Intent(getApplicationContext(),TasksActivity.class);
            startActivity(intent);
            finish();
            
        }
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        registerNow = findViewById(R.id.registerNow);
        TextView forgetYourPassword = findViewById(R.id.forget_your_password);

        forgetYourPassword.setOnClickListener(this);

        registerNow.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext() , RegisterActivity.class);
            startActivity(intent);
        });

        buttonLogin.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email,password;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            if(TextUtils.isEmpty(email)){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(AuthActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(password)){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(AuthActivity.this, "password email", Toast.LENGTH_SHORT).show();
                return;
            }

            signIn(email,password);

        });
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        Toast.makeText(getApplicationContext() , "Login Successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),TasksActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(AuthActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                    }
                });
        
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.forget_your_password) {

            Intent intent = new Intent(AuthActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public void navigateToForgetPassword() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout, new ForgetPasswordFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}