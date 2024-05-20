package com.example.taskmanagement.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.taskmanagement.dao.NoteDao;
import com.example.taskmanagement.fragment.event.AddEventFragment;
import com.example.taskmanagement.fragment.event.EventFragment;
import com.example.taskmanagement.fragment.note.NoteFragment;
import com.example.taskmanagement.fragment.note.NotesFragment;
import com.example.taskmanagement.model.Note;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

public class MyAdapterNote extends RecyclerView.Adapter<MyAdapterNote.MyViewHolder>{
    private static final String TAG = "TAGMyAdapterNote";
    private final LinkedList<Note> notes;
    private final FragmentManager fragmentManager;
    private final Context context;
    private final NoteDao noteDao;
    private final String frame;

    public MyAdapterNote( LinkedList<Note> notes , FragmentManager fragmentManager , Context context , String frame ) {
        this.notes = notes;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.noteDao = new NoteDao( FirebaseFirestore.getInstance() , FirebaseAuth.getInstance() , context , fragmentManager );
        this.frame = frame;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_adapter, parent, false);
        return new MyAdapterNote.MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Note note = notes.get(position);
//        Log.d(TAG,"tttttttttttt");
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        try {
            Date date = dateFormat.parse( note.getStringDate() );
//            Log.d( TAG , "teetetetetete :"+date );
            Date today = new Date();
//            Log.d( TAG , "teetetetetete :"+today );
            Calendar calDate = Calendar.getInstance();
            calDate.setTime(date);
            Calendar calToday = Calendar.getInstance();
            calToday.setTime(today);
            if (calDate.get(Calendar.YEAR) == calToday.get(Calendar.YEAR) &&
                    calDate.get(Calendar.MONTH) == calToday.get(Calendar.MONTH) &&
                    calDate.get(Calendar.DAY_OF_MONTH) == calToday.get(Calendar.DAY_OF_MONTH) ) {
                // Les dates sont les mêmes, afficher le temps
                DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String hourAndMinute = note.getStringTime().substring(0, 5);
                // Utilisez la variable "time" pour afficher le temps où vous en avez besoin
                Log.d(TAG, "time: " + hourAndMinute);
                holder.date.setText(hourAndMinute);
            } else {
                // Les dates sont différentes, afficher la date sous forme de "jour mois"
                DateFormat dayMonthFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
                String dayMonthString = dayMonthFormat.format(date);
                holder.date.setText(dayMonthString);
                // Utilisez la variable "dayMonthString" pour afficher la date sous forme de "jour mois"
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.title.setText(note.getTitle());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                if (Objects.equals(frame, "frame_layout_follow")){
                    fragmentTransaction.replace(R.id.frame_layout_follow, NoteFragment.newInstance( note.getId() , frame ));
                }else if (Objects.equals(frame, "frame_layout")){
                    fragmentTransaction.replace(R.id.frame_layout, NoteFragment.newInstance( note.getId() , frame ));
                }
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
        holder.card.setOnLongClickListener(view -> {

            holder.card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4E7F5")));

            PopupMenu popupMenu = showMenu(view , note);

            return true;
        });
    }

    private PopupMenu showMenu(View view, Note note) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        String followEtat = "Favourite";
        if( note.isFollow()  ){
            followEtat = "Unfavourite";
        }
        popupMenu.getMenu().add(followEtat);
        popupMenu.getMenu().add("Delete");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getTitle().toString()) {
                case "Favourite":
                case "Unfavourite":
                    favouriteItem(note);
                    return true;
                case "Delete":
                    deleteItem(note);
                    return true;
                default:
                    return false;
            }
        });

        return popupMenu;
    }

    private void deleteItem(Note note) {

        AlertDialog alertDialog = new MaterialAlertDialogBuilder( context )
            .setTitle("Delete")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    noteDao.delete(note.getId(), new NoteDao.OnNoteDeleteListener() {
                        @Override
                        public void onNoteDeleteSuccess() {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_layout, new NotesFragment());
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }

                        @Override
                        public void onNoteDeleteFailure(Exception e) {
                            Log.e(TAG, "Erreur lors de la suppression des tâches : ", e);
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

    private void favouriteItem(Note note) {

        note.setFollow( !note.isFollow() );
        noteDao.favourite( note , note.getId() );

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title , date;
        public RelativeLayout card;
        // Context is a reference to the activity that contain the the recycler view
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            title   = itemLayoutView.findViewById(R.id.item_title);
            date    = itemLayoutView.findViewById(R.id.item_date);
            card    = itemLayoutView.findViewById(R.id.item_card);

        }
        @Override
        public void onClick(View v) {

        }
    }
}
