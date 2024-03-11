package com.example.taskmanagement.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.task.TaskFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;
import java.util.Objects;

import com.example.taskmanagement.model.Task;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final LinkedList<Task> tasks;
    private Context context;
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private FragmentManager fragmentManager;

    public MyAdapter(LinkedList<Task> tasks, Context context , FragmentManager fragmentManager ) {
        this.db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.tasks = tasks;
        this.context = context;
        this.fragmentManager = fragmentManager;
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

        String date = task.getStartDate()+" - "+task.getEndDate();
        holder.date.setText(date);

        if(Objects.equals(task.getEtat(), "EN_ATTENTE")){
            holder.box.setChecked(false);
        } else if (Objects.equals(task.getEtat(), "FINISH")) {
            holder.box.setChecked(true);
        }

        holder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            private static final String TAG = "TASK_ITEM";

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

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        public TextView title;
        public TextView date;
        public CheckBox box;
        public Task task;
        public CardView card;

        // Context is a reference to the activity that contain the the recycler view
        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            title   = itemLayoutView.findViewById(R.id.item_title);
            date    = itemLayoutView.findViewById(R.id.item_date);
            box     = itemLayoutView.findViewById(R.id.checkbox);
            card    = itemLayoutView.findViewById(R.id.task_item_card);

        }
        @Override
        public void onClick(View v) {

        }
    }
}
