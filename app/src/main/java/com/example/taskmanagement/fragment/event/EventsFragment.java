package com.example.taskmanagement.fragment.event;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.MyAdapter;
import com.example.taskmanagement.adapters.MyAdapterEvents;
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.dao.EventDao;
import com.example.taskmanagement.dao.TaskDao;
import com.example.taskmanagement.fragment.task.EditTaskFragment;
import com.example.taskmanagement.model.Event;
import com.example.taskmanagement.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "EventsFragment";
    private LinkedList<Event> eventList;
    private RecyclerView myRecycler;
    private LinearLayout progressBar;
    private EventDao eventDao;
    private ViewPager2 viewPager;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        eventList = new LinkedList<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        viewPager = requireActivity().findViewById(R.id.viewPager);
        eventDao = new EventDao(db, mAuth, getContext(), requireActivity().getSupportFragmentManager() , viewPager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        ImageButton btnAddEvent = view.findViewById(R.id.btn_add_event);
        myRecycler=  view.findViewById(R.id.events_list_view);
        progressBar = view.findViewById(R.id.progressBar);
        EditText search = view.findViewById(R.id.search);
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
                LinkedList<Event> filtres= new LinkedList<Event> ();
                for (Event event : eventList ){
                    Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+event);
                    if (event.getTitle().contains(s) || event.getDescription().contains(s)){
                        filtres.add(event);
                    }
                    Log.d(TAG, "filtres récupérées avec succès : " + filtres);
                    MyAdapterEvents myAdapterEvents = new MyAdapterEvents(filtres,getContext(),requireActivity().getSupportFragmentManager() , requireActivity().findViewById(R.id.viewPager));

                    myRecycler.setAdapter(myAdapterEvents);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    myRecycler.setLayoutManager(layoutManager);
                }
            }
        });

        btnAddEvent.setOnClickListener(this);
        showDialog();
        fetchDataAndProcess();
        return view;
    }

    private void fetchDataAndProcess() {
        eventDao.getEvents(new EventDao.OnEventsFetchListener() {
            @Override
            public void onEventsFetchSuccess(LinkedList<Event> events) {
                Log.d(TAG, "Tâches récupérées avec succès : " + events);
                eventList = events;
                hideDialog();
                myRecycler.setHasFixedSize(true);

                MyAdapterEvents myAdapterEvents = new MyAdapterEvents(events,getContext(),requireActivity().getSupportFragmentManager() , requireActivity().findViewById( R.id.viewPager ));
                myRecycler.setAdapter(myAdapterEvents);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                myRecycler.setLayoutManager(layoutManager);
            }
            @Override
            public void onEventsFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : ", e);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_add_event ){
//            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frame_layout, new AddEventFragment());
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            VPAdapter adapter = (VPAdapter) viewPager.getAdapter();
            AddEventFragment fragment = new AddEventFragment();
            if(adapter!=null){
                adapter.addFragment(fragment);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
            }


        }
    }
    private void showDialog(){

        progressBar.setVisibility(View.VISIBLE);
        myRecycler.setVisibility(View.GONE);

    }
    private void hideDialog(){

        progressBar.setVisibility(View.GONE);
        myRecycler.setVisibility(View.VISIBLE);

    }
}