package com.example.taskmanagement.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.fragment.InformationFragment;
import com.example.taskmanagement.fragment.event.AddEventFragment;
import com.example.taskmanagement.fragment.event.EditEventFragment;
import com.example.taskmanagement.fragment.event.EventFragment;
import com.example.taskmanagement.fragment.event.EventsFragment;
import com.example.taskmanagement.fragment.note.NotesFragment;
import com.example.taskmanagement.fragment.task.AddNewTaskFragment;
import com.example.taskmanagement.fragment.task.EditTaskFragment;
import com.example.taskmanagement.fragment.task.HomeRecyclerViewsFragment;
import com.example.taskmanagement.fragment.SettingsFragment;
import com.example.taskmanagement.fragment.task.TaskFragment;
import com.example.taskmanagement.fragment.user.ChangePasswordFragment;
import com.example.taskmanagement.fragment.user.EditeProfileFragment;
import com.example.taskmanagement.fragment.user.ForgetPasswordFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.taskmanagement.databinding.ActivityTasksBinding;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    ImageButton dropdownButton;
    ActivityTasksBinding binding;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    ViewPager2 viewPager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tasks);
        binding = ActivityTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        viewPager = findViewById(R.id.viewPager);
//        TabLayout tabLayout = findViewById(R.id.tabLayout);

        fragmentArrayList.add(new HomeRecyclerViewsFragment());     // 0
        fragmentArrayList.add(new AddNewTaskFragment());            // 1
        fragmentArrayList.add(new EventsFragment());                // 2
        fragmentArrayList.add(new NotesFragment());                 // 3
        fragmentArrayList.add(new SettingsFragment());              // 4
//        fragmentArrayList.add(new TaskFragment());                  // 5
//        fragmentArrayList.add(new EditTaskFragment());              // 6
//        fragmentArrayList.add(new AddEventFragment());              // 7
//        fragmentArrayList.add(new EditEventFragment());             // 8
//        fragmentArrayList.add(new EventFragment());                 // 9
//        fragmentArrayList.add(new ChangePasswordFragment());        // 10
//        fragmentArrayList.add(new EditeProfileFragment());          // 11
//        fragmentArrayList.add(new InformationFragment());           // 12

        dropdownButton = findViewById(R.id.dropdownButton);
        VPAdapter adapter = new VPAdapter(this , fragmentArrayList );
        viewPager.setAdapter(adapter);

//        new TabLayoutMediator(tabLayout, viewPager,
//                (tab, position) -> tab.setText("Tab " + (position + 1))
//        ).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position){
                    case 0:
                        binding.bottomNavigation.setSelectedItemId(R.id.home);
                        break;
                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.add_new_task);
                        break;
                    case 2:
                        binding.bottomNavigation.setSelectedItemId(R.id.events);
                        break;
                    case 3:
                        binding.bottomNavigation.setSelectedItemId(R.id.notes);
                        break;
                    case 4:
                        binding.bottomNavigation.setSelectedItemId(R.id.settings);
                        break;

                }
            }
        });

        user = mAuth.getCurrentUser();
//        replaceFragment(new HomeRecyclerViewsFragment());
        viewPager.setCurrentItem(0);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home){
//                replaceFragment(new HomeRecyclerViewsFragment());
                viewPager.setCurrentItem(0);
            } else if( itemId == R.id.add_new_task ){
//                replaceFragment(new AddNewTaskFragment());
                viewPager.setCurrentItem(1);
            } else if(itemId == R.id.settings){
//                replaceFragment(new SettingsFragment());
                viewPager.setCurrentItem(4);
            } else if(itemId == R.id.events){
//                replaceFragment(new EventsFragment());
                viewPager.setCurrentItem(2);
            } else if(itemId == R.id.notes ){
//                replaceFragment(new NotesFragment() );
                viewPager.setCurrentItem(3);
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
//    private void replaceFragment(Fragment fragment){
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame_layout,fragment);
//        fragmentTransaction.commit();
//
//    }

}