package com.example.taskmanagement.fragment.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.taskmanagement.R;
import com.example.taskmanagement.activity.AuthActivity;
import com.example.taskmanagement.dao.UserDao;
import com.example.taskmanagement.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ForgetPasswordFragment extends Fragment implements View.OnClickListener {
    private ShapeableImageView avatar;
    private static final String TAG = "ChangePasswordFragment";
    private RelativeLayout progressBar , itemVisibility ;
    private TextView userName ;
    private TextInputEditText emailEditText ;
    private UserDao userDao;
    FirebaseAuth auth;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userDao = new UserDao(db, auth, getContext(), requireActivity().getSupportFragmentManager() );
    }
    private void fetchDataAndProcess(){
        showDialog();
        userDao.getCurrentUser(new UserDao.OnUserFetchListener() {
            @Override
            public void onUserFetchSuccess(User user) {

                String name = user.getLastName()+" "+user.getName();
                userName.setText(name);

                if( user.getAvatar() != null ){

                    Picasso.with(getContext())
                            .load(user.getAvatar())
                            .into(avatar);

                }
                hideDialog();

            }
            @Override
            public void onUserFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération de utilisateur : ", e);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        avatar = view.findViewById(R.id.image_avatar);
        progressBar = view.findViewById(R.id.progressBar);
        itemVisibility = view.findViewById(R.id.item_visibility);
        userName = view.findViewById(R.id.user_name);
        emailEditText = view.findViewById(R.id.email);
        Button btnSave = view.findViewById(R.id.btn_save_user);

        btnSave.setOnClickListener(this);

        fetchDataAndProcess();
        return view;
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
                        Intent intent = new Intent( getActivity() , AuthActivity.class);
                        startActivity(intent);
                    }
                });
        }
    }
    public interface OnNavigateToForgetPasswordListener {
        void navigateToForgetPassword();
    }
}