package com.example.taskmanagement.fragment.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.TaskDao;
import com.example.taskmanagement.shared.Utils;
import com.example.taskmanagement.transformation.BorderTransformation;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import com.example.taskmanagement.model.Task;

public class AddNewTaskFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AddNewTaskFragment";
    private TextInputEditText titleEditText , descriptionEditText , startDateEditText , endDateEditText;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private ImageButton imageUpload;
    private Uri imageUri;
    private ActivityResultLauncher<String> mGetContent;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private Calendar calendar;
    private TaskDao taskDao;
    private Button btnSaveTask;
    private CardView btnDate , btnTime;
    private TextView textDate,textTime ;
    private String StringTime;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        Log.d(TAG,"user :"+currentUser.toString());
    }
    public AddNewTaskFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    imageUri = result;
                    Picasso
                        .with(getContext())
                        .load(imageUri)
                        .into(imageUpload);
                }
            });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_new_task, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("tasks");
        taskDao = new TaskDao(db,mAuth,getContext(),getActivity().getSupportFragmentManager());

        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        btnSaveTask = view.findViewById(R.id.btn_save_task);
        progressBar = view.findViewById(R.id.progressBar);
        imageUpload = view.findViewById(R.id.image_upload);
        btnDate = view.findViewById(R.id.btn_date);
        btnTime = view.findViewById(R.id.btn_time);
        textDate = view.findViewById(R.id.item_date_time);
        textTime = view.findViewById(R.id.item_card_time);

        imageUpload.setOnClickListener(v -> openFileChooser());

        calendar = Calendar.getInstance();

        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btnSaveTask.setOnClickListener(this);

        return view;
    }
    private void uploadImage(Task task ) {
        if(imageUri !=null){
            StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+ Utils.getFileExtension( imageUri , requireActivity().getContentResolver() ));
            reference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {

                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Obtenez l'URL de téléchargement avec succès
                        String urlImage = uri.toString();
                        // Mettez à jour l'URL de l'image dans l'objet Task
                        task.setImg(urlImage);
                        Log.d(TAG , "timedvysvsdv :"+task.getTime());
                        // Ajouter la nouvelle tâche avec l'image à la base de données
                        taskDao.save( task ,this);
                    }).addOnFailureListener(e -> {
                        // Échec de l'obtention de l'URL de téléchargement
                        Toast.makeText(getContext(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideDialog();
                    });
                })
                .addOnFailureListener(e -> {
                    // Échec du téléchargement de l'image
                    Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG , "Upload failed: " + e.getMessage());
                    hideDialog();
                });

        }else{
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
            hideDialog();
        }
    }
    private void openFileChooser() {
        mGetContent.launch("image/*");
    }
    public void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
        btnSaveTask.setVisibility(View.GONE);
    }
    public void hideDialog(){
        progressBar.setVisibility(View.GONE);
        btnSaveTask.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btn_date){

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            textDate.setText(dayOfMonth + "." + (month + 1) + "." + year);
                        }
                    }, year, month, dayOfMonth);

            datePickerDialog.show();

        } else if ( view.getId()==R.id.btn_time ) {

            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        textTime.setText(hourOfDay + ":" + minute);

                        StringTime = hourOfDay + ":" + minute;
                        Log.d(TAG , "timesdddddddd :"+StringTime);

                    }
                }, hourOfDay, minute, true);
            timePickerDialog.show();
        } else if (view.getId()==R.id.btn_save_task) {
            showDialog();
            Task taskItem = new Task();
            taskItem.setTitle(String.valueOf(titleEditText.getText()));
            taskItem.setDescription(String.valueOf(descriptionEditText.getText()));
            taskItem.setDate(String.valueOf(textDate.getText()));
            taskItem.setTime((String) textTime.getText());

            if (TextUtils.isEmpty(taskItem.getTitle())) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter title", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(taskItem.getDescription())) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter description", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(taskItem.getDate())) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter start date", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG,"timmmmmmmmme :" +textTime.getText());
            if (TextUtils.isEmpty(taskItem.getTime())) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter time", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadImage( taskItem );
        }

    }
}