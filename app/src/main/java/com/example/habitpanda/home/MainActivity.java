package com.example.habitpanda.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.habitpanda.R;
import com.example.habitpanda.SplashActivity;
import com.example.habitpanda.home.fragment.MyHabitFragment;
import com.example.habitpanda.home.fragment.MyTaskFragment;
import com.example.habitpanda.home.fragment.ProfileFragment;
import com.example.habitpanda.home.fragment.TodayFragment;
import com.example.habitpanda.profile.ProfileActivity;
import com.example.habitpanda.reciever.AlarmReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;

    SharedPreferences spPrivate;
    SharedPreferences.Editor spEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar;

        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color = '#000000'>Habit Panda</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green)));

        spPrivate = getSharedPreferences("panda_pref",MODE_PRIVATE);
        if (spPrivate.getBoolean("rem_on",true)) {
            String time = "";
            time = spPrivate.getString("time","");
            if (time.equals("")) {

                Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMin =  c.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setRemainder(hourOfDay,minute);
                        spEditor.putBoolean("rem_on",true);
                        spEditor.putString("time",hourOfDay+":"+minute);
                        spEditor.putInt("hr",hourOfDay);
                        spEditor.putInt("min",minute);
                        spEditor.commit();
                    }
                },mHour,mMin,true);

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        spEditor.putBoolean("rem_on",false);
                        spEditor.commit();
                    }
                });
                dialog.show();
            }
        }
        spEditor = spPrivate.edit();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.today);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TodayFragment()).commit();
        bottomNavigationView.setOnItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    private void setRemainder(int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND,0);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Sign out?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            spEditor.clear();
                            spEditor.commit();

                            Intent aIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,aIntent,0);
                            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                            am.cancel(pendingIntent);


                            mAuth.signOut();
                            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setTitle("Sign Out");
            alertDialog.show();
        } else if (item.getItemId()==R.id.profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.today:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new TodayFragment()).commit();
                return true;
//            case R.id.profile:
//                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProfileFragment()).commit();
//                return true;
            case R.id.my_habit:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyHabitFragment()).commit();
                return true;
            case R.id.my_task:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyTaskFragment()).commit();
                return true;
        }
        return false;
    }
}