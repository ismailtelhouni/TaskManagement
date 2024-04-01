package com.example.taskmanagement.fragment.task;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.TaskDao;
import com.example.taskmanagement.shared.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Objects;

import com.example.taskmanagement.model.Task;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TASK_ID = "1";
    private static final String TAG = "TaskFragment";
    private String task_id;
    private Task taskItem;
    private TextView taskItemTitle ,taskItemDate , taskItemDescription ;
    private FirebaseFirestore db;
    private ImageView taskItemDone , taskItemPending , taskItemImg;
    private Spinner spinner;
    private ProgressBar progressBar;
    private RelativeLayout taskItemVisibility;
    private FirebaseAuth mAuth;
    private TaskDao taskDao;

    public TaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment TaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskFragment newInstance(String id) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(TASK_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task_id = getArguments().getString(TASK_ID);
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        taskDao = new TaskDao(db,mAuth,getContext(),getActivity().getSupportFragmentManager());
    }

    private void fetchDataAndProcess(){
        taskDao.getTask( task_id , new TaskDao.OnTaskFetchListener() {
            @Override
            public void onTaskFetchSuccess(Task task) {
                Log.d(TAG, "Tâches récupérées avec succès : " + task);
                taskItemTitle.setText(task.getTitle());
                taskItemDescription.setText(task.getDescription());

                Picasso.with(getContext())
                        .load(task.getImg())
                        .into(taskItemImg);

                String string = Utils.getDaysUntilDate(task.getDate());
                String date = string+" - "+task.getTime();
                taskItemDate.setText(date);

                String[] etatsArray = getResources().getStringArray(R.array.task_etats);


                if(task.getEtat().equals("FINISH")){
                    taskItemDone.setVisibility(View.VISIBLE);
                    taskItemPending.setVisibility(View.GONE);

                    String choix = "finish";
                    int position = Arrays.asList(etatsArray).indexOf(choix);
                    spinner.setSelection(position);


                }else{

                    taskItemDone.setVisibility(View.GONE);
                    taskItemPending.setVisibility(View.VISIBLE);

                    String choix = "pending";
                    int position = Arrays.asList(etatsArray).indexOf(choix);
                    spinner.setSelection(position);
                }
                hideDialog();
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                        String textSelected = adapterView.getItemAtPosition(position).toString();
                        Log.d(TAG, "textttt Selected : "+textSelected);

                        if(textSelected.equals("finish")){

                            taskItemDone.setVisibility(View.VISIBLE);
                            taskItemPending.setVisibility(View.GONE);
                            String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                            if(email!=null){
                                db.collection("user").document(email).collection("tasks").document(task_id)
                                    .update("etat","FINISH")
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                            }
                        }else{

                            taskItemDone.setVisibility(View.GONE);
                            taskItemPending.setVisibility(View.VISIBLE);
                            String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                            if(email!=null){
                                db.collection("user").document(email).collection("tasks").document(task_id)
                                    .update("etat","EN_COUR")
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
            @Override
            public void onTaskFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : ", e);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        taskItemTitle = view.findViewById(R.id.task_item_title);
        taskItemDescription = view.findViewById(R.id.task_item_description);
        taskItemDate = view.findViewById(R.id.task_item_date);
        taskItemPending = view.findViewById(R.id.task_item_pending);
        taskItemDone = view.findViewById(R.id.task_item_done);
        spinner = view.findViewById(R.id.task_item_spinner);
        Button taskItemEdit = view.findViewById(R.id.task_item_edit);
        Button taskItemDelete = view.findViewById(R.id.task_item_delete);
        taskItemImg = view.findViewById(R.id.task_item_img);
        progressBar = view.findViewById(R.id.progressBar);
        taskItemVisibility = view.findViewById(R.id.task_item_visibility);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.task_etats, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        taskItemEdit.setOnClickListener(this);

        taskItemDelete.setOnClickListener(this);

        FirebaseUser currentUser = mAuth.getCurrentUser();

        Log.d(TAG,"user :"+ currentUser.toString());

        showDialog();
        fetchDataAndProcess();
        return view;
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.task_item_edit){
            Log.d(TAG,"bien edite");

            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, EditTaskFragment.newInstance(task_id));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (view.getId()==R.id.task_item_delete) {
            Log.d(TAG,"bien delete");
            taskDao.delete(task_id, new TaskDao.OnTaskDeleteListener() {
                @Override
                public void onTaskDeleteSuccess() {
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new HomeRecyclerViewsFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                @Override
                public void onTaskDeleteFailure(Exception e) {
                    Log.e(TAG, "Erreur lors de la suppression des tâches : ", e);
                }
            });
        }
    }
    private void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
        taskItemVisibility.setVisibility(View.GONE);
    }
    private void hideDialog(){
        progressBar.setVisibility(View.GONE);
        taskItemVisibility.setVisibility(View.VISIBLE);
    }
}