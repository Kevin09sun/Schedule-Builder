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
        System.out.println("Hello world");
        ArrayList<User> allusers = DataManager.loadUsers(filePathUser);
        User u = new User("Farmer John");
        Activity a = new Activity("AI Club", 1330, 1400, "Rm 204", 1);
        u.addActivity(a);
        allusers.add(u);
        DataManager.saveUser(filePathUser, allusers);
    }
}
