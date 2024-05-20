package com.example.taskmanagement.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.fragment.event.EventFragment;
import com.example.taskmanagement.fragment.note.NoteFragment;
import com.example.taskmanagement.fragment.note.NotesFragment;
import com.example.taskmanagement.model.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private final FragmentManager fragmentManager;
    public NoteDao(FirebaseFirestore db, FirebaseAuth mAuth, Context context , FragmentManager fragmentManager ) {
        this.db = db;
        this.currentUser = mAuth.getCurrentUser();
        this.context=context;
        this.fragmentManager = fragmentManager;
    }

    public void getNotes(OnNotesFetchListener listener){

        LinkedList<Note> notes = new LinkedList<>();
        String email = currentUser.getEmail();
        if (email != null){

            CollectionReference userNotesRef = db.collection("user").document(email).collection("notes");
            userNotesRef
                .orderBy("date" , Query.Direction.DESCENDING )
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String title = document.getString("title");
                            String description = document.getString("description");
                            Timestamp timestamp = document.getTimestamp("date");
                            Date date = null;
                            if (timestamp != null) {
                                date = timestamp.toDate();
                            }

                            String dateString  = null;
                            String timeString  = null;
                            if (date != null) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
                                String dateTimeString  = dateFormat.format(date);
                                String[] parts = dateTimeString.split(" ");

                                if (parts.length == 2) {
                                    dateString = parts[0];
                                    timeString = parts[1];

                                    Log.d(TAG , "Date: " + dateString);
                                    Log.d(TAG , "Time: " + timeString);
                                } else {
                                    Log.d(TAG , "Format de date-heure invalide");
                                }
                            }
                            Note note = new Note();
                            boolean follow = Boolean.TRUE.equals(document.getBoolean("favourite"));

                            note.setFollow(follow);
                            note.setId(document.getId());
                            note.setTitle(title);
                            note.setDescription(description);
                            note.setDate(timestamp);
                            note.setStringDate(dateString);
                            note.setStringTime(timeString);
                            notes.add(note);
                            Log.d(TAG, document.getId() + " => " + document.getData());

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
    public void getNotesFavourite(OnNotesFetchListener listener){

        LinkedList<Note> notes = new LinkedList<>();
        String email = currentUser.getEmail();
        if (email != null){

            CollectionReference userNotesRef = db.collection("user").document(email).collection("notes");
            userNotesRef
                    .whereEqualTo("favourite",true)
                    .orderBy("date" , Query.Direction.DESCENDING )
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String title = document.getString("title");
                                String description = document.getString("description");
                                Timestamp timestamp = document.getTimestamp("date");
                                Date date = null;
                                if (timestamp != null) {
                                    date = timestamp.toDate();
                                }

                                String dateString  = null;
                                String timeString  = null;
                                if (date != null) {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault());
                                    String dateTimeString  = dateFormat.format(date);
                                    String[] parts = dateTimeString.split(" ");

                                    if (parts.length == 2) {
                                        dateString = parts[0];
                                        timeString = parts[1];

                                        Log.d(TAG , "Date: " + dateString);
                                        Log.d(TAG , "Time: " + timeString);
                                    } else {
                                        Log.d(TAG , "Format de date-heure invalide");
                                    }
                                }
                                Note note = new Note();
                                boolean follow = Boolean.TRUE.equals(document.getBoolean("follow"));

                                note.setFollow(follow);
                                note.setId(document.getId());
                                note.setTitle(title);
                                note.setDescription(description);
                                note.setDate(timestamp);
                                note.setStringDate(dateString);
                                note.setStringTime(timeString);
                                notes.add(note);
                                Log.d(TAG, document.getId() + " => " + document.getData());

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
            note.put("favourite",false);
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
                            String documentId = documentReference.getId();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, NoteFragment.newInstance(documentId , "frame_layout"));
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }
    }
    public void update(Note noteModel , String id){
        if ( currentUser.getEmail() != null ){

            Map<String, Object> note = new HashMap<>();
            note.put("title",noteModel.getTitle());
            note.put("description",noteModel.getDescription());
            note.put("date",noteModel.getDate());
            note.put("favourite",noteModel.isFollow());
            if( noteModel.getPassword() != null ){
                note.put("password",noteModel.getPassword());
            }

            DocumentReference documentReference = db.collection("user").document(currentUser.getEmail()).collection("notes").document(id);

            documentReference.update(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "update Note Success.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, NoteFragment.newInstance(id,"frame_layout"));
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        }
    }
    public void favourite(Note noteModel , String id){
        if ( currentUser.getEmail() != null ){

            Map<String, Object> note = new HashMap<>();
            note.put("favourite",noteModel.isFollow());

            DocumentReference documentReference = db.collection("user").document(currentUser.getEmail()).collection("notes").document(id);

            documentReference.update(note)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(context, "Add to favourite.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        }
    }
    public void getNote( OnNoteFetchListener listener , String id ){

        Note note = new Note();
        String email = currentUser.getEmail();
        if (email!=null){

            DocumentReference userNotesRef = db.collection("user").document(currentUser.getEmail()).collection("notes").document(id);
            userNotesRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Timestamp timestamp = document.getTimestamp("date");
                                    note.setId(id);
                                    Date date = null;
                                    if (timestamp != null) {
                                        date = timestamp.toDate();
                                        note.setDate(timestamp);
                                    }

                                    String dateString  = null;
                                    String timeString  = null;
                                    if (date != null) {
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMM.yyyy HH:mm:ss", Locale.getDefault());
                                        String dateTimeString  = dateFormat.format(date);
                                        String[] parts = dateTimeString.split(" ");
                                        if (parts.length == 2) {
                                            dateString = parts[0];
                                            timeString = parts[1];

                                            Log.d(TAG , "Date: " + dateString);
                                            Log.d(TAG , "Time: " + timeString);
                                            note.setStringDate(dateString);
                                            note.setStringTime(timeString);
                                        } else {
                                            System.out.println("Format de date-heure invalide");
                                        }
                                    }

                                    String title = document.getString("title");
                                    String description = document.getString("description");
                                    boolean follow = Boolean.TRUE.equals(document.getBoolean("favourite"));

                                    note.setFollow(follow);

                                    note.setTitle(title);
                                    note.setDescription(description);
                                    if(document.getString("password")!=null){
                                        note.setPassword(document.getString("password"));
                                    }
                                    listener.onNoteFetchSuccess(note);
                                }
                            }else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                listener.onNoteFetchFailure(task.getException());
                            }
                        }
                    });
        }

    }
    public void delete( String id , OnNoteDeleteListener listener ){
        if(currentUser.getEmail() != null){
            DocumentReference documentReference = db.collection("user").document(currentUser.getEmail()).collection("notes").document(id);
            documentReference.delete()
                    .addOnSuccessListener(aVoid -> listener.onNoteDeleteSuccess())
                    .addOnFailureListener(listener::onNoteDeleteFailure);
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
