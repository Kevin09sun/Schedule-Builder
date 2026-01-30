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
        System.out.println("Hello world");

        DataManager.loadLoaction(filePathLocation, lm);

        ArrayList<User> allusers = DataManager.loadUsers(filePathUser);
        for (User u : allusers){
            if (u.getActivities().size() >= 2){
                if (cc.hasConflict(u.getActivities().get(0), u.getActivities().get(1))){
                    System.out.println(u.getName() + " has conflicts");
                }
            }
        }
    }
}
