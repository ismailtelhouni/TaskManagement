package com.example.taskmanagement.fragment.event;

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
import com.example.taskmanagement.adapters.MyAdapterEvents;
import com.example.taskmanagement.dao.EventDao;
import com.example.taskmanagement.model.Event;
import com.example.taskmanagement.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;

public class EventFollowFragment extends Fragment {

    private static final String TAG = "TAGEventFollowFragment";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EventDao eventDao;
    private ProgressBar progressBar;
    private RelativeLayout layoutVisibility;
    private EditText search;
    private RecyclerView recycler;
    private LinkedList<Event> eventList;

    public EventFollowFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        eventDao = new EventDao( db , mAuth , requireContext() , requireActivity().getSupportFragmentManager() );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_follow, container, false);

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
                LinkedList<Event> filtres= new LinkedList<Event> ();
                for (Event event : eventList ){
                    Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+event);
                    if (event.getTitle().contains(s) || event.getDescription().contains(s)){
                        filtres.add(event);
                    }
                    Log.d(TAG, "filtres récupérées avec succès : " + filtres);
                    MyAdapterEvents myAdapterEvents = new MyAdapterEvents(filtres,getContext() , requireActivity().getSupportFragmentManager() , "frame_layout_follow");

                    recycler.setAdapter(myAdapterEvents);

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
        eventDao.getEventsFavourite(new EventDao.OnEventsFetchListener() {
            @Override
            public void onEventsFetchSuccess(LinkedList<Event> events) {
                Log.d(TAG, "Tâches récupérées avec succès : " + events);
                eventList = events;
                hideDialog();
                recycler.setHasFixedSize(true);

                MyAdapterEvents myAdapter = new MyAdapterEvents(events, requireContext() , requireActivity().getSupportFragmentManager() , "frame_layout_follow");
                recycler.setAdapter(myAdapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recycler.setLayoutManager(layoutManager);
            }

            @Override
            public void onEventsFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des events : ", e);
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