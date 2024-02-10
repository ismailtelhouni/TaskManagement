package com.example.taskmanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.taskmanagement.R;

import java.util.List;

import model.Task;

public class TaskItemAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Task> tasks;

    public TaskItemAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        if (tasks != null) {
            return tasks.size();
        } else {
            return 0;
        }
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.task_item_adapter,null);

        Task currentTask =getItem(position);
        String title = currentTask.getTitle();
        String description = currentTask.getDescription();
        String startDate = currentTask.getStartDate();
        String endDate = currentTask.getEndDate();

        TextView itemTitle = view.findViewById(R.id.item_title);
        itemTitle.setText(title);

        String date = startDate + " - "+endDate;

        TextView itemDate = view.findViewById(R.id.item_date);
        itemDate.setText(date);

        view.findViewById(R.id.task_item_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return view;
    }
}
