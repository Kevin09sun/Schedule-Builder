package ScheduleBuilder.src.model;
/**
 * Represents a single scheduled activity for a user.
 * Stores all relevant details including time, location, priority, and the days of the week the activity repeats on.
 */
public class Activity {
    private String actName;
    private int start; //start time in HHMM format
    private int end; //end time in HHMM format
    private String location;
    private int priority; //user-assigned priority from 1 (low) to 10 (high)
    private boolean[] days; //index 0=monday, 1=tuesday, ... ,6=sunday
    /**
     * Constructs an activity with all required fields
     * 
     * @param actName The name of the activity
     * @param start The start time in HHMM
     * @param end The end time in HHMM
     * @param location The location of the activity
     * @param priority The priority score from 1 (low) to 10 (high)
     * @param days A boolean array of length 7 for which the activity occurs on (0 = monday, 6 = sunday)
     */
    public Activity(String actName, int start, int end, String location, int priority, boolean[] days){
        this.actName = actName;
        this.start = start;
        this.end = end;
        this.location = location;
        this.priority = priority;
        this.days = days;
    }

    /**
     * Returns the name of the activity
     * @return Activity Name
     */
    public String getName(){
        return actName;
    }

    /**
     * Returns the location of the activity
     * @return Activity Location
     */
    public String getLocation(){
        return location;
    }

    /**
     * Returns the start time of the activity in HHMM format
     * @return Start time as an integer (e.g. 1430 = 2:30 PM)
     */
    public int getStartTime(){
        return start;
    }

    /**
     * Returns the end time of the activity in HHMM format
     * @return End time as an integer
     */
    public int getEndTime(){
        return end;
    }

    /**
     * Returns the user-assigned priority score
     * @return Priority from 1 (low) to 10 (high)
     */
    public int getPriority(){
        return priority;
    }

    /**
     * Returns a boolean array representing which days the activity occurs upon
     * @return boolean array of length 7
     */
    public boolean[] getDays(){
        return days;
    }

    /**
     * Returns a human readable string of the days of the activity
     * @return Formated day string using abreviation of days of the week
     */
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

    /**
     * Returns a 7 digit binary string representing the days array, used for csv. 1 means activity occurs
     * @return 7 digit binary string
     */
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
