package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class Splash extends AppCompatActivity {

    ImageView background;
    LottieAnimationView lottie;
   TextView texttodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        background=findViewById(R.id.background);
        lottie=findViewById(R.id.lottie);
        texttodo=findViewById(R.id.texttodo);

        background.animate().translationY(-1500).setDuration(1000).setStartDelay(4000);
        lottie.animate().translationY(1300).setDuration(1000).setStartDelay(4000);
       texttodo.animate().translationY(1300).setDuration(1000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
            }
        }, 5000);
    }
}