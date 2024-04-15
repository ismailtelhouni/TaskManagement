package com.example.taskmanagement.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.taskmanagement.fragment.task.TaskFragment;

import java.util.ArrayList;

public class VPAdapter extends FragmentStateAdapter {
    private static final String TAG = "TAGVPAdapter";
    private final ArrayList<Fragment> arr;
    private final ArrayList<Fragment> listBack;

    public VPAdapter( @NonNull FragmentActivity fragmentActivity , ArrayList<Fragment> arr ) {
        super(fragmentActivity);

        this.arr = arr;
        this.listBack=new ArrayList<>();

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) { return arr.get(position); }

    @Override
    public int getItemCount() { return arr.size(); }

    public void addFragment( Fragment fragment) {

        arr.add(fragment);
    }

    public void addFragmentBack( Fragment fragment) {

        listBack.add(fragment);
    }

    public int getSizeBack() { return listBack.size(); }


    public void addFragmentWithPosition( int position ) {

        Log.d(TAG , " fragment : " + listBack.get(position) );
        arr.add(listBack.get(position));
    }

}
