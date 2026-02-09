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
    /**
     * @return ArrayList<Activity> containing all activities of the user
     */
    public ArrayList<Activity> getActivities(){
        return activities;
    }

    public void updateSchedule(ArrayList<Activity> newSchedule){
        activities = newSchedule;
    }

    public String getName(){
        return username;
    }
}