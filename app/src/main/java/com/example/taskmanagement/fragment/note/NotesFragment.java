package com.example.taskmanagement.fragment.note;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.MyAdapterNote;
import com.example.taskmanagement.adapters.VPAdapter;
import com.example.taskmanagement.dao.NoteDao;
import com.example.taskmanagement.fragment.task.HomeRecyclerViewsFragment;
import com.example.taskmanagement.model.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {
    private static final String TAG = "TAGNotesFragment";
    private LinkedList<Note> noteList;
    private RecyclerView myRecycler;
    private FirebaseFirestore db;
    private LinearLayout progressBar;
    private FirebaseUser currentUser;
    private NoteDao noteDao;
    private ImageButton dropdownButton;
    private ViewPager2 viewPager ;
    private VPAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        noteList = new LinkedList<>();
        currentUser = mAuth.getCurrentUser();
        viewPager = requireActivity().findViewById( R.id.viewPager );
        noteDao = new NoteDao( db , mAuth, requireContext() , viewPager );
        adapter = (VPAdapter) viewPager.getAdapter();
        if (adapter!=null)
            adapter.addFragmentBack(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event

                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
                adapter.addFragmentWithPosition( adapter.getSizeBack()-2 );
                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
                viewPager.setCurrentItem( adapter.getItemCount()-1 , false );
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        myRecycler=  view.findViewById(R.id.notes_list_view);
        progressBar = view.findViewById(R.id.progressBar);
        EditText search = view.findViewById(R.id.search);
        Log.d(TAG,"user :"+currentUser.toString());
        dropdownButton = view.findViewById(R.id.dropdownButton);

        dropdownButton.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(getContext(), dropdownButton);
            popupMenu.getMenuInflater().inflate(R.menu.menu_notes, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_new_note) {
//                     Code à exécuter lorsque l'option 1 est sélectionnée
                    NewNoteFragment fragment = new NewNoteFragment();
                    if(adapter!=null){
                        adapter.addFragment(fragment);
                        adapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(adapter.getItemCount() - 1, true);
                    }

                    return true;
                } else if (itemId == R.id.menu_first_tag) {
                    // Code à exécuter lorsque l'option 1 est sélectionnée
//                    FirebaseAuth.getInstance().signOut();
//                    Intent intent = new Intent(TasksActivity.this, AuthActivity.class);
//                    startActivity(intent);
//                    finish();
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "filtres récupérées avec succès : afterTextChanged ");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
                Log.d(TAG, "filtres récupérées avec succès : beforeTextChanged");
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+s.toString());
                LinkedList<Note> filtres= new LinkedList<Note> ();
                for (Note note : noteList ){
                    Log.d(TAG, "filtres récupérées avec succès : onTextChanged "+note);
//                    if (note.getTitle().contains(s) || task.getDescription().contains(s)){
//                        filtres.add(task);
//                    }
//                    Log.d(TAG, "filtres récupérées avec succès : " + filtres);
                    MyAdapterNote myAdapter = new MyAdapterNote(filtres , requireActivity().findViewById(R.id.viewPager));

                    myRecycler.setAdapter(myAdapter);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                    myRecycler.setLayoutManager(layoutManager);
                }
            }
        });

        showDialog();
        fetchDataAndProcess();
        return view;
    }

    private void fetchDataAndProcess() {
        noteDao.getNotes(new NoteDao.OnNotesFetchListener() {
            @Override
            public void onNotesFetchSuccess(LinkedList<Note> notes) {
                Log.d(TAG, "Tâches récupérées avec succès : " + notes);
                noteList = notes;
                hideDialog();
                myRecycler.setHasFixedSize(true);

                MyAdapterNote myAdapter = new MyAdapterNote( notes , requireActivity().findViewById( R.id.viewPager ) );
                myRecycler.setAdapter(myAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                myRecycler.setLayoutManager(layoutManager);

            }

            @Override
            public void onNotesFetchFailure(Exception e) {
                Log.e(TAG, "Erreur lors de la récupération des tâches : ", e);
            }
        });
    }

    private void showDialog(){

        progressBar.setVisibility(View.VISIBLE);
        myRecycler.setVisibility(View.GONE);

    }
    private void hideDialog(){

        progressBar.setVisibility(View.GONE);
        myRecycler.setVisibility(View.VISIBLE);

    }
}