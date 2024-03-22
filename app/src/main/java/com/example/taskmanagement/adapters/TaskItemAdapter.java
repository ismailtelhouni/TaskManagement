package com.example.taskmanagement.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.task.TaskFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

import com.example.taskmanagement.model.Task;

public class TaskItemAdapter extends BaseAdapter{

    private final LayoutInflater inflater;
    private final List<Task> tasks;
    private final FragmentManager fragmentManager;
    private final FirebaseFirestore db;

    public TaskItemAdapter(Context context, List<Task> tasks, FragmentManager fragmentManager) {
        this.inflater = LayoutInflater.from(context);
        this.tasks = tasks;
        this.fragmentManager = fragmentManager;
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public int getCount() {
        if (tasks != null) {
            return tasks.size();
        } else {
            return 0;
        }
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.task_item_adapter,null);

        Task currentTask =getItem(position);
        String title = currentTask.getTitle();
        String description = currentTask.getDescription();
        String date = currentTask.getDate();
        String time = currentTask.getTime();

        TextView itemTitle = view.findViewById(R.id.item_title);
        itemTitle.setText(title);

        CheckBox checkBox = view.findViewById(R.id.checkbox);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            private static final String TAG = "TASK_ITEM";

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Traitement à effectuer lorsque la case est cochée ou décochée
                if (isChecked) {

                    db.collection("tasks").document(currentTask.getId())
                            .update("etat","FINISH")
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

                } else {

                    db.collection("tasks").document(currentTask.getId())
                            .update("etat","EN_COUR")
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                            .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));


                }
            }
        });


        CardView cardView = view.findViewById(R.id.task_item_card);

        if(Objects.equals(currentTask.getEtat(), "EN_ATTENTE")){
            checkBox.setChecked(false);
        } else if (Objects.equals(currentTask.getEtat(), "FINISH")) {
            checkBox.setChecked(true);
        }
        String dateItem = date + " - "+time;

        TextView itemDate = view.findViewById(R.id.item_date);
        itemDate.setText(dateItem);
        cardView.setOnClickListener(v -> {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, TaskFragment.newInstance(currentTask.getId()));
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return view;
    }
}
