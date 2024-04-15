package com.example.taskmanagement.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.VPAdapter;
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

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = "TAGTasksActivity";
    FirebaseAuth mAuth;
    FirebaseUser user;
    ActivityTasksBinding binding;
    ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    ViewPager2 viewPager ;
    private String fragmentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tasks);
        binding = ActivityTasksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        MaterialToolbar toolbar = findViewById(R.id.tool_bar);
        fragmentPage = "HOME";
        viewPager = findViewById(R.id.viewPager);

        fragmentArrayList.add(new HomeRecyclerViewsFragment());
        fragmentArrayList.add(new AddNewTaskFragment());
        fragmentArrayList.add(new EventsFragment());
        fragmentArrayList.add(new NotesFragment());
        fragmentArrayList.add(new SettingsFragment());

        VPAdapter adapter = new VPAdapter(this , fragmentArrayList );
        viewPager.setAdapter(adapter);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.menu_favorite){
                    Toast.makeText(TasksActivity.this , "Vous avez cliqué sur le menu favorite",Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId()==R.id.menu_search) {
                    Toast.makeText(TasksActivity.this , "Vous avez cliqué sur le menu search",Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId()==R.id.menu_new_event) {
                    AddEventFragment fragment = new AddEventFragment();
                    adapter.addFragment(fragment);
                    adapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(adapter.getItemCount() - 1, false);

                    return true;
                } else if (item.getItemId()==R.id.menu_new_note) {

                    NewNoteFragment fragment = new NewNoteFragment();
                    adapter.addFragment(fragment);
                    adapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(adapter.getItemCount() - 1, false);

                    return true;
                } else if (item.getItemId()==R.id.menu_settings) {
                    Toast.makeText(TasksActivity.this , "Vous avez cliqué sur le menu settings",Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                switch (position){
                    case 0:
                        binding.bottomNavigation.setSelectedItemId(R.id.home);
                        fragmentPage = "HOME";
                        break;
                    case 1:
                        binding.bottomNavigation.setSelectedItemId(R.id.add_new_task);
                        fragmentPage = "ADD_NEW_TASK";
                        break;
                    case 2:
                        binding.bottomNavigation.setSelectedItemId(R.id.events);
                        fragmentPage = "EVENTS";
                        break;
                    case 3:
                        binding.bottomNavigation.setSelectedItemId(R.id.notes);
                        fragmentPage = "NOTES";
                        break;
                    case 4:
                        binding.bottomNavigation.setSelectedItemId(R.id.settings);
                        fragmentPage = "SETTINGS";
                        break;
                }
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
                adapter.addFragmentWithPosition( adapter.getSizeBack()-2 );
                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
                viewPager.setCurrentItem( adapter.getItemCount()-1 , false );

//                Log.d(TAG , "tesssssssssssssssssssst"+ fragmentPage );
//
//                switch(fragmentPage){
//                    case "HOME":
//                        viewPager.setCurrentItem(0);
//                        break;
//                    case "ADD_NEW_TASK":
//                        viewPager.setCurrentItem(1);
//                        break;
//                    case "EVENTS":
//                        viewPager.setCurrentItem(2);
//                        break;
//                    case "NOTES":
//                        viewPager.setCurrentItem(3);
//                        break;
//                    case "SETTINGS":
//                        viewPager.setCurrentItem(4);
//                        break;
//                }
            }
        });

        user = mAuth.getCurrentUser();
//        replaceFragment(new HomeRecyclerViewsFragment());
        viewPager.setCurrentItem(0);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
//
//            switch(fragmentPage){
//                case "HOME":
//                    adapter.addFragmentBack(new HomeRecyclerViewsFragment());
//                    break;
//                case "ADD_NEW_TASK":
//                    adapter.addFragmentBack(new AddNewTaskFragment());
//                    break;
//                case "EVENTS":
//                    adapter.addFragmentBack(new EventsFragment());
//                    break;
//                case "NOTES":
//                    adapter.addFragmentBack(new NotesFragment());
//                    break;
//                case "SETTINGS":
//                    adapter.addFragmentBack(new SettingsFragment());
//                    break;
//            }

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

//        dropdownButton.setOnClickListener(v->{
//            PopupMenu popupMenu = new PopupMenu(TasksActivity.this, dropdownButton);
//            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
//            popupMenu.setOnMenuItemClickListener(item -> {
//                int itemId = item.getItemId();
//                if (itemId == R.id.menu_item1) {
//                    // Code à exécuter lorsque l'option 1 est sélectionnée
//                    FirebaseAuth.getInstance().signOut();
//                    Intent intent = new Intent(TasksActivity.this, AuthActivity.class);
//                    startActivity(intent);
//                    finish();
//                    return true;
//                } else if (itemId == R.id.menu_item2) {
//                    // Code à exécuter lorsque l'option 1 est sélectionnée
//                    FirebaseAuth.getInstance().signOut();
//                    Intent intent = new Intent(TasksActivity.this, AuthActivity.class);
//                    startActivity(intent);
//                    finish();
//                    return true;
//                } else {
//                    return false;
//                }
//            });
//            popupMenu.show();
//        });
    }

}