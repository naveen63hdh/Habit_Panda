package com.example.habitpanda.home.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.habitpanda.R;
import com.example.habitpanda.add_habit.AddHabitActivity;
import com.example.habitpanda.add_task.AddTaskActivity;
import com.example.habitpanda.models.Habit;
import com.example.habitpanda.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyTaskFragment extends Fragment {
    FirebaseAuth auth;
    DatabaseReference taskRef;
    ArrayList<Task> taskList;

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

        auth = FirebaseAuth.getInstance();
        taskRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid()).child("Task");
        taskList = new ArrayList<>();

        populateDataset();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddTaskActivity.class);
                startActivity(intent);
            }
        });
        return myView;
    }

    private void populateDataset() {
        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot taskSnap : snapshot.getChildren()) {
                    String key = taskSnap.getKey();

                    Task myTask = new Task();

                    myTask.setTaskId(key);
                    myTask.setTaskName(taskSnap.child("name").getValue().toString());
                    myTask.setTaskDesc(taskSnap.child("desc").getValue().toString());
                    myTask.setTaskDate(taskSnap.child("date").getValue().toString());
                    taskList.add(myTask);
                }

                for (Task myTask:taskList) {
                    Log.i("HABIT_DATA",myTask.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}