package com.example.habitpanda.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.habitpanda.R;
import com.example.habitpanda.adapters.TodayTabAdapter;
import com.google.android.material.tabs.TabLayout;


public class TodayFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TodayTabAdapter todayTabAdapter;

    public TodayFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_today, container, false);
//        Elements Init
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.container_pager);


//        Setting adapter
        viewPager2.setAdapter(new TodayTabAdapter(this));

//        Initializing Tabs for signin and signup
        tabLayout.addTab(tabLayout.newTab().setText("Habit"));
        tabLayout.addTab(tabLayout.newTab().setText("Task"));

//        on tab selected move to the selected tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


//        On page change change the tab layout to current layout
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        return view;
    }
}