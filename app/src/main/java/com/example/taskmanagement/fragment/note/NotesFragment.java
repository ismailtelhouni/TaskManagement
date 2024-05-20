package com.example.taskmanagement.fragment.note;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.MyAdapterNote;
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.dao.NoteDao;
import com.example.taskmanagement.fragment.event.EventsFragment;
import com.example.taskmanagement.model.Note;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {
    private static final String TAG = "TAGNotesFragment";
    private LinkedList<Note> noteList;
    private RecyclerView myRecycler;
    private LinearLayout progressBar;
    private FirebaseUser currentUser;
    private NoteDao noteDao;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        noteList = new LinkedList<>();
        currentUser = mAuth.getCurrentUser();
        noteDao = new NoteDao(db, mAuth, requireContext() , requireActivity().getSupportFragmentManager());

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
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        myRecycler=  view.findViewById(R.id.notes_list_view);
        progressBar = view.findViewById(R.id.progressBar);
        EditText search = view.findViewById(R.id.search);
        Log.d(TAG,"user :"+currentUser.toString());
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "filtres récupérées avec succès : afterTextChanged ");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
                Log.d(TAG, "filtres récupérées avec succès : beforeTextChanged");
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+s.toString());
                LinkedList<Note> filtres= new LinkedList<Note> ();
                for (Note note : noteList ){
                    Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+note);
                    if (note.getTitle().contains(s) || note.getDescription().contains(s)){
                        filtres.add(note);
                    }
                    Log.d(TAG, "filtres récupérées avec succès : " + filtres);
                    MyAdapterNote myAdapter = new MyAdapterNote(filtres , requireActivity().getSupportFragmentManager() , requireContext());

                    myRecycler.setAdapter(myAdapter);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    myRecycler.setLayoutManager(layoutManager);
                }
            }
        });
        showDialog();
        fetchDataAndProcess();
//        myRecycler.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    View childView = myRecycler.findChildViewUnder(e.getX(), e.getY());
//                    if (childView != null ) {
//                        int position = myRecycler.getChildAdapterPosition(childView);
//
//                        showDeleteAlert(position);
//
//                        Log.d(TAG,"loooooooooooooooooong presss");
//                    }
//                }
//
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    View childView = myRecycler.findChildViewUnder(e.getX(), e.getY());
//                    if (childView != null ) {
//                        int position = myRecycler.getChildAdapterPosition(childView);
//                        // Ouvrir le fragment de détail pour l'élément à cette position
//                        Note note = noteList.get(position);
//                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.frame_layout, NoteFragment.newInstance(note.getId()));
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
//                        return true;
//                    }
//                    return false;
//                }
//            });
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//                View childView = rv.findChildViewUnder(e.getX(), e.getY());
//                return childView != null && gestureDetector.onTouchEvent(e);
//            }
//
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
        return view;
    }

    private void fetchDataAndProcess() {
        noteDao.getNotes(new NoteDao.OnNotesFetchListener() {
            @Override
            public void onNotesFetchSuccess(LinkedList<Note> notes) {
                Log.d(TAG, "Tâches récupérées avec succès : " + notes);
                noteList = notes;
                hideDialog();
                myRecycler.setHasFixedSize(true);

                MyAdapterNote myAdapter = new MyAdapterNote( notes , requireActivity().getSupportFragmentManager() , requireContext());
                myRecycler.setAdapter(myAdapter);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myRecycler.getContext(), DividerItemDecoration.VERTICAL);
                Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider);
                if (drawable != null) {
                    dividerItemDecoration.setDrawable(drawable);
                }
                myRecycler.addItemDecoration(dividerItemDecoration);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                myRecycler.setLayoutManager(layoutManager);

            }

            @Override
            public void onNotesFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : ", e);
            }
        });
    }

    private void showDialog(){

        progressBar.setVisibility(View.VISIBLE);
        myRecycler.setVisibility(View.GONE);

    }
    private void hideDialog(){

        progressBar.setVisibility(View.GONE);
        myRecycler.setVisibility(View.VISIBLE);

    }
//    private void showDeleteAlert(final int position){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//        String[] options = {"Supprimer", "Modifier"};
//        VPAdapter adapter = new VPAdapter(getContext(), R.layout.custom_dialog_item, options);
//
//        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        // Action de suppression
//                        deleteItem(position);
//                        break;
//                    case 1:
//                        // Action de modification
//                        editItem(position);
//                        break;
//                }
//            }
//        });
//        AlertDialog dialog = builder.create();
//
//        // Obtenir les paramètres de la fenêtre de la boîte de dialogue
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        Window window = dialog.getWindow();
//        if (window != null) {
//            layoutParams.copyFrom(window.getAttributes());
//            layoutParams.width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
//            window.setAttributes(layoutParams);
//        }
//
//        dialog.show();
//
//    }

//    private void editItem(int position) {
//
//        Note note = noteList.get(position);
//
//
//    }

//    private void deleteItem(final int position){
//
//        Note note = noteList.get(position);
//        AlertDialog alertDialog = new MaterialAlertDialogBuilder( requireContext() )
//                .setTitle("Delete")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        noteDao.delete(note.getId(), new NoteDao.OnNoteDeleteListener() {
//                            @Override
//                            public void onNoteDeleteSuccess() {
//                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                fragmentTransaction.replace(R.id.frame_layout, new NotesFragment());
//                                fragmentTransaction.addToBackStack(null);
//                                fragmentTransaction.commit();
//                            }
//
//                            @Override
//                            public void onNoteDeleteFailure(Exception e) {
//                                Log.e(TAG, "Erreur lors de la suppression des tâches : ", e);
//                            }
//                        });
//                    }
//                })
//                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .create();
//        alertDialog.show();
//
//    }



}