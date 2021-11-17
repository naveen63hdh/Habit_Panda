package com.example.habitpanda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitpanda.R;
import com.example.habitpanda.models.Habit;
import com.example.habitpanda.models.Task;

import java.util.ArrayList;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.TaskViewHolder> {

    ArrayList<Task> taskArrayList;
    Context context;

    public MyTaskAdapter(ArrayList<Task> taskArrayList, Context context) {
        this.taskArrayList = taskArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TaskViewHolder viewHolder = new TaskViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.nameTxt.setText(taskArrayList.get(position).getTaskName());
        holder.descTxt.setText(taskArrayList.get(position).getTaskDesc());
        holder.dateTxt.setText(taskArrayList.get(position).getTaskDate());
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        //Button actionBtn;
        TextView nameTxt, descTxt, dateTxt;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.task_name);
            descTxt = itemView.findViewById(R.id.task_desc);
            dateTxt = itemView.findViewById(R.id.task_date);

        }
    }
}
