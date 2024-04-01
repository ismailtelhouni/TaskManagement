package com.example.taskmanagement.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.EditeProfileFragment;
import com.example.taskmanagement.fragment.SettingsFragment;
import com.example.taskmanagement.fragment.task.TaskFragment;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserDao {

    private static final String TAG = "TaskDao";
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private final Context context;
    private final FragmentManager fragmentManager;

    public UserDao(FirebaseFirestore db , FirebaseAuth mAuth , Context context, FragmentManager fragmentManager) {
        this.db = db;
        this.currentUser = mAuth.getCurrentUser();
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void getCurrentUser( OnUserFetchListener listener ){
        if(currentUser.getEmail()!=null){
            DocumentReference userRef = db.collection("user").document(currentUser.getEmail());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        User user = new User();

                        user.setId(currentUser.getEmail());
                        user.setName(document.getString("prenom"));
                        user.setLastName(document.getString("nom"));
                        user.setTel(document.getString("tel"));
                        user.setAvatar(document.getString("avatar"));

                        listener.onUserFetchSuccess(user);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    listener.onUserFetchFailure(task.getException());
                }
            });
        }
    }

    public void save(User userItem, EditeProfileFragment editeProfileFragment) {

    }

    public void update( User userItem , EditeProfileFragment editeProfileFragment ) {

        Map<String, Object> user = new HashMap<>();
        user.put("nom", userItem.getName());
        user.put("prenom", userItem.getLastName());
        user.put("tel", userItem.getTel());
        if(userItem.getAvatar()!=null){
            user.put("avatar",userItem.getAvatar());
        }

        DocumentReference userRef = db.collection("user").document(currentUser.getEmail());
        userRef.update(user)
                .addOnSuccessListener(unused -> {
                    editeProfileFragment.hideDialog();
                    Toast.makeText(context, "Edit User Success.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new SettingsFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error writing document", e);
                });

    }

    public interface OnUserFetchListener {
        void onUserFetchSuccess(User user);
        void onUserFetchFailure(Exception e);
    }
}
