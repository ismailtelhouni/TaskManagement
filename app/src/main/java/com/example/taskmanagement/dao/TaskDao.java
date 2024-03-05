package com.example.taskmanagement.dao;

import android.util.Log;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.taskmanagement.HomeRecyclerViewsFragment;
import com.example.taskmanagement.adapters.MyAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

import model.Task;

public class TaskDao {

    private static final String TAG = "TaskDao";
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    public TaskDao(FirebaseFirestore db, FirebaseAuth mAuth) {
        this.db = db;
        this.currentUser = mAuth.getCurrentUser();
    }

    public void getTasks( OnTaskFetchListener listener) {

        LinkedList<Task> tasks = new LinkedList<>();

        CollectionReference userTasksRef = db.collection("user").document(currentUser.getEmail()).collection("tasks");

        userTasksRef
                .orderBy("startDate", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String startDate = document.getString("startDate");
                            String endDate = document.getString("endDate");

                            String etat = document.getString("etat");
                            Task task1 = new Task();
                            task1.setId(document.getId());
                            task1.setTitle(title);
                            task1.setDescription(description);
                            task1.setStartDate(startDate);
                            task1.setEndDate(endDate);
                            task1.setDoc_url(document.getString("doc_url"));
                            task1.setImg(document.getString("img"));
                            task1.setEtat(etat);

                            tasks.add(task1);
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        Log.d(TAG, "Tâches récupérées avec succès : " + tasks);
                        listener.onTaskFetchSuccess(tasks);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        listener.onTaskFetchFailure(task.getException());
                    }
                });
    }

    public interface OnTaskFetchListener {
        void onTaskFetchSuccess(LinkedList<Task> tasks);
        void onTaskFetchFailure(Exception e);
    }

}
