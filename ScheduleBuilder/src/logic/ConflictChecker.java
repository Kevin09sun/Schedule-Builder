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
        if (a.getStartTime() > b.getStartTime()){
            Activity temp = a;
            a = b;
            b = temp;
        }
        if (b.getStartTime() < a.getEndTime()){
            return true;
        }
        int timeDif = b.getStartTime() - a.getEndTime();
        if (lm.getTravelTime(a.getLocation(), b.getLocation()) > timeDif){
            return true;
        }
        else {
            return false;
        }
    }
}
