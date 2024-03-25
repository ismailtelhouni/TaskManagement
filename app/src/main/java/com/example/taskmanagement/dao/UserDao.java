package com.example.taskmanagement.dao;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public interface OnUserFetchListener {
        void onUserFetchSuccess(User user);
        void onUserFetchFailure(Exception e);
    }
}
