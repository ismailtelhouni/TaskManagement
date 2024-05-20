package com.example.taskmanagement.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.event.AddEventFragment;
import com.example.taskmanagement.fragment.event.EventsFragment;
import com.example.taskmanagement.fragment.note.NewNoteFragment;
import com.example.taskmanagement.fragment.note.NotesFragment;
import com.example.taskmanagement.fragment.task.AddNewTaskFragment;
import com.example.taskmanagement.fragment.task.HomeRecyclerViewsFragment;
import com.example.taskmanagement.fragment.SettingsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.taskmanagement.databinding.ActivityTasksBinding;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = "TAGTasksActivity";
    FirebaseAuth mAuth;
    FirebaseUser user;
    ActivityTasksBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tasks);
        binding = ActivityTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        MaterialToolbar toolbar = findViewById(R.id.tool_bar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.menu_favorite){
                    Intent intent = new Intent( getApplicationContext() , FollowActivity.class );
                    startActivity(intent);
                    return true;
                } else if (item.getItemId()==R.id.menu_new_event) {
                    AddEventFragment fragment = new AddEventFragment();
                    replaceFragment(fragment , true);

                    return true;
                } else if (item.getItemId()==R.id.menu_new_note) {

                    NewNoteFragment fragment = new NewNoteFragment();
                    replaceFragment(fragment , true);

                    return true;
                } else if (item.getItemId()==R.id.menu_settings) {
                    Toast.makeText(TasksActivity.this , "Vous avez cliqué sur le menu settings",Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        toolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });

        user = mAuth.getCurrentUser();
        replaceFragment(new HomeRecyclerViewsFragment() , false);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if(itemId == R.id.home){
                replaceFragment(new HomeRecyclerViewsFragment() , true);

            } else if( itemId == R.id.add_new_task ){
                replaceFragment(new AddNewTaskFragment() , true);
            } else if(itemId == R.id.settings){
                replaceFragment(new SettingsFragment() , true );
            } else if(itemId == R.id.events){
                replaceFragment(new EventsFragment() , true );
            } else if(itemId == R.id.notes ){
                replaceFragment(new NotesFragment() , true);
            }
            return true;
        });

        if(user == null){
            Intent intent = new Intent(getApplicationContext(),AuthActivity.class);
            startActivity(intent);
            finish();
        }

    }
    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}