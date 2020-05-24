package com.kabarakuniversityforumApp.General_Issues;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.kabarakuniversityforumApp.AccountCreation.MainActivity;
import com.kabarakuniversityforumApp.R;

import ir.alirezabdn.wp7progress.WP10ProgressBar;

public class SplashScreen extends AppCompatActivity {
    private WP10ProgressBar progressBar;
    private FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progressBar = new WP10ProgressBar(this);

        progressBar= (WP10ProgressBar) findViewById(R.id.progress_bar_splash);
        progressBar.setIndicatorRadius(4);
        //showing progress
        progressBar.showProgressBar();
        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth.getCurrentUser()!=null){
                    startActivity(new Intent(SplashScreen.this, ThePanel.class));
                }else
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        },3000);

    }
}
