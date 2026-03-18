package ScheduleBuilder.src.model;

import java.util.ArrayList;

/**
 * Represents a user profile in the scheduling system.
 * Each user has a unique username and maintains a personal list of activities representing their schedule.
 */
public class User {
    private ArrayList<Activity> activities;
    private String username;

    /**
     * Constructs a new user with the given username and an empty activities arraylist
     * @param username The display name used to identify this user
     */
    public User(String username){
        this.username = username;
        activities = new ArrayList<>();
    }   

    /**
     * Adds a single activity to a user's schedule
     * @param act The activity to add
     */
    public void addActivity(Activity act){
        activities.add(act);
    }
    /**
     * Returns the full list of the user's activities
     * @return ArrayList<Activity> containing all activities of the user
     */
    public ArrayList<Activity> getActivities(){
        return activities;
    }

    /**
     * Replaces the user's current schedule with a new list of activities.
     * Used after the schedule optimizer produces an optimized subset.
     * @param newSchedule The list of activities to update to
     */
    public void updateSchedule(ArrayList<Activity> newSchedule){
        activities = newSchedule;
    }

    /**
     * Returns the name of the user
     * @return Username string
     */
    public String getName(){
        return username;
    }
}