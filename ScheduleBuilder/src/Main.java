package ScheduleBuilder.src;
import ScheduleBuilder.src.data.*;
import ScheduleBuilder.src.logic.*;
import ScheduleBuilder.src.model.*;
import java.io.*;
import java.util.*;

public class Main {
    private static LocationManager lm;
    private static ConflictChecker cc;
    private static ScheduleOptimizer so;
    private static ArrayList<User> users;
    private static BufferedReader br;

    private static final String filePathLocation = "ScheduleBuilder\\resources\\location.csv";
    private static final String filePathUser = "ScheduleBuilder\\resources\\user.csv";
    public static void main(String[] args) throws Exception {
        initialize();
        while (true){
            System.out.println("   CLUB CONFLICT SCHEDULER - MAIN MENU");
            System.out.println("1. Student Mode (Manage Personal Schedule)");
            System.out.println("2. Club Leader Mode (Find Common Times)");
            System.out.println("3. Save & Exit");
            System.out.print("Select an option: ");
            int choice = Integer.parseInt(br.readLine());
            switch (choice) {
                case 1:
                    studentMode();
                    break;
                case 2:
                    leaderMode();
                    break;
                case 3:
                    DataManager.saveUser(filePathUser, users);
                    System.out.println("User saved, exiting...");
                    return;
                default:
                    System.out.println("Invalid Input");
                    break;
            }
        }
    }

    private static void studentMode() throws IOException{
        System.out.println("Enter your name to login/create account: ");
        String name = br.readLine().trim();
        User curUser = null;
        for (User u : users){
            if (u.getName().equals(name)){
                curUser = u;
                break;
            }
        }
        if (curUser == null){
            curUser = new User(name);
            users.add(curUser);
            System.out.println("New profile created for " + name);
        }
        boolean inMenu = true;
        while (inMenu){
            System.out.println("\n--- Student Menu: " + curUser.getName() + " ---");
            System.out.println("1. View Current Schedule");
            System.out.println("2. Add Activity");
            System.out.println("3: Remove an Activity From my Schedule");
            System.out.println("4. Optimize My Schedule (Knapsack)");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choice: ");
            int choice = Integer.parseInt(br.readLine());
            switch (choice) {
                case 1:
                    printSchedule(curUser.getActivities());
                    break;
                case 2:
                    addActivityToSchedule(curUser);
                    break;
                case 3:
                    removeActivity(curUser);
                    break;
                case 4:
                    System.out.println("Calculating optimal schedule based on priorities...");
                    ArrayList<Activity> optimized = so.getOptimalSchedule(curUser.getActivities());
                    System.out.println("--- OPTIMIZED SCHEDULE SUGGESTION ---");
                    printSchedule(optimized);
                    System.out.println("Do you wish to use this schedule? (Respond with Yes or No): ");
                    String ans = br.readLine();
                    if (ans.equals("Yes")){
                        curUser.updateSchedule(optimized);
                        System.out.println("Schedule updated");
                    }
                    else {
                        System.out.println("Ok, schedule unchanged");
                    }
                    break;
                case 5:
                    inMenu = false;
                    break;
                default:
                    break;
            }
        }
    }

    private static void printSchedule(ArrayList<Activity> activities){
        if (activities.isEmpty()){
            System.out.println("No Activities Found");
        }
        for (Activity a : activities){
            System.out.println(" - " + a.getName() + 
                    " [" + a.getStartTime() + "-" + a.getEndTime() + "] " +
                    "@" + a.getLocation() + " (P:" + a.getPriority() + ")");
        }
    }

