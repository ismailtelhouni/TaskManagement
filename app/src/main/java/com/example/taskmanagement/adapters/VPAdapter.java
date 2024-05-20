package com.example.taskmanagement.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.taskmanagement.R;
import com.example.taskmanagement.fragment.task.TaskFragment;

import java.util.ArrayList;

public class VPAdapter extends ArrayAdapter<String> {
    private static final String TAG = "TAGVPAdapter";
    private Context context;
    private String[] options;

    public VPAdapter( Context context, int resource, String[] options ) {
        super(context, resource, options);
        this.context = context;
        this.options = options;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.custom_dialog_item, parent, false);
        }

        TextView textView = view.findViewById(R.id.textView);

        textView.setText(options[position]);

        return view;
    }

}
