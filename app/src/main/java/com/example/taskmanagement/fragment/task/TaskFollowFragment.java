package com.example.taskmanagement.fragment.task;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.MyAdapter;
import com.example.taskmanagement.dao.TaskDao;
import com.example.taskmanagement.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;

public class TaskFollowFragment extends Fragment {

    private static final String TAG = "TAGTaskFollowFragment";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TaskDao taskDao;
    private ProgressBar progressBar;
    private RelativeLayout layoutVisibility;
    private EditText search;
    private RecyclerView recycler;
    private LinkedList<Task> taskList;
    public TaskFollowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        taskDao = new TaskDao( db , mAuth , getContext() , requireActivity().getSupportFragmentManager() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_follow, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        layoutVisibility = view.findViewById(R.id.layout_visibility);
        search = view.findViewById(R.id.search);
        recycler = view.findViewById(R.id.recycle);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                LinkedList<Task> filtres= new LinkedList<Task> ();
                for (Task task : taskList ){
                    Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+task);
                    if (task.getTitle().contains(s) || task.getDescription().contains(s)){
                        filtres.add(task);
                    }
                    Log.d(TAG, "filtres récupérées avec succès : " + filtres);
                    MyAdapter myAdapter = new MyAdapter(filtres , requireActivity().getSupportFragmentManager() , requireContext());

                    recycler.setAdapter(myAdapter);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recycler.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        showDialog();
        fetchDataAndProcess();


        return view;
    }
    private void fetchDataAndProcess() {
        taskDao.getTasksFavourite(new TaskDao.OnTasksFetchListener() {
            @Override
            public void onTasksFetchSuccess(LinkedList<Task> tasks) {
                Log.d(TAG, "Tâches récupérées avec succès : " + tasks);
                taskList = tasks;
                hideDialog();
                recycler.setHasFixedSize(true);

                MyAdapter myAdapter = new MyAdapter(tasks, requireActivity().getSupportFragmentManager() , requireContext() );
                recycler.setAdapter(myAdapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recycler.setLayoutManager(layoutManager);
            }

            @Override
            public void onTasksFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : ", e);
            }
        });
    }
    private void showDialog(){

        progressBar.setVisibility(View.VISIBLE);
        layoutVisibility.setVisibility(View.GONE);

    }
    private void hideDialog(){

        progressBar.setVisibility(View.GONE);
        layoutVisibility.setVisibility(View.VISIBLE);

    }
}