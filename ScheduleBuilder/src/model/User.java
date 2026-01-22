package ScheduleBuilder.src.model;

import java.util.ArrayList;

public class User {
    private ArrayList<Activity> activities;
    private String username;
    public User(String username){
        this.username = username;
        activities = new ArrayList<>();
    }   

    public void addActivity(Activity act){
        activities.add(act);
    }
    
}