package com.example.taskmanagement.fragment.note;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.dao.EventDao;
import com.example.taskmanagement.dao.NoteDao;
import com.example.taskmanagement.model.Note;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewNoteFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TAGNewNoteFragment";
    private CheckBox notesPassword;
    private EditText notesInputDescription , notesInputTitle;
    private NoteDao noteDao;
    private String password;
    public NewNoteFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        noteDao = new NoteDao( db , mAuth , requireContext() , requireActivity().getSupportFragmentManager() );
        password = null;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_note, container, false);

        ImageButton notesSave = view.findViewById(R.id.notes_save);
        TextView notesDate = view.findViewById(R.id.notes_date);
        notesPassword = view.findViewById(R.id.notes_password);
        notesInputTitle = view.findViewById(R.id.notes_input_title);
        notesInputDescription = view.findViewById(R.id.notes_input_description);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy , HH:mm", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        Log.d(TAG, "Formatted Date: " + formattedDate);

        notesDate.setText(formattedDate);


        notesSave.setOnClickListener(this);
        notesPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked){
                    View view1 = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout , null );
                    TextInputEditText passwordEditText = view1.findViewById(R.id.password);

                    int color = ContextCompat.getColor( requireContext() , R.color.primaryBg);
                    Drawable backgroundDrawable = new ColorDrawable(color);

                    AlertDialog alertDialog = new MaterialAlertDialogBuilder( requireContext() )
                            .setTitle("Password")
                            .setView(view1)
                            .setBackground(backgroundDrawable)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d(TAG , "password is :"+String.valueOf( passwordEditText.getText() ));
                                    password = String.valueOf( passwordEditText.getText() );
                                }
                            })
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    notesPassword.setChecked(false);
                                    password = null ;
                                }
                            })
                            .create();
                    alertDialog.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()== R.id.notes_save){

            Note note = new Note();

            note.setTitle( String.valueOf( notesInputTitle.getText() ) );
            note.setDescription( String.valueOf( notesInputDescription.getText() ) );
            if(password != null){
                note.setPassword(password);
            }
            note.setDate(Timestamp.now());

            Log.d(TAG , " la resultat esa lkmldknca : " + note);

            noteDao.save(note);

        }
    }
}