package com.example.habitpanda.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.habitpanda.R;
import com.example.habitpanda.home.MainActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class CountDownTimerService extends Service {

    //take times from each button, convert them to the milliseconds and addup, then throw into the COunter
    long TIME_LIMIT;
    //countdowntimer object
    CountDownTimer Count;

    TextView textTimer;

    NotificationCompat.Builder builder;
    NotificationManager manager;

    DatabaseReference reference;

    String uid, habitCode,habitName,date;


    //Receive intent here, onStartCOmmand takes it as a parameter !!!!
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //Reveive intent here

        if (intent.getAction()!= null) {
            if (intent.getAction().equals("Stop Timer")) {
                Intent i = new Intent("COUNTDOWN_UPDATED");
                //data added to the intent, name of the data, and the data itself
                i.putExtra("countdown","stop");

                //send intent back to the activity
                sendBroadcast(i);
            }

        }
        Bundle bundle = intent.getExtras();
        TIME_LIMIT =  bundle.getLong("Sum_milliseconds");

        uid = intent.getStringExtra("uid");
        habitCode = intent.getStringExtra("code");
        habitName = intent.getStringExtra("name");
        date = intent.getStringExtra("date");

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("HabitDate").child(date).child(habitCode).child("is_completed");

        Toast.makeText(CountDownTimerService.this, "Service Started", Toast.LENGTH_LONG).show();

        //milisecs in the future, time interval (1 sec)
        Count = new CountDownTimer(TIME_LIMIT, 1000) {

            public void onTick(long millisUntilFinished) {
                //converts miliseconds into seconds
                long seconds = millisUntilFinished / 1000;  //300,000 / 1000 = 300 seconds
                //just add function that automatically transforms milis to hours and minutes

                String time = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));


                //create intent
                Intent i = new Intent("COUNTDOWN_UPDATED");
                //data added to the intent, name of the data, and the data itself
                i.putExtra("countdown",time);

                //send intent back to the activity
                sendBroadcast(i);
                //coundownTimer.setTitle(millisUntilFinished / 1000);
            }

            public void onFinish() {
                //coundownTimer.setTitle("Sedned!");
                Intent i = new Intent("COUNTDOWN_UPDATED");
                i.putExtra("countdown","Sent!");
                sendBroadcast(i);
                showNotification();
                //Log.d("COUNTDOWN", "FINISH!");
                //stop the service from within itself
                stopSelf();
            }
        };

        Count.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(CountDownTimerService.this, "Service Stopped", Toast.LENGTH_LONG).show();
        Count.cancel();
//        Cancel Notification
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Habit Completed")
                .setContentText(habitName)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //create channel for android 8 and 9
        createChannel(manager);
        // Add as notification

        manager.notify(123, builder.build());
        reference.setValue(true);


    }

    public void createChannel(NotificationManager notificationManager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_HIGH);
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            assert manager!=null;
            builder.setChannelId("channelID");
//            channel.setDescription("Description");
            manager.createNotificationChannel(channel);
        }

    }
}

