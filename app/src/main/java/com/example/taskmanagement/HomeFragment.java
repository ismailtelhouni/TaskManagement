package com.example.taskmanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.taskmanagement.adapters.TaskItemAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import model.Task;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "HomeFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Task> tasks;
    private ListView tasksListView ;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void fetchDataAndProcess() {
        getTasks(new OnTaskFetchListener() {
            @Override
            public void onTaskFetchSuccess(List<Task> tasks) {
                // Le code qui dépend des tâches récupérées depuis Firestore
                Log.d(TAG, "Tâches récupérées avec succès : " + tasks);
                tasksListView.setAdapter(new TaskItemAdapter(getActivity(), tasks));
            }

            @Override
            public void onTaskFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : ", e);
            }
        });
    }

    private void getTasks(OnTaskFetchListener listener) {

        tasks = new ArrayList<>();
        db.collection("tasks")
                .get()
                .addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String startDate = document.getString("startDate");
                            String endDate = document.getString("endDate");
                            Task task1 = new Task(title,description,startDate,endDate);
                            task1.setId(document.getId());
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tasksListView = view.findViewById(R.id.tasks_list_view);

        fetchDataAndProcess();

//        List<Task> taskList = new ArrayList<>();
//
//        taskList.add(new Task("first title","first discription","02/15/2024","02/20/2024"));
//        taskList.add(new Task("second title","second discription","02/15/2024","02/20/2024"));

        return view;
    }
    interface OnTaskFetchListener {
        void onTaskFetchSuccess(List<Task> tasks);
        void onTaskFetchFailure(Exception e);
    }
}