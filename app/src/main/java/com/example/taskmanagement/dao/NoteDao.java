package com.example.taskmanagement.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.fragment.note.NoteFragment;
import com.example.taskmanagement.model.Note;
import com.example.taskmanagement.model.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class NoteDao {

    private static final String TAG = "TAGNoteDao";
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private final Context context;
    private ViewPager2 viewPager;
    private VPAdapter adapter;
    public NoteDao(FirebaseFirestore db, FirebaseAuth mAuth, Context context , ViewPager2 viewPager) {
        this.db = db;
        this.currentUser = mAuth.getCurrentUser();
        this.context=context;
        this.viewPager = viewPager;
        this.adapter = (VPAdapter) viewPager.getAdapter();
    }

    public void getNotes(OnNotesFetchListener listener){

        LinkedList<Note> notes = new LinkedList<>();
        String email = currentUser.getEmail();
        if (email != null){

            CollectionReference userNotesRef = db.collection("user").document(email).collection("notes");
            userNotesRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String title = document.getString("title");
//                            String description = document.getString("description");
//
//                            Timestamp timestamp = document.getTimestamp("date");
//                            Date date = null;
//                            if (timestamp != null) {
//                                date = timestamp.toDate();
//                            }
//
//                            String dateString  = null;
//                            String timeString  = null;
//                            if (date != null) {
//                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
//                                String dateTimeString  = dateFormat.format(date);
//                                String[] parts = dateTimeString.split(" ");
//
//                                if (parts.length == 2) {
//                                    dateString = parts[0];
//                                    timeString = parts[1];
//
//                                    System.out.println("Date: " + dateString);
//                                    System.out.println("Time: " + timeString);
//                                } else {
//                                    System.out.println("Format de date-heure invalide");
//                                }
//                            }
//
//
//                            String etat = document.getString("etat");
//                            com.example.taskmanagement.model.Task task1 = new Task();
//                            task1.setId(document.getId());
//                            task1.setTitle(title);
//                            task1.setDescription(description);
//                            task1.setDate(dateString);
//                            task1.setTime(timeString);
//                            task1.setDoc_url(document.getString("doc_url"));
//                            task1.setImg(document.getString("img"));
//                            task1.setEtat(etat);
//
//                            tasks.add(task1);
//                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        Log.d(TAG, "Tâches récupérées avec succès : " + notes);
                        listener.onNotesFetchSuccess(notes);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        listener.onNotesFetchFailure(task.getException());
                    }
                });
        }

    }

    public void save(Note noteModel ) {
        if ( currentUser.getEmail() != null ){
            Map<String, Object> note = new HashMap<>();

            note.put("title",noteModel.getTitle());
            note.put("description",noteModel.getDescription());
            note.put("date",noteModel.getDate());
            if( noteModel.getPassword() != null ){
                note.put("password",noteModel.getPassword());
            }

            CollectionReference userNotesRef = db.collection("user").document(currentUser.getEmail()).collection("notes");

            userNotesRef.add(note)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, "Add Note Success.", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            NoteFragment fragment = new NoteFragment();
                            if(adapter!=null){
                                adapter.addFragment(fragment);
                                adapter.notifyDataSetChanged();
                                viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }
    }

    public interface OnNotesFetchListener {
        void onNotesFetchSuccess(LinkedList<Note> notes);
        void onNotesFetchFailure(Exception e);
    }
    public interface OnNoteDeleteListener {
        void onNoteDeleteSuccess();
        void onNoteDeleteFailure(Exception e);
    }
    public interface OnNoteFetchListener {
        void onNoteFetchSuccess(Note note);
        void onNoteFetchFailure(Exception e);
    }

}
