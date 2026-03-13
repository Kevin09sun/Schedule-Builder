package ScheduleBuilder.src.model;

public class Activity {
    private String actName;
    private int start;
    private int end;
    private String location;
    private int priority;
    private boolean[] days; //0=monday, 1=tuesday, ... ,6=sunday

    public Activity(String actName, int start, int end, String location, int priority, boolean[] days){
        this.actName = actName;
        this.start = start;
        this.end = end;
        this.location = location;
        this.priority = priority;
        this.days = days;
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

    public boolean[] getDays(){
        return days;
    }

    public String getDaysString(){
        String[] str = {"M", "Tu", "W", "Th", "F", "Sa", "Su"};
        String res = "";
        for (int i = 0; i < 7; i++){
            if (days[i]){
                res += str[i] + " ";
            }
        }
        return res;
    }

    public String getDaysCSV(){
        String res = "";
        for (boolean bool : days){
            if (bool){
                res += 1;
            }
            else {
                res += 0;
            }
        }
        return res;
    }
}
