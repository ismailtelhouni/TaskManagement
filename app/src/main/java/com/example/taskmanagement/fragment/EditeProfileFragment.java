package com.example.taskmanagement.fragment;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.UserDao;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.shared.Utils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditeProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EditeProfileFragment" ;
    private RelativeLayout progressBar , itemVisibility ;
    private ShapeableImageView imageAvatar ;
    private TextView upload ;
    private TextInputEditText nameEditText , lastNameEditText , phoneEditText , emailEditText ;
    private Button btnSave ;
    private FirebaseFirestore db;
    private Uri imageUri;
    private ActivityResultLauncher<String> mGetContent;
    private StorageReference storageReference;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private UserDao userDao;
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();

        Log.d(TAG,"user :"+currentUser.toString());
    }
    public EditeProfileFragment() {
        // Required empty public constructor
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
                        .into(imageAvatar);
                }
            });
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("users");
        userDao = new UserDao( db , mAuth , getContext() , requireActivity().getSupportFragmentManager() );

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edite_profile, container, false);

        progressBar     = view.findViewById(R.id.progressBar);
        itemVisibility  = view.findViewById(R.id.item_visibility);
        imageAvatar     = view.findViewById(R.id.image_avatar);
        upload          = view.findViewById(R.id.btn_upload);
        nameEditText    = view.findViewById(R.id.name);
        lastNameEditText= view.findViewById(R.id.lastName);
        phoneEditText   = view.findViewById(R.id.tel);
        emailEditText   = view.findViewById(R.id.email);
        btnSave         = view.findViewById(R.id.btn_save_user);

        btnSave.setOnClickListener(this);
        upload.setOnClickListener(this);
        fetchDataAndProcess();


        return view;
    }
    public void fetchDataAndProcess(){
        showDialog();
        userDao.getCurrentUser(new UserDao.OnUserFetchListener() {
            @Override
            public void onUserFetchSuccess(User user) {

                nameEditText.setText(user.getName());
                lastNameEditText.setText(user.getLastName());
                phoneEditText.setText(user.getTel());
                emailEditText.setText(user.getId());
                if( user.getAvatar() != null ){

                    Picasso.with(getContext())
                            .load(user.getAvatar())
                            .into(imageAvatar);

                }
                hideDialog();

            }
            @Override
            public void onUserFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération de user : ", e);
            }
        });
    }
    @Override
    public void onClick(View view) {
        if( view.getId() == R.id.btn_save_user ){

            showDialog();

            User userItem = new User();
            userItem.setName(String.valueOf(nameEditText.getText()));
            userItem.setLastName(String.valueOf(lastNameEditText.getText()));
            userItem.setTel(String.valueOf(phoneEditText.getText()));
            uploadImage(userItem);

        } else if ( view.getId() == R.id.btn_upload ) {
            Log.d(TAG,"bien upload");
            openFileChooser();
        }
    }
    private void uploadImage(User userItem) {
        if(imageUri !=null){
            if( userItem.getAvatar() != null ){

                StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+ Utils.getFileExtension( imageUri , requireActivity().getContentResolver() ));
                reference.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {

                            reference.getDownloadUrl().addOnSuccessListener(uri -> {

                                String urlImage = uri.toString();
                                userItem.setAvatar(urlImage);
                                Log.d(TAG , "timedvysvsdv :"+userItem.getAvatar());
                                userDao.update( userItem ,this);

                            }).addOnFailureListener(e -> {

                                Toast.makeText(getContext(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                hideDialog();

                            });
                        })
                        .addOnFailureListener(e -> {

                            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG , "Upload failed: " + e.getMessage());
                            hideDialog();

                        });

            }else{

                String oldImageUrl = userItem.getAvatar();
                StorageReference oldImageReference = null;

                if (oldImageUrl != null && !oldImageUrl.isEmpty()) {

                    // Créer une référence à l'ancienne image dans Firebase Storage
                    oldImageReference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);

                }

                StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+Utils.getFileExtension( imageUri , requireActivity().getContentResolver() ));

                StorageReference finalOldImageReference = oldImageReference;
                reference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {

                        reference.getDownloadUrl().addOnSuccessListener(uri -> {

                            // Obtenez l'URL de téléchargement avec succès
                            String urlImage = uri.toString();
                            // Mettez à jour l'URL de l'image dans l'objet Task
                            userItem.setAvatar(urlImage);
                            // Ajouter la nouvelle tâche avec l'image à la base de données
                            if ( finalOldImageReference != null) {

                                finalOldImageReference.delete().addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Ancienne image supprimée avec succès");
                                    // Ajouter la nouvelle tâche avec l'image à la base de données
                                    userDao.update( userItem  , this);
                                }).addOnFailureListener(e -> {
                                    Log.e(TAG, "Échec de la suppression de l'ancienne image : " + e.getMessage());
                                    // Si la suppression de l'ancienne image échoue, mettez quand même à jour la tâche
                                    userDao.update( userItem , this );
                                });
                            } else {
                                // Si aucune ancienne image n'existe, mettre simplement à jour la tâche
                                userDao.update( userItem , this );
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
            }
        } else{
            userDao.update( userItem ,this);
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void openFileChooser() {
        mGetContent.launch("image/*");
    }
    public void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
        itemVisibility.setVisibility(View.GONE);
    }
    public void hideDialog(){
        progressBar.setVisibility(View.GONE);
        itemVisibility.setVisibility(View.VISIBLE);
    }
}