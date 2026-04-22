package ScheduleBuilder.src.logic;
import ScheduleBuilder.src.model.*;

/**
 * Checks whether two activities conflict with each other
 * A conflict exists if two activities share the same day and it is impossible to get from the first one to the second one accounting for travel time
 */
public class ConflictChecker {
    private LocationManager lm;

    /**
     * Constructs a conflict checker with access to the location graph
     * @param lm The LocationManager used to compute travel time
     */
    public ConflictChecker(LocationManager lm){
        this.lm = lm;
    }

    /**
     * Determine whether two activities conflict based on time.
     * <p>
     * Two activities conflict if and only if:
     * <ol>
     *   <li>They share at least one common day of the week</li>
     *   <li>Their time windows overlap directly or the gap between them is
     *       shorter than the travel time between their locations.</li>
     * </ol>
     * </p>
     *
     * <p>
     * The method first reorders the two activities so the earlier one (by start time)
     * is always treated as activity a. It then checks for direct overlap, and if none,
     * converts the gap between end of activity a and start of activity b into minutes
     * and compares it against the LocationManager's computed travel time.
     * </p>
     * @param a First activity
     * @param b Second Activity
     * @return True if the activities conflict, false otherwise
     */
    public boolean hasConflict(Activity a, Activity b){
        //check if the activities share any day of the week
        boolean sameDay = false;
        for (int i = 0; i < 7; i++){
            if (a.getDays()[i] && b.getDays()[i]){
                sameDay = true;
                break;
            }
        }
        
        // Activities on dif days can never conflict
        if (!sameDay){
            return false;
        }

        //reorder if necessary so that a always is before b
        if (a.getStartTime() > b.getStartTime()){
            Activity temp = a;
            a = b;
            b = temp;
        }

        // direct time overlap
        if (b.getStartTime() < a.getEndTime()){
            return true;
        }

        //converts from HHMM to total minutes for gap calculation
        int aEndMins = (a.getEndTime() / 100) * 60 + (a.getEndTime() % 100);
        int bStartMins = (b.getStartTime() / 100) * 60 + (b.getStartTime() % 100);
        int timeDif = bStartMins - aEndMins;

        //conflict if travel time between locations exceeds the available gap
        return lm.getTravelTime(a.getLocation(), b.getLocation()) > timeDif;
    }
}
