package com.example.taskmanagement.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.EventDao;
import com.example.taskmanagement.dao.NoteDao;
import com.example.taskmanagement.fragment.event.EventFragment;
import com.example.taskmanagement.fragment.event.EventsFragment;
import com.example.taskmanagement.fragment.note.NotesFragment;
import com.example.taskmanagement.model.Event;
import com.example.taskmanagement.model.Note;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

public class MyAdapterEvents extends RecyclerView.Adapter<MyAdapterEvents.MyViewHolder>{

    private static final String TAG = "TAGMyAdapterEvents";
    private final LinkedList<Event> events;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final EventDao eventDao;

    public MyAdapterEvents(LinkedList<Event> events, Context context , FragmentManager fragmentManager ) {
        this.events = events;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.eventDao = new EventDao( FirebaseFirestore.getInstance() , FirebaseAuth.getInstance() , context , fragmentManager );
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
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, EventFragment.newInstance(event.getId()));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        holder.card.setOnLongClickListener(view -> {

            holder.card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4E7F5")));

            PopupMenu popupMenu = showMenu(view , event);

            return true;
        });
    }
    private PopupMenu showMenu(View view, Event event) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        String followEtat = "Favourite";
        if( event.isFavourite()  ){
            followEtat = "Unfavourite";
        }
        popupMenu.getMenu().add(followEtat);
        popupMenu.getMenu().add("Delete");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getTitle().toString()) {
                case "Favourite":
                case "Unfavourite":
                    favouriteItem(event);
                    return true;
                case "Delete":
                    deleteItem(event);
                    return true;
                default:
                    return false;
            }
        });

        return popupMenu;
    }
    private void deleteItem(Event event) {

        AlertDialog alertDialog = new MaterialAlertDialogBuilder( context )
                .setTitle("Delete")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eventDao.delete(event.getId(), new EventDao.OnEventDeleteListener() {
                            @Override
                            public void onEventDeleteSuccess() {
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.frame_layout, new EventsFragment());
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }

                            @Override
                            public void onEventDeleteFailure(Exception e) {
                                Log.e(TAG, "Erreur lors de la suppression du event : ", e);
                            }
                        });
                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();

    }

    private void favouriteItem(Event event) {

        event.setFavourite( !event.isFavourite() );
        eventDao.favourite( event.getId() , event );

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
