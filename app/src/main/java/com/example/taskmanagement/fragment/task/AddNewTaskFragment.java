package com.example.taskmanagement.fragment.task;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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

public class AddNewTaskFragment extends Fragment {

    private static final String TAG = "AddNewTaskFragment";
    private TextInputEditText titleEditText , descriptionEditText , startDateEditText , endDateEditText;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private DatePickerDialog.OnDateSetListener onDateSetListener , onEndDateSetListener;
    private String startDate , endDate;
    private ImageButton imageUpload;
    private Uri imageUri;
    private ActivityResultLauncher<String> mGetContent;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private TaskDao taskDao;
    private Button btnSaveTask;

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
                        .transform(new BorderTransformation(R.color.black, 1, 20))
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
        startDateEditText = view.findViewById(R.id.date_de_debut);
        endDateEditText = view.findViewById(R.id.date_de_fin);
        btnSaveTask = view.findViewById(R.id.btn_save_task);
        progressBar = view.findViewById(R.id.progressBar);
        imageUpload = view.findViewById(R.id.image_upload);

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
            month = month + 1;
            Log.d(TAG, "onDateSet : mm/dd/yyyy " + month + "/" + day + "/" + year);

            startDate = month + "/" + day + "/" + year;
            startDateEditText.setText(startDate);
        };
        onEndDateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            Log.d(TAG, "onDateSet : mm/dd/yyyy " + month + "/" + day + "/" + year);

            endDate = month + "/" + day + "/" + year;
            endDateEditText.setText(endDate);
        };
        imageUpload.setOnClickListener(v -> openFileChooser());


        btnSaveTask.setOnClickListener(v -> {

            showDialog();
            Task task = new Task();
            task.setTitle(String.valueOf(titleEditText.getText()));
            task.setDescription(String.valueOf(descriptionEditText.getText()));
            task.setStartDate(String.valueOf(startDateEditText.getText()));
            task.setEndDate(String.valueOf(endDateEditText.getText()));
            startDate = String.valueOf(startDateEditText.getText());
            endDate = String.valueOf(endDateEditText.getText());

            if (TextUtils.isEmpty(task.getTitle())) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter title", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(task.getDescription())) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter description", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(startDate)) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter start date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(endDate)) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter end date", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadImage( task );
        });

        return view;
    }
    private void uploadImage(Task task ) {
        if(imageUri !=null){

            StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+ Utils.getFileExtension( imageUri , requireActivity().getContentResolver() ));
            reference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // L'image a été téléchargée avec succès
                        // Maintenant, obtenir l'URL de téléchargement
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Obtenez l'URL de téléchargement avec succès
                            String urlImage = uri.toString();
                            // Mettez à jour l'URL de l'image dans l'objet Task
                            task.setImg(urlImage);
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
}