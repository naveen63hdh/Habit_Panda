package com.example.habitpanda.add_habit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.habitpanda.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddHabitActivity extends AppCompatActivity {
    SeekBar endTimeSeek;
    TextView endTimeTxt;
    RadioGroup typeGrp;
    LinearLayout timerLayout;
    EditText nameTxt, descTxt, hhTxt, mmTxt, ssTxt;
    CheckBox monCB, tueCB, wedCB, thurCB, friCB, satCB, sunCB;

    String habitName, habitDesc, time;
    int habitType, days, hh=0, mm=0, ss=0;
    boolean mon, tue, wed, thur, fri, sat, sun;

    boolean dateCompleted = false, habitCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        ActionBar actionBar;

        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color = '#000000'>Add Habit</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green)));

        nameTxt = findViewById(R.id.habit_name);
        descTxt = findViewById(R.id.habit_desc);
        typeGrp = findViewById(R.id.habit_type);
        timerLayout = findViewById(R.id.timerLayout);
        endTimeSeek = findViewById(R.id.endTimeSeekbar);
        endTimeTxt = findViewById(R.id.endTimeTxt);
        hhTxt = findViewById(R.id.timer_hh);
        mmTxt = findViewById(R.id.timer_mm);
        ssTxt = findViewById(R.id.timer_ss);
        monCB = findViewById(R.id.habit_mon);
        tueCB = findViewById(R.id.habit_tues);
        wedCB = findViewById(R.id.habit_wed);
        thurCB = findViewById(R.id.habit_thurs);
        friCB = findViewById(R.id.habit_fri);
        satCB = findViewById(R.id.habit_sat);
        sunCB = findViewById(R.id.habit_sun);


        endTimeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                endTimeTxt.setText("Habit End Time : " + progress + " days");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        typeGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.manual) {
                    if (timerLayout.getVisibility() == View.VISIBLE)
                        timerLayout.setVisibility(View.GONE);
                } else {
                    if (timerLayout.getVisibility() == View.GONE)
                        timerLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void createHabit(View view) {
        ProgressDialog progressDialog = ProgressDialog.show(this, "Please Wait", "Creating your Habit");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference habitRef = database.getReference().child("Users").child(auth.getUid()).child("Habit").push();
        String key = habitRef.getKey();
        habitName = nameTxt.getText().toString();
        habitDesc = descTxt.getText().toString();

        switch (typeGrp.getCheckedRadioButtonId()) {
            case R.id.manual:
                habitType = 0;
                break;
            case R.id.timer:
                habitType = 1;
                break;
        }
        if (habitType == 1) {
            if (!hhTxt.getText().toString().equals(""))
                hh = Integer.parseInt(hhTxt.getText().toString());
            if (!mmTxt.getText().toString().equals(""))
                mm = Integer.parseInt(mmTxt.getText().toString());
            if (!ssTxt.getText().toString().equals(""))
                ss = Integer.parseInt(ssTxt.getText().toString());
            time = hh + ":" + mm + ":" + ss;
        }
        days = endTimeSeek.getProgress();
        mon = monCB.isChecked();
        tue = tueCB.isChecked();
        wed = wedCB.isChecked();
        thur = thurCB.isChecked();
        fri = friCB.isChecked();
        sat = satCB.isChecked();
        sun = sunCB.isChecked();


        DatabaseReference habitDateRef = database.getReference().child("Users").child(auth.getUid()).child("HabitDate");
        Calendar c = Calendar.getInstance();
        int count = 0;

        HashMap<String,HashMap<String,String>> dateMap = new HashMap<>();


        while (count < days) {
            int day = c.get(Calendar.DAY_OF_WEEK);
            if (isSelectedDay(day)) {
                HashMap<String,String> dummyMap = new HashMap<>();
                Date date = c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
                String myDate = sdf.format(date);
                habitDateRef.child(myDate).child(key).child("is_completed").setValue(false);
                dummyMap.put("Date",myDate);
                dateMap.put(myDate,dummyMap);
                count++;
            }
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        HashMap<String, Object> habit = new HashMap<>();
//        habit.put()

        habit.put("name", habitName);
        habit.put("desc", habitDesc);
        habit.put("type", habitType);
        if (habitType == 1)
            habit.put("time", time);
//        habit.put("Calendar",calendarDate);
        habit.put("days", days);
        habit.put("date", dateMap);
        habit.put("mon", mon);
        habit.put("tue", tue);
        habit.put("wed", wed);
        habit.put("thur", thur);
        habit.put("fri", fri);
        habit.put("sat", sat);
        habit.put("sun", sun);

        habitRef.updateChildren(habit).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(AddHabitActivity.this, "Habit Created Successfully", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddHabitActivity.this, "Please check your network connectivity and try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isSelectedDay(int day) {
        switch (day) {
            case 1:
                if (sun)
                    return true;
                break;
            case 2:
                if (mon)
                    return true;
                break;
            case 3:
                if (tue)
                    return true;
                break;
            case 4:
                if (wed)
                    return true;
                break;
            case 5:
                if (thur)
                    return true;
                break;
            case 6:
                if (fri)
                    return true;
                break;
            case 7:
                if (sat)
                    return true;
                break;
        }
        return false;
    }
}