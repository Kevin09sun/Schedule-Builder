package ScheduleBuilder.src.data;

import java.io.*;
import java.util.*;
import ScheduleBuilder.src.logic.*;
import ScheduleBuilder.src.model.*;

public class DataManager {
    public static void loadLoaction(String filename, LocationManager lm) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        while (line != null){
            String[] parts = line.split(",");
            String origin = parts[0].trim().toLowerCase();
            String destination = parts[1].trim().toLowerCase();
            int time = Integer.parseInt(parts[2].trim());
            lm.addPath(origin, destination, time);
            line = br.readLine();
        }
        System.out.println("Location loaded successfully\n");
    }
    
    public static ArrayList<User> loadUsers(String filename) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        HashMap<String, User> userMap = new HashMap<>();
        while (line != null){
            String[] parts = line.split(",");
            String username = parts[0].trim();
            String actName = parts[1].trim();
            int start = Integer.parseInt(parts[2].trim());
            int end = Integer.parseInt(parts[3].trim());
            String location = parts[4].trim();
            int priority = Integer.parseInt(parts[5].trim());
            Activity act = new Activity(actName, start, end, location, priority);
            userMap.putIfAbsent(username, new User(username));
            userMap.get(username).addActivity(act);
            line = br.readLine();
        }
        System.out.println("User loaded\n");
        return new ArrayList<>(userMap.values());
    }

    public static void saveUser(String filename, ArrayList<User> allUsers) throws IOException{
        PrintWriter pw = new PrintWriter(new FileWriter(filename));
        for (User u : allUsers){
            for (Activity a : u.getActivities()){
                pw.println(u.getName() + "," + a.getName() + "," + a.getStartTime() + "," + a.getEndTime() + "," + a.getLocation() + "," + a.getPriority());
            }
        }
        System.out.println("All data saved to " + filename);
    }
}