package com.example.taskmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.event.EventFragment;
import com.example.taskmanagement.fragment.task.TaskFragment;
import com.example.taskmanagement.model.Event;
import com.example.taskmanagement.model.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class MyAdapterEvents extends RecyclerView.Adapter<MyAdapterEvents.MyViewHolder>{

    private static final String TAG = "TAGMyAdapterEvents";
    private final LinkedList<Event> events;
    private final Context context;
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private final FragmentManager fragmentManager;
    private final ViewPager2 viewPager;

    public MyAdapterEvents(LinkedList<Event> events, Context context, FragmentManager fragmentManager , ViewPager2 viewPager ) {
        this.events = events;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item_adapter, parent, false);

        return new MyAdapterEvents.MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Event event = events.get(position);
        holder.title.setText(event.getTitle());
        holder.lieu.setText(event.getLieu());
        String date = event.getStartDate()+" - "+event.getEndDate();
        holder.date.setText(date);

        Picasso.with(context)
            .load(event.getImage())
            .into(holder.image);

        holder.card.setOnClickListener(view -> {
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.viewPager, EventFragment.newInstance(event.getId()));
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            VPAdapter adapter = (VPAdapter) viewPager.getAdapter();
            EventFragment fragment = EventFragment.newInstance(event.getId());
            if(adapter!=null){
                adapter.addFragment(fragment);
                adapter.notifyDataSetChanged();
                viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView title , lieu , date ;
        public Event event;
        public RelativeLayout card;
        public ImageView image;

        // Context is a reference to the activity that contain the the recycler view
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            title   = itemLayoutView.findViewById(R.id.item_title);
            date    = itemLayoutView.findViewById(R.id.item_date);
            lieu    = itemLayoutView.findViewById(R.id.item_lieu);
            image   = itemLayoutView.findViewById(R.id.item_image);
            card    = itemLayoutView.findViewById(R.id.item_card);

        }
        @Override
        public void onClick(View v) {

        }
    }
}
