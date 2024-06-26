package com.example.taskmanagement.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.fragment.event.AddEventFragment;
import com.example.taskmanagement.fragment.event.EventFragment;
import com.example.taskmanagement.fragment.event.EventsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.example.taskmanagement.model.Event;

public class EventDao {

    private static final String TAG = "TAGEventDao";
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final ViewPager2 viewPager;
    private final VPAdapter adapter;

    public EventDao(
            FirebaseFirestore db,
            FirebaseAuth mAuth,
            Context context,
            FragmentManager fragmentManager ,
            ViewPager2 viewPager
    ) {
        this.db = db;
        this.currentUser = mAuth.getCurrentUser();
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.viewPager = viewPager;
        this.adapter = (VPAdapter) viewPager.getAdapter();
    }

    public void getEvents( OnEventsFetchListener listener ) {

        if(currentUser.getEmail() != null){

            LinkedList<Event> events = new LinkedList<>();

            CollectionReference userTasksRef = db.collection("events");

            userTasksRef
                    .orderBy("startDate", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Event event = new Event();
                                event.setId(document.getId());
                                event.setTitle(document.getString("title"));
                                event.setDescription(document.getString("description"));
                                event.setStartDate(document.getString("startDate"));
                                event.setEndDate(document.getString("endDate"));
                                event.setCategory(document.getString("category"));
                                event.setImage(document.getString("image"));
                                event.setStatus(document.getString("status"));
                                event.setLieu(document.getString("lieu"));
                                event.setEmail(document.getString("userEmail"));

                                events.add(event);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            Log.d(TAG, "Event récupérées avec succès : " + events);
                            listener.onEventsFetchSuccess(events);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            listener.onEventsFetchFailure(task.getException());
                        }
                    });
        }
    }
    public void save(Event eventModel , AddEventFragment fragment ){

        if(currentUser.getEmail() != null){
            Map<String, Object> event = new HashMap<>();

            event.put("title",eventModel.getTitle());
            event.put("description",eventModel.getDescription());
            event.put("endDate",eventModel.getEndDate());
            event.put("startDate",eventModel.getStartDate());
            event.put("image",eventModel.getImage());
            event.put("category",eventModel.getCategory());
            event.put("lieu",eventModel.getLieu());
            event.put("userEmail",eventModel.getEmail());
            event.put("status","EN_ATTENTE");

            CollectionReference userTasksRef = db.collection("events");

            userTasksRef.add(event)
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(context, "Add Event Success.", Toast.LENGTH_SHORT).show();

                    fragment.hideDialog();
                    Log.d(TAG, "DocumentSnapshot successfully written!");
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.frame_layout, new EventsFragment());
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
                    EventsFragment eventsFragment = new EventsFragment();
                    if(adapter!=null){
                        adapter.addFragment(eventsFragment);
                        adapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
                    }

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }
    }
    public void getEvent( String eventId , OnEventFetchListener listener ){
        if(currentUser.getEmail() != null){
            DocumentReference userTaskRef = db.collection("events").document(eventId);
            userTaskRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Event event = new Event();

                        event.setId(eventId);
                        event.setTitle(document.getString("title"));
                        event.setDescription(document.getString("description"));
                        event.setStartDate(document.getString("startDate"));
                        event.setEndDate(document.getString("endDate"));
                        event.setCategory(document.getString("category"));
                        event.setImage(document.getString("image"));
                        event.setStatus(document.getString("status"));
                        event.setLieu(document.getString("lieu"));
                        event.setEmail(document.getString("userEmail"));

                        listener.onEventFetchSuccess(event);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    listener.onEventFetchFailure(task.getException());
                }
            });
        }
    }
    public void delete( String eventId , OnEventDeleteListener listener){

        if(currentUser.getEmail() != null){
            DocumentReference eventRef = db
                .collection("events")
                .document(eventId);

            eventRef.delete()
                .addOnSuccessListener(aVoid -> listener.onEventDeleteSuccess())
                .addOnFailureListener(listener::onEventDeleteFailure);
        }
    }
    public void update( String event_id , Event eventModel ){

        if(currentUser.getEmail() != null){
            Map<String, Object> event = new HashMap<>();

            event.put("title",eventModel.getTitle());
            event.put("description",eventModel.getDescription());
            event.put("endDate",eventModel.getEndDate());
            event.put("startDate",eventModel.getStartDate());
            event.put("image",eventModel.getImage());
            event.put("category",eventModel.getCategory());
            event.put("lieu",eventModel.getLieu());
            event.put("status",eventModel.getStatus());
            CollectionReference userTasksRef = db.collection("events");
            userTasksRef.document(event_id)
                .update(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "update Event Success.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot successfully written!");
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.frame_layout, EventFragment.newInstance(event_id));
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
                    EventFragment fragment = EventFragment.newInstance(event_id);
                    if(adapter!=null){
                        adapter.addFragment(fragment);
                        adapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }
    }
    public interface OnEventsFetchListener {
        void onEventsFetchSuccess( LinkedList<Event> events );
        void onEventsFetchFailure( Exception e );
    }
    public interface OnEventDeleteListener {
        void onEventDeleteSuccess();
        void onEventDeleteFailure( Exception e );
    }
    public interface OnEventFetchListener {
        void onEventFetchSuccess( Event event );
        void onEventFetchFailure( Exception e );
    }
}
