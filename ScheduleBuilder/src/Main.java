package ScheduleBuilder.src;
import ScheduleBuilder.src.data.*;
import ScheduleBuilder.src.logic.*;
import ScheduleBuilder.src.model.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePathLocation = "ScheduleBuilder\\resources\\location.csv";
        String filePathUser = "ScheduleBuilder\\resources\\user.csv";
        File f = new File(filePathLocation);
        LocationManager lm = new LocationManager();
        ConflictChecker cc = new ConflictChecker(lm);
        ScheduleOptimizer so = new ScheduleOptimizer(cc);
        System.out.println("Hello world");

        DataManager.loadLoaction(filePathLocation, lm);

        ArrayList<User> allusers = DataManager.loadUsers(filePathUser);
        for (User u : allusers){
            System.out.println("Optimal Activities for user: " + u.getName());
            ArrayList<Activity> temp = so.getOptimalSchedule(u.getActivities());
            for (Activity a : temp){
                System.out.print(a.getName() + " ");
            }
            System.out.println();
        }
    }
}
