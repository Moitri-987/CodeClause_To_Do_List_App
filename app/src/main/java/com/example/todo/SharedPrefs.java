package com.example.todo    ;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    SharedPreferences mySharedPrefs;

    public SharedPrefs(Context context){
        mySharedPrefs = context.getSharedPreferences("filename", Context.MODE_PRIVATE);
    }

    public void setDarkMode(Boolean state){
        SharedPreferences.Editor editor = mySharedPrefs.edit();
        editor.putBoolean("DarkMode", state);
        editor.apply();
    }
    public Boolean loadDarkMode(){
        Boolean state = mySharedPrefs.getBoolean("DarkMode", false);
        return state;
    }
}
