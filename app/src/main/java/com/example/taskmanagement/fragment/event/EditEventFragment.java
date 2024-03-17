package com.example.taskmanagement.fragment.event;

import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.EventDao;
import com.example.taskmanagement.model.Event;
import com.example.taskmanagement.shared.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEventFragment extends Fragment implements View.OnClickListener{

    private static final String EVENT_ID = "1";
    private static final String TAG = "EditEventFragment";
    private String event_id;
    private Event oldEvent;
    private TextInputEditText titleEditText , categoryEditText , lieuEditText , descriptionEditText , startDateEditText , endDateEditText;
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
    private EventDao eventDao;
    private Button btnSaveEvent;
    public EditEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment EditEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditEventFragment newInstance(String id) {
        EditEventFragment fragment = new EditEventFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event_id = getArguments().getString(EVENT_ID);
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("events");
        eventDao = new EventDao(db,mAuth,getContext(),getActivity().getSupportFragmentManager());

        if( mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            Log.d(TAG,"user :"+currentUser.toString());
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);

        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        categoryEditText = view.findViewById(R.id.category);
        lieuEditText = view.findViewById(R.id.lieu);
        startDateEditText = view.findViewById(R.id.date_de_debut);
        endDateEditText = view.findViewById(R.id.date_de_fin);
        btnSaveEvent = view.findViewById(R.id.btn_save_event);
        progressBar = view.findViewById(R.id.progressBar);
        imageUpload = view.findViewById(R.id.image_upload);

        startDateEditText.setOnClickListener(this);
        endDateEditText.setOnClickListener(this);
        imageUpload.setOnClickListener(this);
        btnSaveEvent.setOnClickListener(this);

        fetchDataAndProcess();

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

        return view;
    }

    private void fetchDataAndProcess() {
        eventDao.getEvent(event_id, new EventDao.OnEventFetchListener() {
            @Override
            public void onEventFetchSuccess(Event event) {
                oldEvent = event;
                titleEditText.setText(event.getTitle());
                descriptionEditText.setText(event.getDescription());
                startDateEditText.setText(event.getStartDate());
                endDateEditText.setText(event.getEndDate());
                categoryEditText.setText(event.getCategory());
                lieuEditText.setText(event.getLieu());
                Picasso.with(getContext())
                        .load(event.getImage())
                        .into(imageUpload);
            }

            @Override
            public void onEventFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération de event : ", e);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_save_event){

            showDialog();
            Event event = new Event(
                    null ,
                    String.valueOf( titleEditText.getText() ) ,
                    String.valueOf( descriptionEditText.getText() ) ,
                    String.valueOf( lieuEditText.getText() ) ,
                    String.valueOf( categoryEditText.getText() ),
                    null ,
                    null ,
                    String.valueOf( startDateEditText.getText() ) ,
                    String.valueOf( endDateEditText.getText() ) ,
                    currentUser.getEmail()
            );
            startDate = String.valueOf(startDateEditText.getText());
            endDate = String.valueOf(endDateEditText.getText());

            if (TextUtils.isEmpty(event.getTitle())) {

                hideDialog();
                Toast.makeText(getActivity(), "Enter title", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(event.getDescription())) {

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
            uploadImage( event );

        } else if ( view.getId() == R.id.date_de_fin) {

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

        } else if ( view.getId() == R.id.date_de_debut) {

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

        } else if ( view.getId() == R.id.image_upload) {
            openFileChooser();
        }


    }

    private void uploadImage(Event event) {
        if(imageUri !=null){
            StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+ Utils.getFileExtension( imageUri , requireActivity().getContentResolver() ) );

            String oldImageUrl = oldEvent.getImage();
            StorageReference oldImageReference = null;

            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {

                // Créer une référence à l'ancienne image dans Firebase Storage
                oldImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);

            }

            StorageReference finalOldImageReference = oldImageReference;

            reference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {

                        // L'image a été téléchargée avec succès
                        // Maintenant, obtenir l'URL de téléchargement

                        reference.getDownloadUrl().addOnSuccessListener(uri -> {

                            // Obtenez l'URL de téléchargement avec succès
                            String urlImage = uri.toString();
                            // Mettez à jour l'URL de l'image dans l'objet Task
                            event.setImage(urlImage);
                            // Ajouter la nouvelle tâche avec l'image à la base de données
                            if ( finalOldImageReference != null) {

                                finalOldImageReference.delete().addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Ancienne image supprimée avec succès");
                                    // Ajouter la nouvelle tâche avec l'image à la base de données
                                    eventDao.update( event_id , event );
                                }).addOnFailureListener(e -> {
                                    Log.e(TAG, "Échec de la suppression de l'ancienne image : " + e.getMessage());
                                    // Si la suppression de l'ancienne image échoue, mettez quand même à jour la tâche
                                    eventDao.update( event_id , event );
                                });
                            } else {
                                // Si aucune ancienne image n'existe, mettre simplement à jour la tâche
                                eventDao.update( event_id , event );
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
                    });
        }else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void openFileChooser() {
        mGetContent.launch("image/*");
    }
    public void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
        btnSaveEvent.setVisibility(View.GONE);
    }
    public void hideDialog(){
        progressBar.setVisibility(View.GONE);
        btnSaveEvent.setVisibility(View.VISIBLE);
    }
}