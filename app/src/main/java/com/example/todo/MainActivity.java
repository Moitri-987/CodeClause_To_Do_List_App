package com.example.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    com.example.todo.SharedPrefs sharedPrefs;

    static ArrayList<task> arrayList;
    static ItemAdapter adapter;

    ListView listView;

    AlarmManager alarmManager;
    Intent alarm_intent;
    PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPrefs = new com.example.todo.SharedPrefs(this);

        if(sharedPrefs.loadDarkMode() == true){
            setTheme(R.style.AppDarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        adapter = new ItemAdapter(this, arrayList);
        listView = findViewById(R.id.listView);

        listView.setAdapter(adapter);

        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm_intent = new Intent(getApplicationContext(), MyAlarm.class);

    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("task_list", null);
        Type type = new TypeToken<ArrayList<task>>(){}.getType();
        arrayList = gson.fromJson(json, type);

        if(arrayList == null){
            arrayList = new ArrayList<>();
        }
    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(MainActivity.arrayList);

        editor.putString("task_list", json);
        editor.apply();
    }


    public class ItemAdapter extends ArrayAdapter<task> {
        public ItemAdapter(Context context, ArrayList<task> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            task user = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_task, parent, false);
            }
            final TextView tvtitle = convertView.findViewById(R.id.item_title);

            tvtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(), Edit.class);

                    in.putExtra("itemId", position);
                    startActivity(in);
                }
            });
            tvtitle.setOnLongClickListener(new View.OnLongClickListener() {
                task i = getItem(position);
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Do you want to delete this item?")
                            .setMessage("This item will no longer exist")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(i.dateTime != null)
                                    {
                                        cancelAlarm(i.id);
                                    }

                                    adapter.remove(i);

                                    saveData();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                    return true;
                }
            });
            final TextView tvdetails =convertView.findViewById(R.id.item_details);

            CheckBox tvdone =convertView.findViewById(R.id.item_done);

            tvdone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (buttonView.isChecked()) {
                        Log.i("INFO", "Checked "+String.valueOf(position));

                        task currentTask = getItem(position);
                        currentTask.is_done = true;

                        tvtitle.setPaintFlags(tvtitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvdetails.setPaintFlags(tvtitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        saveData();

                    }
                    else {
                        Log.i("INFO", "Unchecked "+String.valueOf(position));

                        task currentTask = getItem(position);
                        currentTask.is_done = false;

                        tvtitle.setPaintFlags(tvtitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        tvdetails.setPaintFlags(tvtitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

                        saveData();
                    }
                }
            });

            tvtitle.setText(user.title);
            tvdetails.setText(user.detail);

            String[] lines = user.detail.split("\n");
            tvdetails.setText("");

            if(!user.detail.isEmpty()){
                for (int i = 0; i < lines.length; i++) {
                    //"\u2022" -> bullet point
                    tvdetails.append("\u2022  " + lines[i]+"\n");
                }
            }

            tvdone.setChecked(user.is_done);
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.add_item:
                startActivity(new Intent(MainActivity.this, Edit.class));

                return true;

            case R.id.settings:
                openSettings();
                return  true;

            case R.id.clear_all:
                clearall();
                return true;

            case R.id.clear_completed:
                clear_completed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openSettings(){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    public void clearall(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Are you sure?")
                .setMessage("All the items will be cleared")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i("No of items", String.valueOf(adapter.getCount()));

                        while(adapter.getCount() > 0)
                        {
                            task it = adapter.getItem(adapter.getCount() - 1);

                            if(it.dateTime != null)
                            {
                                cancelAlarm(it.id);
                            }
                            Log.i("Removed", String.valueOf(adapter.getCount()));
                            adapter.remove(it);
                        }



                        saveData();
                        Toast.makeText(getApplicationContext(), "Cleared all Tasks", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();


    }
    public void clear_completed(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Are you sure?")
                .setMessage("All the Completed will be cleared")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Log.i("No of items", String.valueOf(adapter.getCount()));
                        int ind = adapter.getCount();
                        while(ind > 0)
                        {
                            task it = adapter.getItem(ind - 1);
                            if(it.is_done)
                            {
                                if(it.dateTime != null)
                                {
                                    cancelAlarm(it.id);
                                }
                                Log.i("Removed", String.valueOf(adapter.getCount()));
                                adapter.remove(it);
                            }
                            ind--;

                        }

                        saveData();
                        Toast.makeText(getApplicationContext(), "Cleared all Tasks", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    public void cancelAlarm(int req_code){
        PendingIntent pen = PendingIntent.getBroadcast(MainActivity.this,
                req_code, alarm_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pen);
        Log.i("Note", "Alarm Cancelled " + req_code);
    }

}
