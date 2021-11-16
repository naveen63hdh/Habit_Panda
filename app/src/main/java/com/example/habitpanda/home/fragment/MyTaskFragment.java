package com.example.habitpanda.home.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.habitpanda.R;
import com.example.habitpanda.add_habit.AddHabitActivity;
import com.example.habitpanda.add_task.AddTaskActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyTaskFragment extends Fragment {


    FloatingActionButton addBtn;
    public MyTaskFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =  inflater.inflate(R.layout.fragment_my_task, container, false);
        addBtn = myView.findViewById(R.id.add_task);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTaskActivity.class);
                startActivity(intent);
            }
        });
        return myView;
    }
}