    private static void addActivityToSchedule(User u) throws IOException{
        System.out.println("Enter Activity Name: ");
        String name = br.readLine().trim();
        System.out.println("Enter Activity Start Time (Form {hour}{minute}, e.g. 14:30 should be entered as 1430): ");
        int start = Integer.parseInt(br.readLine());
        System.out.println("Enter Activity End Time (Form {hour}{minute}, e.g. 14:30 should be entered as 1430): ");
        int end = Integer.parseInt(br.readLine());
        System.out.println("Enter Activity Location: ");
        String location = br.readLine();
        System.out.println("Enter Activity Priority (1 = low, 9 = high): ");
        int priority = Integer.parseInt(br.readLine());
        Activity a = new Activity(name, start, end, location, priority);
        for (Activity acts : u.getActivities()){
            if (cc.hasConflict(acts, a)){
                System.out.println("The new activity has conflict with another activity " + acts.getName() + " . Are you sure you want to add? (Respond Yes or No)");
                String response = br.readLine();
                if (response.equals("Yes")){
                    u.addActivity(a);
                    System.out.println("Activity Added");
                    return;
                }
                else {
                    System.out.println("Ok, going back to menu");
                    return;
                }
            }
        }
        u.addActivity(a);
        System.out.println("Activity Added");
        return;
    }

    public static void removeActivity(User u) throws IOException{
        System.out.println("Enter the name of the activity you wish to remove: ");
        String name = br.readLine();
        for (Activity a : u.getActivities()){
            if (a.getName().equals(name)){
                u.getActivities().remove(a);
                System.out.println("Activity Removed");
                return;
            }
        }
        System.out.println("Didn't find Activity");
    }

    private static void leaderMode() throws IOException{
        System.out.println("\n--- Club Leader Mode ---");
        if (users.isEmpty()) {
            System.out.println("No users in the database to select.");
            return;
        }
        ArrayList<User> selected = new ArrayList<>();
        while (true){
            System.out.println("\n--- Select Students ---");
            System.out.println("Currently Selected: " + selected.size() + " students");
            
            System.out.println("Available Students:");
            for (User u : users) {
                if (!selected.contains(u)) {
                    System.out.println(" - " + u.getName());
                }
            }
            System.out.println("\nType 'done' when you are finished selecting.");
            System.out.print("Enter student name to add: ");
            String inputName = br.readLine();
            
            // Exit condition
            if (inputName.equalsIgnoreCase("done") || inputName.equals("0")) {
                break;
            }

            boolean studentFound = false;
            for (User u : users) {
                if (u.getName().equalsIgnoreCase(inputName)) {
                    studentFound = true;
                    if (!selected.contains(u)) {
                        selected.add(u);
                        System.out.println("SUCCESS: Added " + u.getName() + " to the meeting.");
                    } else {
                        System.out.println("NOTICE: " + u.getName() + " is already selected.");
                    }
                    break;
                }
            }
            if (!studentFound) {
                System.out.println("ERROR: Could not find a student named '" + inputName + "'. Check spelling.");
            }
        }
        System.out.println("\n--- Meeting Details ---");
        System.out.print("Meeting Name: ");
        String meetingName = br.readLine();
        System.out.print("Meeting Location: ");
        String meetingLoc = br.readLine();
        System.out.print("Duration (in minutes): ");
        int duration = Integer.parseInt(br.readLine());
        System.out.print("Priority Score (1-10): ");
        int priority = Integer.parseInt(br.readLine());

        System.out.println("\nScanning for common times between 8:00 and 16:00...");
        ArrayList<Activity> possibleSlots = new ArrayList<>();

        for (int hour = 8; hour <= 16; hour++){
            for (int min = 0; min <= 45; min += 15){
                int startTime = hour * 100 + min;
                int endTime = startTime + duration;
                if (endTime > 1700){
                    continue;
                }
            }
        }
    }

    private static int addMinutes(int startTime, int duration){
        int hours = startTime / 100;
        int minutes = startTime % 100;
        minutes += duration;
        hours += minutes / 60;
        minutes %= 60;
        return (hours * 100) + minutes;
    }

    private static void initialize() throws IOException{
        lm = new LocationManager();
        br = new BufferedReader(new InputStreamReader(System.in));
        DataManager.loadLoaction(filePathLocation, lm);
        cc = new ConflictChecker(lm);
        so = new ScheduleOptimizer(cc);
        users = DataManager.loadUsers(filePathUser);
        System.out.println("System initailized with " + users.size() + " users");
    }


}
