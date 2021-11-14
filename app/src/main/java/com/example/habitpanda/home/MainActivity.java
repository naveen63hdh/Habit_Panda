package com.example.habitpanda.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.habitpanda.R;
import com.example.habitpanda.home.fragment.MyHabitFragment;
import com.example.habitpanda.home.fragment.MyTaskFragment;
import com.example.habitpanda.home.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar;

        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color = '#000000'>Habit Panda</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.green)));

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.my_habit);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MyHabitFragment()).commit();
        bottomNavigationView.setOnItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new ProfileFragment()).commit();
                return true;
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