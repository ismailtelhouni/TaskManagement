package com.example.taskmanagement.fragment.note;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.NoteDao;
import com.example.taskmanagement.fragment.event.EditEventFragment;
import com.example.taskmanagement.model.Note;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment implements View.OnClickListener {

    private static final String NOTE_ID = "0";
    private static final String FRAME = "2";
    private static final String TAG = "TAGNoteFragment";
    private EditText notesInputDescription , notesInputTitle;
    private NoteDao noteDao;
    private String noteId , frame;
    private Note oldNote;
    private TextView noteDate;
    private ProgressBar progressBar;
    private RelativeLayout layoutVisibility;
    private ImageButton noteSave;
    private TextWatcher textWatcher;
    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @param frame Parameter 2.
     * @return A new instance of fragment NoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance( String id , String frame ) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putString(NOTE_ID, id);
        args.putString(FRAME, frame);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            noteId = getArguments().getString(NOTE_ID);
            frame = getArguments().getString(FRAME);
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        noteDao = new NoteDao( db , mAuth , requireContext() , requireActivity().getSupportFragmentManager() );

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);


        noteDate = view.findViewById(R.id.notes_date);
        notesInputTitle = view.findViewById(R.id.notes_input_title);
        notesInputDescription = view.findViewById(R.id.notes_input_description);
        progressBar = view.findViewById(R.id.progressBar);
        layoutVisibility = view.findViewById(R.id.layout_visibility);
        noteSave = view.findViewById(R.id.notes_save);
        hideBtnSave();

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                showBtnSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        fetchDataAndProcess();
        notesInputTitle.addTextChangedListener(textWatcher);
        notesInputDescription.addTextChangedListener(textWatcher);
        noteSave.setOnClickListener(this);
        return view;
    }

    private void fetchDataAndProcess() {
        showDialog();
        noteDao.getNote(new NoteDao.OnNoteFetchListener() {
            @Override
            public void onNoteFetchSuccess(Note note) {
                Log.d(TAG,note.toString() + " date : "+ note.getStringDate() + note.getStringTime());

                oldNote = note;

                notesInputTitle.removeTextChangedListener(textWatcher);
                notesInputTitle.setText(note.getTitle());
                notesInputTitle.addTextChangedListener(textWatcher);

                notesInputDescription.removeTextChangedListener(textWatcher);
                notesInputDescription.setText(note.getDescription());
                notesInputDescription.addTextChangedListener(textWatcher);

                String[] split = note.getStringDate().split("\\.");
                String[] splitTime = note.getStringTime().split(":");

                String date = split[0]+" "+split[1]+" "+split[2]+", "+splitTime[0]+":"+splitTime[1];
                noteDate.setText(date);
                if ( note.getPassword() != null ){

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

                                    String password = String.valueOf( passwordEditText.getText() );
                                    if (Objects.equals(note.getPassword(), password)){
                                        hideDialog();
                                        dialogInterface.dismiss();
                                    }else{
                                        Toast.makeText(getContext(), "password incorrect", Toast.LENGTH_SHORT).show();
                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.frame_layout, new NotesFragment());
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    }
                                }
                            })
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_layout, new NotesFragment());
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }
                            })
                            .create();
                    alertDialog.show();
                }else {
                    hideDialog();
                }
            }

            @Override
            public void onNoteFetchFailure(Exception e) {

            }
        },
                noteId
        );

    }
    private void showDialog(){

        progressBar.setVisibility(View.VISIBLE);
        layoutVisibility.setVisibility(View.GONE);

    }
    private void hideDialog(){

        progressBar.setVisibility(View.GONE);
        layoutVisibility.setVisibility(View.VISIBLE);

    }
    private void showBtnSave(){
        noteSave.setVisibility(View.VISIBLE);
    }
    private void hideBtnSave(){
        noteSave.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.notes_save){
            if(oldNote.getTitle().contentEquals(notesInputTitle.getText()) && oldNote.getDescription().contentEquals(notesInputDescription.getText())){

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, NoteFragment.newInstance(noteId , frame));
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            } else{

                oldNote.setTitle(String.valueOf(notesInputTitle.getText()));
                oldNote.setDescription(String.valueOf(notesInputDescription.getText()));
                oldNote.setDate(Timestamp.now());

                noteDao.update(oldNote , noteId);

            }
        }

    }
}