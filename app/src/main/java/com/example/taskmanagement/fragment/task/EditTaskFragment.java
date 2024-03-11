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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditTaskFragment extends Fragment {

    private static final String TASK_ID = "1";
    private String task_id;
    private static final String TAG = "UpdateTaskFragment";
    private TextInputEditText titleEditText , descriptionEditText , startDateEditText , endDateEditText ;
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
    private Task oldTask;
    private TaskDao taskDao;

    public EditTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param task_id Parameter 1.
     * @return A new instance of fragment EditTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditTaskFragment newInstance(String task_id) {
        EditTaskFragment fragment = new EditTaskFragment();
        Bundle args = new Bundle();
        args.putString(TASK_ID, task_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task_id = getArguments().getString(TASK_ID);
        }
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
        mAuth = FirebaseAuth.getInstance();
        if( mAuth.getCurrentUser() != null ){
            currentUser = mAuth.getCurrentUser();
        }
        Log.d(TAG,"user :"+currentUser.toString());
        db = FirebaseFirestore.getInstance();
        taskDao = new TaskDao(db,mAuth,getContext(),getActivity().getSupportFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);

        storageReference = FirebaseStorage.getInstance().getReference("tasks");

        fetchDataAndProcess();

        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        startDateEditText = view.findViewById(R.id.date_de_debut);
        endDateEditText = view.findViewById(R.id.date_de_fin);
        Button btnSaveTask = view.findViewById(R.id.btn_save_task);
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

            Task task = new Task();
            task.setTitle(String.valueOf(titleEditText.getText()));
            task.setDescription(String.valueOf(descriptionEditText.getText()));
            task.setStartDate(String.valueOf(startDateEditText.getText()));
            task.setEndDate(String.valueOf(endDateEditText.getText()));
            startDate = String.valueOf(startDateEditText.getText());
            endDate = String.valueOf(endDateEditText.getText());

            if (TextUtils.isEmpty(task.getTitle())) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(task.getDescription())) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(startDate)) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(endDate)) {

                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadImage( task );
        });

        return view;
        
    }

    private void fetchDataAndProcess() {
        taskDao.getTask(task_id , new TaskDao.OnTaskFetchListener() {
            @Override
            public void onTaskFetchSuccess(Task task) {
                titleEditText.setText(task.getTitle());
                descriptionEditText.setText(task.getDescription());
                startDateEditText.setText(task.getStartDate());
                endDateEditText.setText(task.getEndDate());
            }
            @Override
            public void onTaskFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : ", e);
            }
        });
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = requireActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(Task task ) {

        if(imageUri !=null){

            String oldImageUrl = oldTask.getImg();
            StorageReference oldImageReference = null;

            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {

                // Créer une référence à l'ancienne image dans Firebase Storage
                oldImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);

            }

            StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

            StorageReference finalOldImageReference = oldImageReference;
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
                        if ( finalOldImageReference != null) {

                            finalOldImageReference.delete().addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Ancienne image supprimée avec succès");
                                // Ajouter la nouvelle tâche avec l'image à la base de données
                                taskDao.update( task_id , task );
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Échec de la suppression de l'ancienne image : " + e.getMessage());
                                // Si la suppression de l'ancienne image échoue, mettez quand même à jour la tâche
                                taskDao.update( task_id , task );
                            });
                        } else {
                            // Si aucune ancienne image n'existe, mettre simplement à jour la tâche
                            taskDao.update( task_id , task );
                        }
                    }).addOnFailureListener(e -> {
                        // Échec de l'obtention de l'URL de téléchargement
                        Toast.makeText(getContext(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Échec du téléchargement de l'image
                    Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG , "Upload failed: " + e.getMessage());
                })
                .addOnProgressListener(snapshot -> {
                    // Suivi de la progression du téléchargement
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                });
        }else{
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void openFileChooser() {
        mGetContent.launch("image/*");
    }
}