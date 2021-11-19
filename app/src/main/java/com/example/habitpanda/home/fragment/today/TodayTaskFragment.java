package com.example.habitpanda.home.fragment.today;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.habitpanda.R;
import com.example.habitpanda.adapters.TodayHabitAdapter;
import com.example.habitpanda.adapters.TodayTaskAdapter;
import com.example.habitpanda.models.Habit;
import com.example.habitpanda.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodayTaskFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference taskReference, dateReference;
    FirebaseAuth auth;

    ArrayList<Task> taskList;
    String today;

    RecyclerView taskRecycler;
    TextView emptyText;
    ProgressDialog progressDialog;
    long size = 0;


    public TodayTaskFragment() {
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
        View view = inflater.inflate(R.layout.fragment_today_task, container, false);
        auth = FirebaseAuth.getInstance();

        taskRecycler = view.findViewById(R.id.contentRecycler);
        emptyText = view.findViewById(R.id.empty);

        taskRecycler.setHasFixedSize(true);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        taskRecycler.setItemAnimator(new DefaultItemAnimator());


        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
        today = sdf.format(date);

        database = FirebaseDatabase.getInstance();
        taskReference = database.getReference().child("Users").child(auth.getUid()).child("Task");
        dateReference = database.getReference().child("Users").child(auth.getUid()).child("Task Date");

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(getContext(), "Please wait", "Loading Today's tasks");
        taskList = new ArrayList<>();
        populateDatabase();
    }

    private void populateDatabase() {
        dateReference.child(today).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = snapshot.getChildrenCount();
                if (size<=0) {
                    progressDialog.dismiss();
                    emptyText.setVisibility(View.VISIBLE);
                    taskRecycler.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    taskRecycler.setVisibility(View.VISIBLE);
                }

                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    String key = dateSnap.getKey();
                    Task myTask = new Task();
                    myTask.setTaskId(key);
                    taskReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot taskSnap) {
                            myTask.setTaskName(taskSnap.child("name").getValue().toString());
                            myTask.setTaskDesc(taskSnap.child("desc").getValue().toString());
                            myTask.setTaskDate(taskSnap.child("date").getValue().toString());
                            taskList.add(myTask);
                            if (size == taskList.size()) {
                                Log.i("TASK_DATA", taskList.toString());
                                TodayTaskAdapter habitAdapter = new TodayTaskAdapter(taskList, getContext());
                                taskRecycler.setAdapter(habitAdapter);
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}