package com.example.taskmanagement.fragment.event;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

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
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.dao.EventDao;
import com.example.taskmanagement.model.Event;
import com.example.taskmanagement.shared.Utils;
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
    private static final String EVENT_ID = "1";
    private static final String TAG = "TAGEventFragment";
    private String event_id;
    private TextView eventItemTitle , eventItemDate , eventItemDescription , eventItemLieu , eventItemCategory , itemDateRest ;
    private FirebaseUser currentUser;
    private ImageView eventItemStatus , eventItemImage ;
    private ProgressBar progressBar ;
    private LinearLayout btnLayout ;
    private Event eventItem;
    private ViewPager2 viewPager;
    private EventDao eventDao;
    private VPAdapter adapter;
    private RelativeLayout itemData;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param event_id Parameter 1.
     * @return A new instance of fragment EventFragment.
     */
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        viewPager = requireActivity().findViewById(R.id.viewPager);
        eventDao = new EventDao(db, mAuth, getContext() , requireActivity().getSupportFragmentManager() , viewPager );
        adapter = (VPAdapter) viewPager.getAdapter();
        if (adapter!=null)
            adapter.addFragmentBack(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event

                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
                adapter.addFragmentWithPosition( adapter.getSizeBack()-2 );
                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
                viewPager.setCurrentItem( adapter.getItemCount()-1 , true );
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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

                String dateRest = Utils.getDaysUntilStartDate( event.getStartDate() , event.getEndDate() );
                itemDateRest.setText(dateRest);

                if(Objects.equals(dateRest, "Ended")){
                    eventItemStatus.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_finish));
                }else if(Objects.equals(dateRest, "EN_ATTENTE")){
                    eventItemStatus.setColorFilter( ContextCompat.getColor(requireContext(), R.color.color_pending) );
                }else if(Objects.equals(dateRest, "Commenced")){
                    eventItemStatus.setColorFilter(ContextCompat.getColor(requireContext(), R.color.color_court));
                }else {
                    eventItemStatus.setColorFilter( ContextCompat.getColor(requireContext(), R.color.color_pending) );
                }
                Log.d(TAG , " currentUser.getEmail() : "+currentUser.getEmail()+" - event.getEmail() : "+event.getEmail());
                if(!Objects.equals(currentUser.getEmail(), event.getEmail())){
                    btnLayout.setVisibility(View.GONE);
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
        itemDateRest        = view.findViewById(R.id.item_date_rest);

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

            eventDao.delete(event_id, new EventDao.OnEventDeleteListener() {
                @Override
                public void onEventDeleteSuccess() {
//                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.frame_layout, new EventsFragment());
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
                    EventsFragment fragment = new EventsFragment();
                    if(adapter!=null){
                        adapter.addFragment(fragment);
                        adapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(adapter.getItemCount() - 1, false);
                    }
                }

                @Override
                public void onEventDeleteFailure(Exception e) {
                    Log.e(TAG, "Erreur lors de la suppression des tâches : ", e);
                }
            });

        }else if(view.getId()==R.id.btn_edit_event){
            Log.d(TAG,"bien edite");

//            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frame_layout, EditEventFragment.newInstance(event_id));
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            EditEventFragment fragment = EditEventFragment.newInstance(event_id);
            if(adapter!=null){
                adapter.addFragment(fragment);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(adapter.getItemCount() - 1, false);
            }

        }
    }
}