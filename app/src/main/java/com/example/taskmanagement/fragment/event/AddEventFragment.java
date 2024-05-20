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

public class AddEventFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "TAGAddEventFragment";
    private TextInputEditText titleEditText , categoryEditText , lieuEditText , descriptionEditText , startDateEditText , endDateEditText;
    private ProgressBar progressBar;
    private DatePickerDialog.OnDateSetListener onDateSetListener , onEndDateSetListener;
    private String startDate , endDate;
    private ImageButton imageUpload;
    private Uri imageUri;
    private ActivityResultLauncher<String> mGetContent;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private EventDao eventDao;
    private Button btnSaveEvent;
    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("events");
        eventDao = new EventDao(db, mAuth, getContext() , requireActivity().getSupportFragmentManager() );

        if( mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            Log.d(TAG,"user :"+currentUser.toString());
        }

//        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
//            @Override
//            public void handleOnBackPressed() {
//                // Handle the back button event
//
//                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
//                adapter.addFragmentWithPosition( adapter.getSizeBack()-2 );
//                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
//                viewPager.setCurrentItem( adapter.getItemCount()-1 , false );
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

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
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

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
                currentUser.getEmail(),
                    false);
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
                    requireActivity(),
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
                    requireActivity(),
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
                            eventDao.save( event , this );
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
        btnSaveEvent.setVisibility(View.GONE);
    }
    public void hideDialog(){
        progressBar.setVisibility(View.GONE);
        btnSaveEvent.setVisibility(View.VISIBLE);
    }
}