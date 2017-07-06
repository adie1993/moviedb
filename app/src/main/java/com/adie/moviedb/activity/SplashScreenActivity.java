package com.adie.moviedb.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.adie.moviedb.R;


public class SplashScreenActivity extends AppCompatActivity {

    private CountDownTimer mCountDownTimer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //delay 3 seconds
        this.mCountDownTimer = new CountDownTimer(1000, 3000) {
            public void onTick(long l) {
            }

            public void onFinish() {

                    Intent a = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(a);
                    finish();

            }
        }.start();
    }


}
