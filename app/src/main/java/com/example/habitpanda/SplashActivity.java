package com.example.habitpanda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.habitpanda.home.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    ImageView logoView;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();
        logoView = findViewById(R.id.logo);

        int splashTimeOut = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth.getUid()!=null) {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, splashTimeOut);

        Animation myAnim= AnimationUtils.loadAnimation(this,R.anim.anim_splash);
        logoView.startAnimation(myAnim);
    }
}