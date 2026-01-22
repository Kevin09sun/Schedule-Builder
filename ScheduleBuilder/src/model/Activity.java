package ScheduleBuilder.src.model;

public class Activity {
    private String actName;
    private int start;
    private int end;
    private String location;
    private int priority;

    public Activity(String actName, int start, int end, String location, int priority){
        this.actName = actName;
        this.start = start;
        this.end = end;
        this.location = location;
        this.priority = priority;
    }

    public String getName(){
        return actName;
    }

    public String getLocation(){
        return location;
    }

    public int getStartTime(){
        return start;
    }

    public int getEndTime(){
        return end;
    }

    public int getPriority(){
        return priority;
    }
}
