package com.example.taskmanagement.fragment.user;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.activity.AuthActivity;
import com.example.taskmanagement.activity.ForgetPasswordActivity;
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.dao.UserDao;
import com.example.taskmanagement.fragment.SettingsFragment;
import com.example.taskmanagement.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment  implements View.OnClickListener{

    private ShapeableImageView avatar;
    private static final String TAG = "ChangePasswordFragment";
    private RelativeLayout progressBar , itemVisibility ;
    private TextView userName ;
    private TextInputEditText emailEditText , passwordEditText , confirmPasswordEditText , newPasswordEditText;
    private FirebaseUser currentUser;
    private UserDao userDao;
    private ViewPager2 viewPager;
    private VPAdapter adapter;
    public ChangePasswordFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        viewPager = requireActivity().findViewById(R.id.viewPager);
        userDao = new UserDao(db, auth, getContext(), requireActivity().getSupportFragmentManager() );
        adapter = (VPAdapter) viewPager.getAdapter();

        if (adapter!=null)
            adapter.addFragmentBack(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event

                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
                adapter.addFragmentWithPosition( adapter.getSizeBack()-2 );
                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
                viewPager.setCurrentItem( adapter.getItemCount()-1 , false );
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
    private void fetchDataAndProcess(){
        showDialog();
        userDao.getCurrentUser(new UserDao.OnUserFetchListener() {
            @Override
            public void onUserFetchSuccess(User user) {

                String name = user.getLastName()+" "+user.getName();
                userName.setText(name);

                emailEditText.setText(user.getId());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_change_password, container, false);

        avatar = view.findViewById(R.id.image_avatar);
        progressBar = view.findViewById(R.id.progressBar);
        itemVisibility = view.findViewById(R.id.item_visibility);
        userName = view.findViewById(R.id.user_name);
        TextView forgetYourPassword = view.findViewById(R.id.forget_your_password);
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        newPasswordEditText = view.findViewById(R.id.new_password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        Button btnSave = view.findViewById(R.id.btn_save_user);

        btnSave.setOnClickListener(this);
        forgetYourPassword.setOnClickListener(this);

        fetchDataAndProcess();
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.btn_save_user){
            showDialog();

            String email = currentUser.getEmail();
            if ( email != null ){

                String password = String.valueOf(passwordEditText.getText());
                AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "User reauthenticated successfully");

                            String newPassword = String.valueOf(newPasswordEditText.getText());
                            String confirmPassword = String.valueOf(confirmPasswordEditText.getText());

                            if(newPassword.equals(confirmPassword)){
                                currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, "User password updated.");
                                            Toast.makeText(getContext(), "User password updated.", Toast.LENGTH_SHORT).show();

//                                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                                            fragmentTransaction.replace(R.id.frame_layout, new SettingsFragment());
//                                            fragmentTransaction.addToBackStack(null);
//                                            fragmentTransaction.commit();
                                            SettingsFragment fragment = new SettingsFragment();
                                            if(adapter!=null){
                                                adapter.addFragment(fragment);
                                                adapter.notifyDataSetChanged();
                                                viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
                                            }

                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG,"Error update password :"+ e.getMessage() );
                                        Toast.makeText(getContext(), "Error update password :"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            }

                        } else {
                            Log.e(TAG, "Error reauthenticating user: " + task.getException().getMessage());
                            Toast.makeText(getContext(), "Password incorrect", Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    });
            }


        }
        else if (view.getId()==R.id.forget_your_password) {

            Intent intent = new Intent( getContext() , ForgetPasswordActivity.class);
            startActivity(intent);

        }
    }
    public void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
        itemVisibility.setVisibility(View.GONE);
    }
    public void hideDialog(){
        progressBar.setVisibility(View.GONE);
        itemVisibility.setVisibility(View.VISIBLE);
    }
}