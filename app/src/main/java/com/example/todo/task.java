package com.example.todo;

public class task {
    String title;
    String detail;
    Boolean is_done;
    String dateTime;
    int id;

    public task(String title, String detail, Boolean is_done, String dateTime, int id){
        this.title = title;
        this.detail = detail;
        this.is_done = is_done;
        this.dateTime = dateTime;
        this.id = id;
    }
}
