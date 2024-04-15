package com.example.taskmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.taskmanagement.R;
import com.example.taskmanagement.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;

public class MyAdapterNote extends RecyclerView.Adapter<MyAdapterNote.MyViewHolder>{
    private static final String TAG = "TAGMyAdapterNote";
    private final LinkedList<Note> notes;
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private final ViewPager2 viewPager;

    public MyAdapterNote( LinkedList<Note> notes , ViewPager2 viewPager ) {
        this.notes = notes;
        this.db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        this.currentUser = mAuth.getCurrentUser();
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public MyAdapterNote.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_adapter, parent, false);

        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterNote.MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
