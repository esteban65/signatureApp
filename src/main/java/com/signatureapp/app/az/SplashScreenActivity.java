package com.signatureapp.app.az;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);



        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {

                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    startActivity(new Intent( SplashScreenActivity.this,     MainActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                }else{
                    startActivity(new Intent( SplashScreenActivity.this,     RegisterActivity.class));
                    finish();
                    overridePendingTransition(0, 0);
                };

            }
        }, secondsDelayed * 2000);

    }
}