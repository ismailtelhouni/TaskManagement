package com.example.taskmanagement.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.event.EventsFragment;
import com.example.taskmanagement.fragment.note.NotesFragment;
import com.example.taskmanagement.fragment.task.AddNewTaskFragment;
import com.example.taskmanagement.fragment.task.HomeRecyclerViewsFragment;
import com.example.taskmanagement.fragment.SettingsFragment;
import com.example.taskmanagement.fragment.user.ForgetPasswordFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.taskmanagement.databinding.ActivityTasksBinding;

public class TasksActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    ImageButton dropdownButton;
    ActivityTasksBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tasks);
        binding = ActivityTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        dropdownButton = findViewById(R.id.dropdownButton);

        user = mAuth.getCurrentUser();
        replaceFragment(new HomeRecyclerViewsFragment());
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home){
                replaceFragment(new HomeRecyclerViewsFragment());
            } else if( itemId == R.id.add_new_task ){
                replaceFragment(new AddNewTaskFragment());
            } else if(itemId == R.id.settings){
                replaceFragment(new SettingsFragment());
            } else if(itemId == R.id.events){
                replaceFragment(new EventsFragment());
            } else if(itemId == R.id.notes ){
                replaceFragment(new NotesFragment() );
            }
            return true;
        });

        if(user == null){
            Intent intent = new Intent(getApplicationContext(),AuthActivity.class);
            startActivity(intent);
            finish();
        }

//        if (getIntent().getBooleanExtra("navigate_to_forget_password", false)) {
//
//            getSupportFragmentManager().beginTransaction()
//                .replace(R.id.frame_layout, new ForgetPasswordFragment())
//                .commit();
//        }

        dropdownButton.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(TasksActivity.this, dropdownButton);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_item1) {
                    // Code à exécuter lorsque l'option 1 est sélectionnée
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(TasksActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.menu_item2) {
                    // Code à exécuter lorsque l'option 1 est sélectionnée
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(TasksActivity.this, AuthActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });
    }
    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }

}