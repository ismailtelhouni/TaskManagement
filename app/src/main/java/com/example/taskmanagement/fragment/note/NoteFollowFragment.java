package com.example.taskmanagement.fragment.note;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.MyAdapter;
import com.example.taskmanagement.adapters.MyAdapterNote;
import com.example.taskmanagement.dao.NoteDao;
import com.example.taskmanagement.model.Note;
import com.example.taskmanagement.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;

public class NoteFollowFragment extends Fragment {

    private static final String TAG = "TAGNoteFollowFragment";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private NoteDao noteDao;
    private ProgressBar progressBar;
    private RelativeLayout layoutVisibility;
    private EditText search;
    private RecyclerView recycler;
    private LinkedList<Note> noteList;
    public NoteFollowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        noteDao = new NoteDao( db , mAuth , requireContext() , requireActivity().getSupportFragmentManager() );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_follow, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        layoutVisibility = view.findViewById(R.id.layout_visibility);
        search = view.findViewById(R.id.search);
        recycler = view.findViewById(R.id.recycle);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                LinkedList<Note> filtres= new LinkedList<Note> ();
                for (Note note : noteList ){
                    Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+note);
                    if (note.getTitle().contains(s) || note.getDescription().contains(s)){
                        filtres.add(note);
                    }
                    Log.d(TAG, "filtres récupérées avec succès : " + filtres);
                    MyAdapterNote myAdapter = new MyAdapterNote(filtres , requireActivity().getSupportFragmentManager() , requireContext());

                    recycler.setAdapter(myAdapter);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recycler.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        showDialog();
        fetchDataAndProcess();

        return view;
    }

    private void fetchDataAndProcess() {
        noteDao.getNotesFavourite(new NoteDao.OnNotesFetchListener() {
            @Override
            public void onNotesFetchSuccess(LinkedList<Note> notes) {
                Log.d(TAG, "Tâches récupérées avec succès : " + notes);
                noteList = notes;
                hideDialog();
                recycler.setHasFixedSize(true);

                MyAdapterNote myAdapter = new MyAdapterNote(notes, requireActivity().getSupportFragmentManager() , requireContext() );
                recycler.setAdapter(myAdapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recycler.setLayoutManager(layoutManager);
            }

            @Override
            public void onNotesFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des notes : ", e);
            }
        });
    }
    private void showDialog(){

        progressBar.setVisibility(View.VISIBLE);
        layoutVisibility.setVisibility(View.GONE);

    }
    private void hideDialog(){

        progressBar.setVisibility(View.GONE);
        layoutVisibility.setVisibility(View.VISIBLE);

    }
}