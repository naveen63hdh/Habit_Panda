package com.example.habitpanda.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.habitpanda.home.fragment.today.TodayHabitFragment;
import com.example.habitpanda.home.fragment.today.TodayTaskFragment;

public class TodayTabAdapter extends FragmentStateAdapter {

    public TodayTabAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1)
            return new TodayTaskFragment();
        return new TodayHabitFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
