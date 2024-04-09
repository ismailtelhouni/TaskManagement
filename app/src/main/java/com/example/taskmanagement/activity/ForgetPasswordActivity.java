package com.example.taskmanagement.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.UserDao;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ForgotPasswordActivity";
    private RelativeLayout progressBar , itemVisibility ;
    private TextInputEditText emailEditText ;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        auth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);
        itemVisibility = findViewById(R.id.item_visibility);
        emailEditText = findViewById(R.id.email);
        Button btnSend = findViewById(R.id.btn_save_user);

        btnSend.setOnClickListener(this);

    }
    public void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
        itemVisibility.setVisibility(View.GONE);
    }
    public void hideDialog(){
        progressBar.setVisibility(View.GONE);
        itemVisibility.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_save_user){
            String email = String.valueOf(emailEditText.getText());

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                        Intent intent = new Intent( this , AuthActivity.class);
                        startActivity(intent);
                    }
                });
        }
    }
}