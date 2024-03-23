package com.example.taskmanagement.fragment.task;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.MyAdapter;
import com.example.taskmanagement.dao.TaskDao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.Objects;

import com.example.taskmanagement.model.Task;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeRecyclerViewsFragment extends Fragment {

    private static final String TAG = "HomeRecycleFragment";
    private LinkedList<Task> taskList;
    private RecyclerView myRecycler;
    private FirebaseFirestore db;
    private LinearLayout progressBar;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private TaskDao taskDao;
    private EditText search;

    public HomeRecyclerViewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        taskList= new LinkedList<Task>();
        currentUser = mAuth.getCurrentUser();
        taskDao = new TaskDao( db ,mAuth ,getContext(),getActivity().getSupportFragmentManager());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_recycler_views, container, false);
        myRecycler=  view.findViewById(R.id.tasks_list_view);
        progressBar = view.findViewById(R.id.progressBar);
        search= view.findViewById(R.id.search);
        Log.d(TAG,"user :"+currentUser.toString());

        search.addTextChangedListener(new TextWatcher() {


            @Override
            public void afterTextChanged(Editable s) {

                Log.d(TAG, "filtres récupérées avec succès : afterTextChanged ");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

                Log.d(TAG, "filtres récupérées avec succès : beforeTextChanged");
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+s.toString());
                LinkedList<Task> filtres= new LinkedList<Task> ();
                for (Task task : taskList ){
                    Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+task);
                    if (task.getTitle().contains(s) || task.getDescription().contains(s)){
                        filtres.add(task);
                    }
                    Log.d(TAG, "filtres récupérées avec succès : " + filtres);
                    MyAdapter myAdapter = new MyAdapter(filtres,getContext(),getActivity().getSupportFragmentManager());

                    myRecycler.setAdapter(myAdapter);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    myRecycler.setLayoutManager(layoutManager);
                    }
            }
        });

        showDialog();
        fetchDataAndProcess();
        return view;
    }
    private void fetchDataAndProcess() {
        taskDao.getTasks(new TaskDao.OnTasksFetchListener() {

            @Override
            public void onTasksFetchSuccess(LinkedList<Task> tasks) {
                // Le code qui dépend des tâches récupérées depuis Firestore
                Log.d(TAG, "Tâches récupérées avec succès : " + tasks);
                taskList = tasks;
                hideDialog();
                myRecycler.setHasFixedSize(true);

                MyAdapter myAdapter = new MyAdapter(tasks,getContext(), requireActivity().getSupportFragmentManager());
                myRecycler.setAdapter(myAdapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                myRecycler.setLayoutManager(layoutManager);

            }

            @Override
            public void onTasksFetchFailure(Exception e) {
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