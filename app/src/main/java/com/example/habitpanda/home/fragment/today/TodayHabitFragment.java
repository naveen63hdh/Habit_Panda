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

import com.example.habitpanda.R;
import com.example.habitpanda.adapters.TodayHabitAdapter;
import com.example.habitpanda.models.Habit;
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

public class TodayHabitFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference habitReference, dateReference;
    FirebaseAuth auth;

    ArrayList<Habit> habitList;
    String today;

    RecyclerView habitRecycler;
    ProgressDialog progressDialog;
    long size = 0;


    public TodayHabitFragment() {
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
        View view = inflater.inflate(R.layout.fragment_today_habit, container, false);

        auth = FirebaseAuth.getInstance();

        habitRecycler = view.findViewById(R.id.contentRecycler);

        habitRecycler.setHasFixedSize(true);
        habitRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        habitRecycler.setItemAnimator(new DefaultItemAnimator());


        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
        today = sdf.format(date);

        database = FirebaseDatabase.getInstance();
        habitReference = database.getReference().child("Users").child(auth.getUid()).child("Habit");
        dateReference = database.getReference().child("Users").child(auth.getUid()).child("HabitDate");

        return view;
    }

    private void populateDatabase() {
        dateReference.child(today).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = snapshot.getChildrenCount();
                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    Log.i("HABIT_ID", dateSnap.getKey());
                    Habit myHabit = new Habit();
                    String id = dateSnap.getKey();
                    myHabit.setId(id);
                    myHabit.setIsCompleted((Boolean) dateSnap.child("is_completed").getValue());
                    habitReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnap) {
                            Log.i("HABIT_Data", dataSnap.child("name").getValue().toString());
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
                            if (size == habitList.size()) {
                                Log.i("HABIT_DATA", habitList.toString());
                                TodayHabitAdapter habitAdapter = new TodayHabitAdapter(habitList, getContext());
                                habitRecycler.setAdapter(habitAdapter);
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

    @Override
    public void onResume() {
        super.onResume();
        progressDialog = ProgressDialog.show(getContext(), "Please wait", "Loading Today's habits");
        habitList = new ArrayList<>();
        populateDatabase();
    }

}