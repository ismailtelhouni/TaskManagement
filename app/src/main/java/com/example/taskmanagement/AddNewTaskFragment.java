package com.example.taskmanagement;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link AddNewTaskFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class AddNewTaskFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "AddNewTaskFragment";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private TextInputEditText titleEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText startDateEditText;
    private TextInputEditText endDateEditText;
    private ProgressBar progressBar;
    FirebaseFirestore db;
    private DatePickerDialog.OnDateSetListener onDateSetListener , onEndDateSetListener;
    private String startDate , endDate;

    public AddNewTaskFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment AddNewTaskFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static AddNewTaskFragment newInstance( /**String param1, String param2**/) {
//        AddNewTaskFragment fragment = new AddNewTaskFragment();
//        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
////        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_task, container, false);

        db = FirebaseFirestore.getInstance();
        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        startDateEditText = view.findViewById(R.id.date_de_debut);
        endDateEditText = view.findViewById(R.id.date_de_fin);
        Button btnSaveTask = view.findViewById(R.id.btn_save_task);
        progressBar = view.findViewById(R.id.progressBar);

        startDateEditText.setOnClickListener(view1 -> {

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog,
                    onDateSetListener,
                    year,
                    month,
                    day
            );
            dialog.getWindow();
            dialog.show();
        });
        endDateEditText.setOnClickListener(view12 -> {

            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getActivity(),
                    androidx.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert,
                    onEndDateSetListener,
                    year,
                    month,
                    day
            );
            dialog.getWindow();
            dialog.show();
        });
        onDateSetListener = (datePicker, year, month, day) -> {
            month = month+1;
            Log.d(TAG,"onDateSet : mm/dd/yyyy " +month+"/"+day+"/"+year);

            startDate = month + "/" + day + "/" + year ;
            startDateEditText.setText(startDate);
        };
        onEndDateSetListener = (datePicker, year, month, day) -> {
            month = month+1;
            Log.d(TAG,"onDateSet : mm/dd/yyyy " +month+"/"+day+"/"+year);

            endDate = month + "/" + day + "/" + year ;
            endDateEditText.setText(endDate);
        };


        btnSaveTask.setOnClickListener(v->{

            String title,description , startDate ,endDate ;
            title = String.valueOf(titleEditText.getText());
            description = String.valueOf(descriptionEditText.getText());
            startDate = String.valueOf(startDateEditText.getText());
            endDate = String.valueOf(endDateEditText.getText());

            if(TextUtils.isEmpty(title)){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(description)){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(startDate)){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(endDate)){

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            addNewTask(title,description,startDate,endDate);

        });

        return view;
    }

    private void addNewTask(String title, String description, String startDate, String endDate) {

        Map<String, Object> task = new HashMap<>();

        task.put("title",title);
        task.put("description",description);
        task.put("startDate",startDate);
        task.put("endDate",endDate);

        db.collection("tasks")
                .add(task)
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(getActivity(), "Add Task Success.", Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "DocumentSnapshot successfully written!");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));


    }
}