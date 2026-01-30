package ScheduleBuilder.src.logic;
import java.util.ArrayList;
import java.util.Comparator;
import java.lang.reflect.Array;
import java.util.*;
import ScheduleBuilder.src.data.*;
import ScheduleBuilder.src.model.*;
public class ScheduleOptimizer {
    private ConflictChecker cc;
    public ScheduleOptimizer(ConflictChecker cc){
        this.cc = cc;
    }

    public ArrayList<Activity> getOptimalSchedule(ArrayList<Activity> allActivities){
        Collections.sort(allActivities, Comparator.comparingInt(Activity::getStartTime));
        return recurse(allActivities, 0, null);
    }

    private ArrayList<Activity> recurse(ArrayList<Activity> activities, int currentIndex, Activity last){
        if (currentIndex >= activities.size()){
            return new ArrayList<>();
        }

        Activity a = activities.get(currentIndex);
        ArrayList<Activity> skip = recurse(activities, currentIndex + 1, last);
        int skipScore = calculate(skip);

        ArrayList<Activity> include = new ArrayList<>();
        int includeScore = -1;
        boolean conflict = false;
        if (last != null){
            conflict = cc.hasConflict(last, a);
        }
        if (!conflict){
            include = recurse(activities, currentIndex + 1, a);
            include.add(a);
            includeScore = calculate(include);
        }
        if (skipScore > includeScore){
            return skip;
        }
        else {
            return include;
        }
    }

    private int calculate(ArrayList<Activity> activites){
        int sum = 0;
        for (Activity a : activites){
            sum += a.getPriority();
        }
        return sum;
    }

}