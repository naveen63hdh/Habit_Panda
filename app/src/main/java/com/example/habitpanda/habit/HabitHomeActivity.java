package com.example.habitpanda.habit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.DrawableMarginSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.habitpanda.R;
import com.example.habitpanda.home.MainActivity;
import com.example.habitpanda.models.Habit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.vo.DateData;

public class HabitHomeActivity extends AppCompatActivity {

    //    MCalendarView calendarView;
    MaterialCalendarView calendarView;
    String code, uid, name;

    ArrayList<CalendarDay> completedList, notCompletedList;
    ArrayList<String> dateList;

    FirebaseDatabase database;
    DatabaseReference reference, dateRef;
    FirebaseAuth auth;

    DataSnapshot dateSnap;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_home);

        code = getIntent().getExtras().getString("code");
        name = getIntent().getExtras().getString("name");
        auth = FirebaseAuth.getInstance();
        uid = auth.getUid();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color = '#000000'>"+name+"</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green)));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow);


        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Users").child(uid).child("Habit").child(code);
        dateRef = database.getReference().child("Users").child(uid).child("HabitDate");

        calendarView = findViewById(R.id.habitCalendar);

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);

//        calendarDays = new ArrayList<>();

//        LocalDate localDate = LocalDate.of(2020,11,6);
//        CalendarDay calendarDay = CalendarDay.from(2021,12,6);
//        calendarDays.add(calendarDay);

    }


    @Override
    protected void onResume() {
        super.onResume();

        progressDialog = ProgressDialog.show(this,"Please Wait","Generating your calendar");
        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dateSnap = snapshot;
                dateList = new ArrayList<>();
                completedList = new ArrayList<>();
                notCompletedList = new ArrayList<>();
                populateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        else if (item.getItemId() == R.id.delete_btn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure about deleting this habit")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteHabit();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setTitle("Delete Habit");
            alertDialog.show();
//            deleteHabit();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteHabit() {
        ProgressDialog dialog = ProgressDialog.show(this, "Please wait", "Deleting the habit");
        for (String date : dateList) {
            dateRef.child(date).child(code).removeValue();
            if (date.equals(dateList.get(dateList.size() - 1)))
                dateRef.child(date).child(code).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(HabitHomeActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    Intent intent = new Intent(HabitHomeActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                });
        }
    }

    private void populateList() {
        reference.child("date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    String date = snap.getKey();
                    Date d = getDate(date);
                    SimpleDateFormat dddf = new SimpleDateFormat("dd", Locale.US);
                    SimpleDateFormat mmdf = new SimpleDateFormat("MM", Locale.US);
                    SimpleDateFormat yydf = new SimpleDateFormat("yyyy", Locale.US);

                    int dd = Integer.parseInt(dddf.format(d));
                    int MM = Integer.parseInt(mmdf.format(d));
                    int yy = Integer.parseInt(yydf.format(d));

                    CalendarDay cd = CalendarDay.from(yy, MM, dd);

                    dateList.add(date);
                    if (Boolean.parseBoolean(dateSnap.child(date).child(code).child("is_completed").getValue().toString())) {
                        completedList.add(cd);
                    } else {
                        notCompletedList.add(cd);
                    }
                }

                calendarView.addDecorators(new DayViewDecorator() {
                    @Override
                    public boolean shouldDecorate(CalendarDay day) {
                        return completedList.contains(day);
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        view.setBackgroundDrawable(AppCompatResources.getDrawable(HabitHomeActivity.this, R.drawable.date_completed));
                    }
                });

                calendarView.addDecorators(new DayViewDecorator() {
                    @Override

                    public boolean shouldDecorate(CalendarDay day) {
                        return notCompletedList.contains(day);
                    }

                    @Override
                    public void decorate(DayViewFacade view) {
                        Drawable drawable = AppCompatResources.getDrawable(HabitHomeActivity.this, R.drawable.date_not_completed);
                        view.setSelectionDrawable(drawable);
                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Date getDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}