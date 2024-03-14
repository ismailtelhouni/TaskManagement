package com.example.taskmanagement.fragment.event;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.EventDao;
import com.example.taskmanagement.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EVENT_ID = "1";
    private static final String TAG = "EventFragment";

    // TODO: Rename and change types of parameters
    private String event_id;
    private TextView eventItemTitle , eventItemDate , eventItemDescription , eventItemLieu , eventItemCategory ;
    private FirebaseFirestore db ;
    private ImageView eventItemStatus , eventItemImage ;
    private ProgressBar progressBar ;
    private LinearLayout btnLayout ;
    private Event eventItem;
    private FirebaseAuth mAuth;
    private EventDao eventDao;
    private RelativeLayout itemData;

    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event_id Parameter 1.
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance( String event_id ) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString(EVENT_ID, event_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event_id = getArguments().getString(EVENT_ID);
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        eventDao = new EventDao( db , mAuth , getContext() , getActivity().getSupportFragmentManager() );

    }
    private void fetchDataAndProcess(){
        eventDao.getEvent(event_id, new EventDao.OnEventFetchListener() {
            @Override
            public void onEventFetchSuccess(Event event) {

                Log.d(TAG, "Tâches récupérées avec succès : " + event);
                eventItem = event;

                eventItemTitle.setText(event.getTitle());
                eventItemDescription.setText(event.getDescription());
                eventItemLieu.setText(event.getLieu());
                eventItemCategory.setText(event.getCategory());

                Picasso.with(getContext())
                        .load(event.getImage())
                        .into(eventItemImage);

                String date = event.getStartDate()+" - "+event.getEndDate();
                eventItemDate.setText(date);

                if(Objects.equals(event.getStatus(), "FINISH")){
                    eventItemStatus.setColorFilter(R.color.color_finish);
                }else if(Objects.equals(event.getStatus(), "EN_ATTENTE")){
                    eventItemStatus.setColorFilter(R.color.color_pending);
                }else if(Objects.equals(event.getStatus(), "EN_COUR")){
                    eventItemStatus.setColorFilter(R.color.color_court);
                }
                hideDialog();
            }

            @Override
            public void onEventFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération de event : ", e);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        eventItemTitle      = view.findViewById(R.id.item_title);
        eventItemCategory   = view.findViewById(R.id.item_category);
        eventItemDate       = view.findViewById(R.id.item_date);
        eventItemDescription =view.findViewById(R.id.item_description);
        eventItemImage      = view.findViewById(R.id.item_image);
        eventItemLieu       = view.findViewById(R.id.item_lieu);
        eventItemStatus     = view.findViewById(R.id.item_status);

        progressBar         = view.findViewById(R.id.progressBar);
        btnLayout           = view.findViewById(R.id.btn_layout);
        itemData            = view.findViewById(R.id.item_data);
        Button btnEdit = view.findViewById(R.id.btn_edit_event);
        Button btnDelete = view.findViewById(R.id.btn_delete_event);

        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        showDialog();
        fetchDataAndProcess();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

//        if(!Objects.equals(currentUser.getEmail(), eventItem.getEmail())){
//            btnLayout.setVisibility(View.GONE);
//        }
    }
    private void showDialog(){
        progressBar.setVisibility(View.VISIBLE);
        itemData.setVisibility(View.GONE);
    }
    private void hideDialog(){
        progressBar.setVisibility(View.GONE);
        itemData.setVisibility(View.VISIBLE);
    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btn_delete_event){
            Log.d(TAG,"bien delete");
        }else if(view.getId()==R.id.btn_edit_event){
            Log.d(TAG,"bien edite");
        }
    }
}