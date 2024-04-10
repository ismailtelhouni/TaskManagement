package com.example.taskmanagement.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.taskmanagement.fragment.task.TaskFragment;

import java.util.ArrayList;

public class VPAdapter extends FragmentStateAdapter {
    private ArrayList<Fragment> arr;

    public VPAdapter( @NonNull FragmentActivity fragmentActivity , ArrayList<Fragment> arr ) {
        super(fragmentActivity);

        this.arr = arr;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) { return arr.get(position); }

    @Override
    public int getItemCount() { return arr.size(); }

    public void addFragment( Fragment fragment) {

        arr.add(fragment);
    }
}
