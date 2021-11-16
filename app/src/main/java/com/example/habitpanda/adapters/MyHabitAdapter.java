package com.example.habitpanda.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitpanda.R;
import com.example.habitpanda.models.Habit;

import java.util.ArrayList;

public class MyHabitAdapter extends RecyclerView.Adapter<MyHabitAdapter.HabitViewHolder>{

    ArrayList<Habit> habitArrayList;
    Context context;

    public MyHabitAdapter(ArrayList<Habit> habitArrayList, Context context) {
        this.habitArrayList = habitArrayList;
        this.context = context;
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
        if (habitArrayList.get(position).getHabitType()==0)
            holder.actionBtn.setText("Mark as Complete");
        else
            holder.actionBtn.setText("Start Timer");

        if (habitArrayList.get(position).isMon())
            holder.mon.setTextColor(context.getResources().getColor(R.color.dark_green));
        if (habitArrayList.get(position).isTue())
            holder.tues.setTextColor(context.getResources().getColor(R.color.dark_green));
        if (habitArrayList.get(position).isWed())
            holder.wed.setTextColor(context.getResources().getColor(R.color.dark_green));
        if (habitArrayList.get(position).isThur())
            holder.thur.setTextColor(context.getResources().getColor(R.color.dark_green));
        if (habitArrayList.get(position).isFri())
            holder.fri.setTextColor(context.getResources().getColor(R.color.dark_green));
        if (habitArrayList.get(position).isSat())
            holder.sat.setTextColor(context.getResources().getColor(R.color.dark_green));
        if (habitArrayList.get(position).isSat())
            holder.sun.setTextColor(context.getResources().getColor(R.color.dark_green));

    }

    @Override
    public int getItemCount() {
        return habitArrayList.size();
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {

        ImageView levelImg;
        Button actionBtn;
        TextView mon,tues,wed,thur,fri,sat,sun,nameTxt,descTxt;
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
        }
    }
}
