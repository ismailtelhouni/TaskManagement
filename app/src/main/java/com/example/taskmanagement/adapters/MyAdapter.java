package com.example.taskmanagement.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.taskmanagement.R;
import com.example.taskmanagement.dao.NoteDao;
import com.example.taskmanagement.dao.TaskDao;
import com.example.taskmanagement.fragment.note.NotesFragment;
import com.example.taskmanagement.fragment.task.HomeRecyclerViewsFragment;
import com.example.taskmanagement.fragment.task.TaskFragment;
import com.example.taskmanagement.model.Note;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.Objects;

import com.example.taskmanagement.model.Task;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private static final String TAG = "TAGMyAdapter";
    private final LinkedList<Task> tasks;
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private final FragmentManager fragmentManager;
    private final TaskDao taskDao;
    private final Context context;


    public MyAdapter(LinkedList<Task> tasks , FragmentManager fragmentManager , Context context ) {
        this.db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.tasks = tasks;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.taskDao = new TaskDao( db , mAuth , context , fragmentManager );
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_adapter, parent, false);

        return new MyViewHolder(itemLayoutView);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Task task = tasks.get(position);
        holder.title.setText(task.getTitle());

        String description = task.getDescription();
        Log.d(TAG,"description : "+description);
        if (description.length() > 20) {
            description = description.substring(0, 20)+"...";
        }
        holder.description.setText(description);

//        String dateRest = Utils.getDaysUntilStartDate( task.getStartDate() , task.getEndDate() );
//        holder.date.setText(dateRest);

        if(Objects.equals(task.getEtat(), "EN_ATTENTE")){
            holder.box.setChecked(false);
        } else if (Objects.equals(task.getEtat(), "FINISH")) {
            holder.box.setChecked(true);
        }

        holder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DocumentReference userTasksRef = db.collection("user").document(currentUser.getEmail()).collection("tasks").document(task.getId());
                // Traitement à effectuer lorsque la case est cochée ou décochée
                if (isChecked) {
                    userTasksRef
                        .update("etat","FINISH")
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

                } else {
                    userTasksRef
                        .update("etat","EN_COUR")
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                        .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                }
            }
        });
        holder.card.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, TaskFragment.newInstance(task.getId()));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        holder.card.setOnLongClickListener(view -> {

            holder.card.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4E7F5")));

            PopupMenu popupMenu = showMenu(view , task);

            return true;
        });

    }
    private PopupMenu showMenu(View view, Task task) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        String favouriteEtat = "Favourite";
        if( task.isFavourite()  ){
            favouriteEtat = "Unfavourite";
        }
        popupMenu.getMenu().add(favouriteEtat);
        popupMenu.getMenu().add("Delete");
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getTitle().toString()) {
                case "Favourite":
                case "Unfavourite":
                    favouriteItem(task);
                    return true;
                case "Delete":
                    deleteItem(task);
                    return true;
                default:
                    return false;
            }
        });

        return popupMenu;
    }
    private void deleteItem(Task task) {

        AlertDialog alertDialog = new MaterialAlertDialogBuilder( context )
                .setTitle("Delete")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        taskDao.delete(task.getId(), new TaskDao.OnTaskDeleteListener() {
                            @Override
                            public void onTaskDeleteSuccess() {
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.frame_layout, new HomeRecyclerViewsFragment());
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }

                            @Override
                            public void onTaskDeleteFailure(Exception e) {
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

    private void favouriteItem(Task task) {

        task.setFavourite( !task.isFavourite() );
        taskDao.favourite( task.getId() , task );

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView title , date , description;
        public CheckBox box;
        public CardView card;

        // Context is a reference to the activity that contain the the recycler view
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            title   = itemLayoutView.findViewById(R.id.item_title);
            date    = itemLayoutView.findViewById(R.id.item_date);
            box     = itemLayoutView.findViewById(R.id.checkbox);
            card    = itemLayoutView.findViewById(R.id.task_item_card);
            description = itemLayoutView.findViewById(R.id.item_description);

        }
        @Override
        public void onClick(View v) {

        }
    }
}
