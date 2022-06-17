package com.example.todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Edit extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    com.example.todo.SharedPrefs sharedPrefs;

    EditText title_text, detail_text;
    TextView dateTime_text;
    Button addButton;

    int itemId;

    public static final String reqCode_SharedPrefs = "reqCode_sharedPrefs";
    public static String req_code = "req_code";
    task i;

    public static String alarm_title;

    AlarmManager alarmManager;
    Intent alarm_intent;
    PendingIntent pendingIntent;
    Calendar c;
    Calendar calendar;
    int day, month, year, hour, minute;
    long set_Time;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        sharedPrefs = new com.example.todo.SharedPrefs(this);

        if(sharedPrefs.loadDarkMode() == true){
            setTheme(R.style.SettingsDarkTheme);
        }
        else{
            setTheme(R.style.SettingsTheme);
        }

        c = Calendar.getInstance();

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm_intent = new Intent(getApplicationContext(), MyAlarm.class);
        createNotificationChannel();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title_text = (EditText) findViewById(R.id.title_text);
        detail_text = (EditText) findViewById(R.id.detail_text);
        dateTime_text = (TextView) findViewById(R.id.dateTime_text);

        dateTime_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_DateTime();
            }
        });

        addButton = findViewById(R.id.addButton);

        Intent intent   = getIntent();
        itemId = intent.getIntExtra("itemId", -1);
        Log.i("itemId", String.valueOf(itemId));

        if(itemId != -1) {
            i = MainActivity.adapter.getItem(itemId);

            title_text.setText(i.title);
            detail_text.setText(i.detail);
            dateTime_text.setText(i.dateTime);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(title_text.getText().toString().replaceAll(" ", "").length() == 0)){
                        i.title = title_text.getText().toString();
                        i.detail = detail_text.getText().toString();
                        if(i.dateTime != null)
                        {
                            cancelAlarm(i.id);
                        }
                        i.dateTime = dateTime_text.getText().toString();

                        if(!dateTime_text.getText().toString().isEmpty())
                        {
                            int id = generate_ID();
                            i.id = id;
                            setAlarm(c, id, title_text.getText().toString());
                        }
                        saveData();
                    }
                    else{
                        MainActivity.adapter.remove(i);
                        cancelAlarm(i.id);
                        saveData();
                    }
                    finish();
                }

            });

        }
        else{
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(title_text.getText().toString().replaceAll(" ", "").length() == 0)){
                        int reqCode = generate_ID();
                        task newTask = new task(title_text.getText().toString(), detail_text.getText().toString(), false,dateTime_text.getText().toString(), reqCode);
                        MainActivity.adapter.add(newTask);

                        if(!dateTime_text.getText().toString().isEmpty())
                        {
                            setAlarm(c, reqCode, title_text.getText().toString());
                        }

                        Log.i("Item added ", String.valueOf(reqCode));
                        saveData();
                    }
                    finish();
                }

            });
        }

        }
        public void saveData () {
            SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(MainActivity.arrayList);
            editor.putString("task_list", json);
            editor.apply();
        }

        public int generate_ID()
        {
            SharedPreferences sharedPreferences = getSharedPreferences(reqCode_SharedPrefs, MODE_PRIVATE);

            int id = sharedPreferences.getInt(reqCode_SharedPrefs, -1);

            SharedPreferences edit_pref = getSharedPreferences(reqCode_SharedPrefs, MODE_PRIVATE);
            SharedPreferences.Editor editor = edit_pref.edit();

            Log.i("id", String.valueOf(id));
            if(id == -1)
            {
                editor.putInt(reqCode_SharedPrefs, 1);
                editor.apply();

                Log.i("id became", String.valueOf(sharedPreferences.getInt(req_code, -1)));
                return 1;
            }
            else
            {
                Log.i("came here", "line 125");
                editor.putInt(reqCode_SharedPrefs, id + 1);
                editor.apply();
                return id+1;
            }
        }

        private void createNotificationChannel(){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                CharSequence name = "RemainderChannel";
                String description = "Channel for lem";

                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel channel = new NotificationChannel("notify", name, importance);
                channel.setDescription(description);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }

    public void setAlarm(Calendar c, int req_code, String title){
        long timeNow = System.currentTimeMillis();
        Log.i("TimeLeft", String.valueOf(c.getTimeInMillis() - timeNow));
        alarm_intent = new Intent(Edit.this, MyAlarm.class);

        alarm_intent.putExtra("title", title);
        alarm_intent.putExtra("Id", req_code);

        pendingIntent = PendingIntent.getBroadcast(Edit.this, req_code, alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarm_title = title;

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - 2000, pendingIntent);
        Log.i("Note", "Alarm SET " + req_code);


    }
    public void cancelAlarm(int req_code){
        PendingIntent pen = PendingIntent.getBroadcast(Edit.this,
                req_code, alarm_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pen);
        Log.i("Note", "Alarm CANCELLED " + req_code);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Edit.this, Edit.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE,minute);
        c.set(Calendar.SECOND, 0);

        set_Time = c.getTimeInMillis();
        Log.i("TIME SET TO",  c.getTime().toString());

        Date date = c.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        dateTime_text.setText(simpleDateFormat.format(date).toString());

    }

    public void set_DateTime()
    {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Edit.this, Edit.this,year, month,day);
        datePickerDialog.show();

    }
    public void clearTime(View v)
    {
        dateTime_text.setText("");
    }
}

