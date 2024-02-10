package com.example.taskmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;

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


        dropdownButton = findViewById(R.id.dropdownButton);
        mAuth = FirebaseAuth.getInstance();
        //button = findViewById(R.id.logout);
        user = mAuth.getCurrentUser();
        replaceFragment(new HomeFragment());
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home){
                replaceFragment(new HomeFragment());
            } else if( itemId == R.id.add_new_task ){
                replaceFragment(new AddNewTaskFragment());
            } else if(itemId == R.id.settings){
                replaceFragment(new SettingsFragment());
            }
            return true;
        });

        if(user == null){
            Intent intent = new Intent(getApplicationContext(),AuthActivity.class);
            startActivity(intent);
            finish();
        }

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