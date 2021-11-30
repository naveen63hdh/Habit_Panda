package com.example.habitpanda.adapters;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitpanda.R;
import com.example.habitpanda.home.MainActivity;
import com.example.habitpanda.models.Habit;
import com.example.habitpanda.service.CountDownTimerService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class TodayHabitAdapter extends RecyclerView.Adapter<TodayHabitAdapter.HabitViewHolder> {

    ArrayList<Habit> habitArrayList;
    Context context;
    String hr, min, sec, uid, date;

    NotificationCompat.Builder builder;
    NotificationManager manager;


    public TodayHabitAdapter(ArrayList<Habit> habitArrayList, Context context, String uid, String date) {
        this.habitArrayList = habitArrayList;
        this.context = context;
        this.uid = uid;
        this.date = date;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_habit, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        HabitViewHolder viewHolder = new HabitViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        holder.nameTxt.setText(habitArrayList.get(position).getHabitName());
        holder.descTxt.setText(habitArrayList.get(position).getHabitDesc());
        Log.i("HABIT_TYPE", "" + habitArrayList.get(position).getHabitType());
        if (habitArrayList.get(position).isCompleted()) {
            holder.timerTxt.setVisibility(View.GONE);
            holder.actionBtn.setText("Completed");
        } else {
            if (habitArrayList.get(position).getHabitType() == 0) {
                holder.timerTxt.setVisibility(View.GONE);
                holder.actionBtn.setText("Mark as Complete");
            } else {
                holder.timerTxt.setVisibility(View.VISIBLE);
                holder.actionBtn.setText("Start Timer");
            }
        }

        if (habitArrayList.get(position).isMon())
            holder.mon.setTextColor(context.getResources().getColor(R.color.green));
        if (habitArrayList.get(position).isTue())
            holder.tues.setTextColor(context.getResources().getColor(R.color.green));
        if (habitArrayList.get(position).isWed())
            holder.wed.setTextColor(context.getResources().getColor(R.color.green));
        if (habitArrayList.get(position).isThur())
            holder.thur.setTextColor(context.getResources().getColor(R.color.green));
        if (habitArrayList.get(position).isFri())
            holder.fri.setTextColor(context.getResources().getColor(R.color.green));
        if (habitArrayList.get(position).isSat())
            holder.sat.setTextColor(context.getResources().getColor(R.color.green));
        if (habitArrayList.get(position).isSat())
            holder.sun.setTextColor(context.getResources().getColor(R.color.green));

        holder.actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (habitArrayList.get(holder.getAdapterPosition()).getHabitType() == 0) {
//                    TODO for mark as complete option
                } else {
                    String timer = habitArrayList.get(holder.getAdapterPosition()).getTime();
//                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.US);
//                    try {
//                        Date time = sdf.parse(timer);
//                        SimpleDateFormat hhf = new SimpleDateFormat("hh",Locale.US);
//                        SimpleDateFormat mmf = new SimpleDateFormat("mm",Locale.US);
                    String[] time = timer.split(":");
//                        SimpleDateFormat ssf = new SimpleDateFormat("ss",Locale.US);
                    hr = time[0];
                    min = time[1];
                    sec = time[2];

//                        Dialog dialog = new TimerDialog(context,hh,mm,ss);
//                        dialog.setCanceledOnTouchOutside(true);
//                        dialog.show();

                    Dialog dialog = new Dialog(context);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.dialog_timer);

                    Button timerBtn = dialog.findViewById(R.id.timerBtn);
                    EditText hhTxt = dialog.findViewById(R.id.timer_hh);
                    EditText mmTxt = dialog.findViewById(R.id.timer_mm);
                    EditText ssTxt = dialog.findViewById(R.id.timer_ss);

                    hhTxt.setText(hr);
                    mmTxt.setText(min);
                    ssTxt.setText(sec);

                    timerBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hr = hhTxt.getText().toString();
                            min = mmTxt.getText().toString();
                            sec = ssTxt.getText().toString();


                            //parse to long
                            Long hourLong = Long.parseLong(hr);
                            //to MIliseconds
                            long hourMili = hourLong * 3600000;
                            //MINUTES
                            //parse to long
                            Long minuteLong = Long.parseLong(min);
                            //to Miliseconds
                            long minuteMili = minuteLong * 60000;
                            //SECONDS
                            Long secondLong = Long.parseLong(sec);
                            //to MIliseconds
                            long secondMili = secondLong * 1000;


                            //add to the sum
                            long milisSum = secondMili + minuteMili + hourMili;

                            //new intent to start the service
                            Intent intent = new Intent(context, CountDownTimerService.class);
                            //put extra
                            intent.putExtra("Sum_milliseconds", milisSum);
                            intent.putExtra("uid", uid);
                            intent.putExtra("code", habitArrayList.get(holder.getAdapterPosition()).getId());
                            intent.putExtra("name", habitArrayList.get(holder.getAdapterPosition()).getHabitName());
                            intent.putExtra("date", date);
                            //send the intent, start service
                            context.stopService(new Intent(context, CountDownTimerService.class));
                            context.startService(intent);

//                                Start Notification

                            PendingIntent stopIntent = PendingIntent.getService(context,0,intent,0);

                            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                                    new Intent(context, MainActivity.class), 0);

                            builder = new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_notifications)
                                    .setContentTitle(habitArrayList.get(holder.getAdapterPosition()).getHabitName())
                                    .setContentText(hr + ":" + min + ":" + sec)
                                    .setOnlyAlertOnce(true)
                                    .setContentIntent(contentIntent)
                                    .addAction(R.drawable.ic_close,"Stop Timer",stopIntent)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            //create channel for android 8 and 9
                            createChannel(manager);
                            // Add as notification

                            manager.notify(100, builder.build());

                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }


                BroadcastReceiver uiUpdated = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        //This is the part where I get the timer value from the service and I update it every second,
                        //because I send the data from the service every second.

                        String time = intent.getExtras().getString("countdown");
                        builder.setContentText(time);
                        manager.notify(100, builder.build());
                    }
                };//New boradcasrReceiver with the same name as he one registered earlier


                context.registerReceiver(uiUpdated, new IntentFilter("COUNTDOWN_UPDATED"));


//                holder.timerTxt.setText(hr+":"+min+":"+sec);
            }
        });
    }

    public void createChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel channel = new NotificationChannel("channelID", "name", NotificationManager.IMPORTANCE_HIGH);
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            assert manager != null;
            builder.setChannelId("channelID");
//            channel.setDescription("Description");
            manager.createNotificationChannel(channel);
        }

    }


    @Override
    public int getItemCount() {
        return habitArrayList.size();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        ImageView levelImg;
        Button actionBtn;
        TextView mon, tues, wed, thur, fri, sat, sun, nameTxt, descTxt, timerTxt;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            levelImg = itemView.findViewById(R.id.level_img);
            actionBtn = itemView.findViewById(R.id.action_btn);
            mon = itemView.findViewById(R.id.mon);
            tues = itemView.findViewById(R.id.tue);
            wed = itemView.findViewById(R.id.wed);
            thur = itemView.findViewById(R.id.thur);
            fri = itemView.findViewById(R.id.fri);
            sat = itemView.findViewById(R.id.sat);
            sun = itemView.findViewById(R.id.sun);
            nameTxt = itemView.findViewById(R.id.habit_name);
            descTxt = itemView.findViewById(R.id.habit_desc);
            timerTxt = itemView.findViewById(R.id.countDownTimer);
        }
    }
}
