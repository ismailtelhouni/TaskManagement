package com.example.taskmanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.taskmanagement.adapters.MyAdapter;
import com.example.taskmanagement.adapters.TaskItemAdapter;
import com.example.taskmanagement.dao.TaskDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Task;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeRecyclerViewsFragment extends Fragment {

    private static final String TAG = "HomeRecycleFragment";
    private LinkedList<Task> tasks;
    private RecyclerView myRecycler;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private TaskDao taskDao;

    public HomeRecyclerViewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        tasks= new LinkedList<Task>();
        currentUser = mAuth.getCurrentUser();
        taskDao = new TaskDao( db ,mAuth );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_recycler_views, container, false);

        myRecycler=  view.findViewById(R.id.tasks_list_view);

        progressBar = view.findViewById(R.id.progressBar);

        currentUser = mAuth.getCurrentUser();

        Log.d(TAG,"user :"+currentUser.toString());

        showDialog();
        fetchDataAndProcess();
        return view;
    }
    private void fetchDataAndProcess() {
        taskDao.getTasks(new TaskDao.OnTaskFetchListener() {
            @Override
            public void onTaskFetchSuccess(LinkedList<Task> tasks) {
                // Le code qui dépend des tâches récupérées depuis Firestore
                Log.d(TAG, "Tâches récupérées avec succès : " + tasks);
                hideDialog();
                myRecycler.setHasFixedSize(true);

                MyAdapter myAdapter = new MyAdapter(tasks,getContext(),getActivity().getSupportFragmentManager());
                myRecycler.setAdapter(myAdapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                myRecycler.setLayoutManager(layoutManager);

            }

            @Override
            public void onTaskFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : ", e);
            }
        });
    }
    private void showDialog(){

        progressBar.setVisibility(View.VISIBLE);
        myRecycler.setVisibility(View.GONE);

    }
    private void hideDialog(){

        progressBar.setVisibility(View.GONE);
        myRecycler.setVisibility(View.VISIBLE);

    }

}