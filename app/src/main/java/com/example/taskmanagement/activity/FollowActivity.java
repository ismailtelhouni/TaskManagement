package com.example.taskmanagement.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskmanagement.R;
import com.example.taskmanagement.databinding.ActivityFollowBinding;
import com.example.taskmanagement.databinding.ActivityTasksBinding;
import com.example.taskmanagement.fragment.event.AddEventFragment;
import com.example.taskmanagement.fragment.event.EventFollowFragment;
import com.example.taskmanagement.fragment.note.NewNoteFragment;
import com.example.taskmanagement.fragment.note.NoteFollowFragment;
import com.example.taskmanagement.fragment.task.HomeRecyclerViewsFragment;
import com.example.taskmanagement.fragment.task.TaskFollowFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FollowActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TAGFollowActivity";
    FirebaseAuth mAuth;
    FirebaseUser user;
    @NonNull ActivityFollowBinding binding;
    Button tasksFollowBtn , eventsFollowBtn, notesFollowBtn ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_follow);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.followActivity), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        binding = ActivityFollowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        MaterialToolbar toolbar = findViewById(R.id.tool_bar);
        tasksFollowBtn = findViewById(R.id.btn_task);
        eventsFollowBtn = findViewById(R.id.btn_event);
        notesFollowBtn = findViewById(R.id.btn_note);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId()==R.id.menu_new_event) {
                AddEventFragment fragment = new AddEventFragment();
                replaceFragment(fragment);

                return true;
            } else if (item.getItemId()==R.id.menu_new_note) {

                NewNoteFragment fragment = new NewNoteFragment();
                replaceFragment(fragment);

                return true;
            } else if (item.getItemId()==R.id.menu_settings) {
//                    Toast.makeText(this , "Vous avez cliqué sur le menu settings",Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        toolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent( this , TasksActivity.class );
            startActivity(intent);
        });

        tasksFollowBtn.setOnClickListener(this);
        eventsFollowBtn.setOnClickListener(this);
        notesFollowBtn.setOnClickListener(this);

        replaceFragment(new TaskFollowFragment());
    }
    private void replaceFragment( Fragment fragment ) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_follow, fragment );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_task){
            replaceFragment(new TaskFollowFragment());

            // Change style for "Tasks" button
            tasksFollowBtn.setTextColor(getResources().getColor(R.color.primary));
            tasksFollowBtn.setBackgroundColor(getResources().getColor(R.color.primaryBg));
            tasksFollowBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryBg)));

            // Reset style for "Events" button
            eventsFollowBtn.setTextColor(getResources().getColor(R.color.gray));
            eventsFollowBtn.setBackgroundColor(getResources().getColor(android.R.color.white));

            // Reset style for "Notes" button
            notesFollowBtn.setTextColor(getResources().getColor(R.color.gray));
            notesFollowBtn.setBackgroundColor(getResources().getColor(android.R.color.white));

        } else if (view.getId()==R.id.btn_event) {
            replaceFragment(new EventFollowFragment());

            // Reset style for "Tasks" button
            tasksFollowBtn.setTextColor(getResources().getColor(R.color.gray));
            tasksFollowBtn.setBackgroundColor(getResources().getColor(android.R.color.white));

            // Change style for "Events" button
            eventsFollowBtn.setTextColor(getResources().getColor(R.color.primary));
            eventsFollowBtn.setBackgroundColor(getResources().getColor(R.color.primaryBg));
            eventsFollowBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryBg)));

            // Reset style for "Notes" button
            notesFollowBtn.setTextColor(getResources().getColor(R.color.gray));
            notesFollowBtn.setBackgroundColor(getResources().getColor(android.R.color.white));

        } else if (view.getId()==R.id.btn_note) {
            replaceFragment(new NoteFollowFragment());

            // Reset style for "Tasks" button
            tasksFollowBtn.setTextColor(getResources().getColor(R.color.gray));
            tasksFollowBtn.setBackgroundColor(getResources().getColor(android.R.color.white));

            // Reset style for "Events" button
            eventsFollowBtn.setTextColor(getResources().getColor(R.color.gray));
            eventsFollowBtn.setBackgroundColor(getResources().getColor(android.R.color.white));

            // Change style for "Notes" button
            notesFollowBtn.setTextColor(getResources().getColor(R.color.primary));
            notesFollowBtn.setBackgroundColor(getResources().getColor(R.color.primaryBg));
            notesFollowBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryBg)));


        }
    }
}