package com.example.taskmanagement.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.taskmanagement.R;
import com.example.taskmanagement.adapters.VPAdapter;

public class InformationFragment extends Fragment {
    private static final String TAG = "TAGInformationFragment";

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        OnBackPressedCallback callback = new OnBackPressedCallback(true ) {
//            @Override
//            public void handleOnBackPressed() {
//                // Handle the back button event
//
//                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
//                adapter.addFragmentWithPosition( adapter.getSizeBack()-2 );
//                Log.d(TAG , " adapter.getItemCount() : " + adapter.getItemCount() );
//                viewPager.setCurrentItem( adapter.getItemCount()-1 , false );
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_information, container, false);
    }
}