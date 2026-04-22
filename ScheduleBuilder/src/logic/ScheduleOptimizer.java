package ScheduleBuilder.src.logic;
import java.util.*;
import ScheduleBuilder.src.model.*;

/**
 * Generates the highest total priority conflict-free subset of activities for a user given his activities.
 * Uses a recursive algorithm similar to the 0/1 knapsack problem,
 * where each activity is either included or excluded, and the goal is to
 * maximize the total priority score while avoiding scheduling conflicts.
 */
public class ScheduleOptimizer {
    private ConflictChecker cc;
    private HashMap<String, ArrayList<Activity>> dp;

    /**
     * Constructs a schedule optimizer with a conflict checker
     * @param cc The conflict checker used to validate activity combinations
     */
    public ScheduleOptimizer(ConflictChecker cc){
        this.cc = cc;
        dp = new HashMap<>();
    }

    /**
     * Returns the optimal subset of activities that maximizes the total prioirty of them.
     * Activities are first sorted by start time to ensure that the recursive algorithm processes them in chronological order
     * @param allActivities The full list of activities to be optimized
     * @return An arraylist of activities representing the optimal schedule
     */
    public ArrayList<Activity> getOptimalSchedule(ArrayList<Activity> allActivities){
        //sort by start time
        Collections.sort(allActivities, Comparator.comparingInt(Activity::getStartTime));
        dp.clear();
        return recurse(allActivities, 0, null);
    }

    /**
     * Recursively explore all possible include/exclude combination of activities, returning the subset with the highest total priority
     * <p>
     * At each index, two branches are explored:
     * <ul>
     *   <li>Skip: The current activity is excluded and the recursion advances.</li>
     *   <li>Include: If the current activity does not conflict with the last included activity, it is added and the recursion advances with it as the new last.</li>
     * </ul>
     * The branch with the higher total priority score is returned.
     * </p>
     *
     * <p>
     * This is very similar to the 0/1 knapsack problem where weight is the constraint and value is the variable being maximized.
     * </p>
     * @param activities The full sorted list of activities
     * @param currentIndex The current position in the list being evaluated
     * @param last The last activity that was included, null if none
     * @return The optimal schedule for maximizing priority
     */
    private ArrayList<Activity> recurse(ArrayList<Activity> activities, int currentIndex, Activity last){
        //base case: no more activities to be optimized
        if (currentIndex >= activities.size()){
            return new ArrayList<>();
        }
        int lastIdx;
        if (last == null){
            lastIdx = -1;
        }
        else {
            lastIdx = activities.indexOf(last);
        }
        String key = currentIndex + "," + lastIdx;
        if (dp.containsKey(key)){
            //to prevent writing to previous cached results
            return new ArrayList<>(dp.get(key));
        }

        Activity a = activities.get(currentIndex);

        //option 1: skip
        ArrayList<Activity> skip = recurse(activities, currentIndex + 1, last);
        int skipScore = calculate(skip);

        //option 2: include (only if it doesn't conflict)
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
        ArrayList<Activity> result;
        if (skipScore > includeScore){
            result = skip;
        }
        else {
            result = include;
        }
        dp.put(key, result);
        return result;
    }

    /**
     * Computes the total priority of a list of activities
     * @param activites The list of activities to score
     * @return The sum of all priority values in the list
     */
    private int calculate(ArrayList<Activity> activites){
        int sum = 0;
        for (Activity a : activites){
            sum += a.getPriority();
        }
        return sum;
    }

}