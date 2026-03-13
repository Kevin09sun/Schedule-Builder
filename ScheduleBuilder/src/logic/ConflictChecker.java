package ScheduleBuilder.src.logic;
import ScheduleBuilder.src.model.*;

public class ConflictChecker {
    private LocationManager lm;
    public ConflictChecker(LocationManager lm){
        this.lm = lm;
    }

    /**
     * 
     * @param a First activity
     * @param b Second Activity
     * @return A boolean indicating whether it is possible to get from activity a to b accounting for the travel time between the two
     */
    public boolean hasConflict(Activity a, Activity b){
        boolean sameDay = false;
        for (int i = 0; i < 7; i++){
            if (a.getDays()[i] && b.getDays()[i]){
                sameDay = true;
            }
        }
        if (!sameDay){
            return false;
        }
        if (a.getStartTime() > b.getStartTime()){
            Activity temp = a;
            a = b;
            b = temp;
        }
        if (b.getStartTime() < a.getEndTime()){
            return true;
        }
        int aEndMins = (a.getEndTime() / 100) * 60 + (a.getEndTime() % 100);
        int bStartMins = (b.getStartTime() / 100) * 60 + (b.getStartTime() % 100);
        int timeDif = bStartMins - aEndMins;
        if (lm.getTravelTime(a.getLocation(), b.getLocation()) > timeDif){
            return true;
        }
        else {
            return false;
        }
    }
}
