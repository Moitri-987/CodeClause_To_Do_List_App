package com.example.todo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {
    private Switch switch1;
    private TextView suggestions;
    com.example.todo.SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPrefs = new com.example.todo.SharedPrefs(this);
        if(sharedPrefs.loadDarkMode() == true){
            setTheme(R.style.SettingsDarkTheme);
        }
        else{
            setTheme(R.style.SettingsTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switch1 = findViewById(R.id.switch1);
        if(sharedPrefs.loadDarkMode() == true){
            switch1.setChecked(true);
        }
        else{
            switch1.setChecked(false);
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPrefs.setDarkMode(true);
                    restartApp();
                }
                else {
                    sharedPrefs.setDarkMode(false);
                    restartApp();
                }
            }
        });

        suggestions = findViewById(R.id.suggestions);

        suggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);

                Uri data = Uri.parse("mailto:?subject=" + "To Do List App Suggestion"+ "&body=" + "Your Suggestions" + "&to=" + "passionidol987@gmail.com");

                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent, "Send mail..."));
            }
        });
    }

    public void restartApp(){
        Intent j = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(j);

        Intent i = new Intent(getApplicationContext(), Settings.class);
        startActivity(i);

        finish();
    }
}
