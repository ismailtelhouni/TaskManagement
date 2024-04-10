package com.example.taskmanagement.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.taskmanagement.R;
import com.example.taskmanagement.activity.AuthActivity;
import com.example.taskmanagement.activity.TasksActivity;
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.dao.UserDao;
import com.example.taskmanagement.fragment.user.ChangePasswordFragment;
import com.example.taskmanagement.fragment.user.EditeProfileFragment;
import com.example.taskmanagement.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {

    private ShapeableImageView avatar;
    private static final String TAG = "SettingsFragment";
    private RelativeLayout progressBar , itemVisibility ;
    private TextView userName,email;
    private UserDao userDao;
    private ViewPager2 viewPager;
    public SettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userDao = new UserDao(db, mAuth, getContext(), requireActivity().getSupportFragmentManager() );
        viewPager = requireActivity().findViewById(R.id.viewPager);
    }
    private void fetchDataAndProcess(){
        showDialog();
        userDao.getCurrentUser(new UserDao.OnUserFetchListener() {
            @Override
            public void onUserFetchSuccess(User user) {

                String name = user.getLastName()+" "+user.getName();
                userName.setText(name);

                email.setText(user.getId());
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        avatar = view.findViewById(R.id.image_avatar);
        userName = view.findViewById(R.id.user_name);
        email = view.findViewById(R.id.email);
        CardView editeProfile = view.findViewById(R.id.card_edit_profile);
        CardView information = view.findViewById(R.id.card_information);
        CardView changePassword = view.findViewById(R.id.card_change_password);
        CardView logOut = view.findViewById(R.id.card_log_out);
        progressBar     = view.findViewById(R.id.progressBar);
        itemVisibility  = view.findViewById(R.id.item_visibility);

        editeProfile.setOnClickListener(this);
        information.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        logOut.setOnClickListener(this);

        fetchDataAndProcess();
        return view;
    }

    @Override
    public void onClick(View view) {

        VPAdapter adapter = (VPAdapter) viewPager.getAdapter();

        if (view.getId()==R.id.card_edit_profile){

            Log.d(TAG,"edite Profile");
//            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frame_layout, new EditeProfileFragment() );
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            EditeProfileFragment fragment = new EditeProfileFragment();
            if(adapter!=null){
                adapter.addFragment(fragment);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
            }

        } else if (view.getId()==R.id.card_information) {

            Log.d(TAG,"information");
//            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frame_layout, new InformationFragment());
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            InformationFragment fragment = new InformationFragment();
            if(adapter!=null){
                adapter.addFragment(fragment);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
            }

        } else if (view.getId()==R.id.card_change_password) {

            Log.d(TAG,"change password");
//            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frame_layout, new ChangePasswordFragment());
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            ChangePasswordFragment fragment = new ChangePasswordFragment();
            if(adapter!=null){
                adapter.addFragment(fragment);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
            }

        } else if (view.getId()==R.id.card_log_out) {

            Log.d(TAG,"log out");
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), AuthActivity.class);
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