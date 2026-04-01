package ScheduleBuilder.src.data;

import java.io.*;
import java.util.*;
import ScheduleBuilder.src.logic.*;
import ScheduleBuilder.src.model.*;

/**
 * Utility class responsible for all persistent file reading/writing in the application.
 * All methods are static, no instance of this class is every created
 */
public class DataManager {
    /**
     * Reads the location csv file and fills out the LocationManager graph
     * <p>
     * Each line in the file represents one bidirectional edge in the format:
     * origin, destination, travelTimeInMinutes.
     * The method trims whitespace from all fields before passing them to
     * lm.addPath() to prevent lookup mismatches caused by inconsistent spacing.
     * </p>
     * @param filename Path to the location CSV file
     * @param lm The locationManager to be filled out
     * @throws IOException If the file can't be read
     */
    public static void loadLoaction(String filename, LocationManager lm) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        while (line != null){

            //extracts the origin, destination, and travel time
            String[] parts = line.split(",");
            String origin = parts[0].trim().toLowerCase();
            String destination = parts[1].trim().toLowerCase();
            int time = Integer.parseInt(parts[2].trim());

            lm.addPath(origin, destination, time);
            line = br.readLine();
        }
        System.out.println("Location loaded successfully\n");
        br.close();
    }
    
    /**
     * Reads the user csv file and reconstructs all user and activity objects
     * <p>
     * Each line in the file represents one activity belonging to a user, in the format:
     * username, activityName, startTime, endTime, location, priority, dayString.
     * Since multiple lines can belong to the same user, a hashamp
     * is used to ensures each username maps to exactly one User object, and subsequent rows for the same user append to their existing activity list.
     * </p>
     *
     * <p>
     * The day string is a 7-character binary string where each character corresponds to a day of the week starting Monday. Each character
     * is parsed into the boolean array stored in the Activity.
     * </p>
     * 
     * @param filename Path to the user csv file
     * @return An ArrayList for all reconstructed user objects
     * @throws IOException If the file cannot be read
     */
    public static ArrayList<User> loadUsers(String filename) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();

        //HashMap so one user can have multiple activities
        HashMap<String, User> userMap = new HashMap<>();
        while (line != null){

            //extracts and parse from csv
            String[] parts = line.split(",");
            String username = parts[0].trim();
            String actName = parts[1].trim();
            int start = Integer.parseInt(parts[2].trim());
            int end = Integer.parseInt(parts[3].trim());
            String location = parts[4].trim().toLowerCase();
            int priority = Integer.parseInt(parts[5].trim());

            //parsing day string into a boolean array
            boolean days[] = new boolean[7];
            if (parts.length >= 7){
                String dayStr = parts[6].trim();
                for (int i = 0; i < 7; i++){
                    days[i] = (dayStr.charAt(i) == '1');
                }
            }

            //default to monday if no day string present
            else {
                days[0] = true;
            }
            Activity act = new Activity(actName, start, end, location, priority, days);

            //creates user entry if not already present, then appends the activity
            userMap.putIfAbsent(username, new User(username));
            userMap.get(username).addActivity(act);
            line = br.readLine();
        }
        System.out.println("User loaded\n");
        br.close();
        return new ArrayList<>(userMap.values());
    }

    /**
     * Saves all pre-exisiting and all new users and activities to the csv file. Each activity is written the same way as they are read. 
     * @param filename Path to the csv file
     * @param allUsers The list of all users, including those that are already there and those to be saved
     * @throws IOException If the file cannot written
     */
    public static void saveUser(String filename, ArrayList<User> allUsers) throws IOException{
        //Try to ensure that the file is closed after writing to it
        try(FileWriter fw = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw)){

            for (User u : allUsers){
                for (Activity a : u.getActivities()) {
                    //Build the csv matching the format of loadUser
                    String line = u.getName() + "," + 
                                a.getName() + "," + 
                                a.getStartTime() + "," + 
                                a.getEndTime() + "," + 
                                a.getLocation() + "," + 
                                a.getPriority() + "," +
                                a.getDaysCSV();
                    
                    pw.println(line);
                }
            }
        }
        System.out.println("All data saved to " + filename);
    }
}