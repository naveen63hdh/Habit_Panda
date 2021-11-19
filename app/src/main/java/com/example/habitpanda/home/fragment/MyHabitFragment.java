package com.example.habitpanda.home.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.habitpanda.adapters.MyHabitAdapter;
import com.example.habitpanda.add_habit.AddHabitActivity;
import com.example.habitpanda.models.Habit;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyHabitFragment extends Fragment {
    FirebaseAuth auth;
    DatabaseReference habitRef;
    ArrayList<Habit> habitList;

    FloatingActionButton addBtn;

    RecyclerView habitRecycler;
    TextView emptyText;
    ProgressDialog progressDialog;

    public MyHabitFragment() {
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
        View myView = inflater.inflate(R.layout.fragment_my_habit, container, false);
        addBtn = myView.findViewById(R.id.add_habit);
        habitRecycler = myView.findViewById(R.id.contentRecycler);
        emptyText = myView.findViewById(R.id.empty);

        habitRecycler.setHasFixedSize(true);
        habitRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        habitRecycler.setItemAnimator(new DefaultItemAnimator());

        auth = FirebaseAuth.getInstance();
        habitRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.getUid()).child("Habit");


        //populateDataset();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddHabitActivity.class);
                startActivity(intent);
            }
        });
        return myView;
    }

    private void populateDataset() {
        habitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnap : snapshot.getChildren()) {

                    String key = dataSnap.getKey();
//                    for (DataSnapshot dataSnap : habitSnap.getChildren())
                    Habit myHabit = new Habit();
//                    DataSnapshot dataSnap = habitSnap.child(key);
                    myHabit.setId(key);

                    myHabit.setHabitName(dataSnap.child("name").getValue().toString());
                    myHabit.setHabitDesc(dataSnap.child("desc").getValue().toString());
                    myHabit.setHabitType(Integer.parseInt(dataSnap.child("type").getValue().toString()));
                    if (myHabit.getHabitType() == 1)
                        myHabit.setTime(dataSnap.child("time").getValue().toString());
                    myHabit.setDays(Integer.parseInt(dataSnap.child("days").getValue().toString()));
                    myHabit.setMon(Boolean.parseBoolean(dataSnap.child("mon").getValue().toString()));
                    myHabit.setTue(Boolean.parseBoolean(dataSnap.child("tue").getValue().toString()));
                    myHabit.setWed(Boolean.parseBoolean(dataSnap.child("wed").getValue().toString()));
                    myHabit.setThur(Boolean.parseBoolean(dataSnap.child("thur").getValue().toString()));
                    myHabit.setFri(Boolean.parseBoolean(dataSnap.child("fri").getValue().toString()));
                    myHabit.setSat(Boolean.parseBoolean(dataSnap.child("sat").getValue().toString()));
                    myHabit.setSun(Boolean.parseBoolean(dataSnap.child("sun").getValue().toString()));
                    habitList.add(myHabit);
                }

                for (Habit myHabit : habitList) {
                    Log.i("HABIT_DATA", myHabit.toString());
                }

                MyHabitAdapter habitAdapter = new MyHabitAdapter(habitList, getContext());
                habitRecycler.setAdapter(habitAdapter);
                progressDialog.dismiss();
                if (habitList.size() <= 0) {
                    emptyText.setVisibility(View.VISIBLE);
                    habitRecycler.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    habitRecycler.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("LOG_TAG", "Resume");
        progressDialog = ProgressDialog.show(getContext(), "Please wait", "Loading your habits");
        habitList = new ArrayList<>();
        populateDataset();
    }
}