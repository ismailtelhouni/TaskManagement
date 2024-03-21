package com.example.taskmanagement.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanagement.fragment.task.AddNewTaskFragment;
import com.example.taskmanagement.fragment.task.HomeRecyclerViewsFragment;
import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.task.TaskFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.example.taskmanagement.model.Task;

public class TaskDao {
    private static final String TAG = "TaskDao";
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private final Context context;
    private final FragmentManager fragmentManager;
    public TaskDao(FirebaseFirestore db, FirebaseAuth mAuth,Context context, FragmentManager fragmentManager) {
        this.db = db;
        this.currentUser = mAuth.getCurrentUser();
        this.context=context;
        this.fragmentManager=fragmentManager;
    }
        public void getTasks( OnTasksFetchListener listener) {

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
                        listener.onTasksFetchSuccess(tasks);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        listener.onTasksFetchFailure(task.getException());
                    }
                });
    }
    public void save(Task taskModel , AddNewTaskFragment fragment){

        Map<String, Object> task = new HashMap<>();

        task.put("title",taskModel.getTitle());
        task.put("description",taskModel.getDescription());
        task.put("endDate",taskModel.getEndDate());
        task.put("startDate",taskModel.getStartDate());
        task.put("img",taskModel.getImg());
        task.put("doc_url",taskModel.getDoc_url());
        task.put("etat","EN_ATENTE");

        CollectionReference userTasksRef = db.collection("user").document(currentUser.getEmail()).collection("tasks");

        userTasksRef.add(task)
            .addOnSuccessListener(aVoid -> {

                Toast.makeText(context, "Add Task Success.", Toast.LENGTH_SHORT).show();
                fragment.hideDialog();
                Log.d(TAG, "DocumentSnapshot successfully written!");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new HomeRecyclerViewsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            })
            .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
    public void update( String task_id , Task taskModel ){

        if(currentUser.getEmail() != null){
            Map<String, Object> task = new HashMap<>();
            task.put("title",taskModel.getTitle());
            task.put("description",taskModel.getDescription());
            task.put("endDate",taskModel.getEndDate());
            task.put("startDate",taskModel.getStartDate());
            task.put("img",taskModel.getImg());
            task.put("doc_url",taskModel.getDoc_url());
            task.put("etat",taskModel.getEtat());
            CollectionReference userTasksRef = db.collection("user").document(currentUser.getEmail()).collection("tasks");
            userTasksRef.document(task_id)
                .update(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Add Task Success.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, TaskFragment.newInstance(task_id));
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }
    }
    public void delete(String taskId, OnTaskDeleteListener listener){

        DocumentReference taskRef = db.collection("user")
                .document(currentUser.getEmail())
                .collection("tasks")
                .document(taskId);

        taskRef.delete()
                .addOnSuccessListener(aVoid -> {
                    listener.onTaskDeleteSuccess();
                })
                .addOnFailureListener(e -> {
                    listener.onTaskDeleteFailure(e);
                });

    }
    public void getTask( String taskId , OnTaskFetchListener listener ){
        DocumentReference userTaskRef = db.collection("user").document(currentUser.getEmail()).collection("tasks").document(taskId);
        userTaskRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    Task taskItem = new Task();

                    taskItem.setId(taskId);
                    taskItem.setTitle(document.getString("title"));
                    taskItem.setDescription(document.getString("description"));
                    taskItem.setStartDate(document.getString("startDate"));
                    taskItem.setEndDate(document.getString("endDate"));
                    taskItem.setEtat(document.getString("etat"));
                    taskItem.setDoc_url(document.getString("doc_url"));
                    taskItem.setImg(document.getString("img"));

                    listener.onTaskFetchSuccess(taskItem);
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                listener.onTaskFetchFailure(task.getException());
            }
        });
    }
    public interface OnTasksFetchListener {
        void onTasksFetchSuccess(LinkedList<Task> tasks);
        void onTasksFetchFailure(Exception e);
    }
    public interface OnTaskDeleteListener {
        void onTaskDeleteSuccess();
        void onTaskDeleteFailure(Exception e);
    }
    public interface OnTaskFetchListener {
        void onTaskFetchSuccess(Task task);
        void onTaskFetchFailure(Exception e);
    }
}
