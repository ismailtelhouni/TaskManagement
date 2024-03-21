package com.example.taskmanagement.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.taskmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(() -> {
            Intent intent1;
            if(currentUser != null){

                intent1 = new Intent(getApplicationContext(), TasksActivity.class);

            }else{

                intent1 = new Intent(getApplicationContext(), AuthActivity.class);

            }
            startActivity(intent1);
            finish();
        },500);

    }
}