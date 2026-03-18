package ScheduleBuilder.src;
import ScheduleBuilder.src.data.*;
import ScheduleBuilder.src.logic.*;
import ScheduleBuilder.src.model.*;
import java.io.*;
import java.util.*;

/**
 * Main program
 */
public class Main {
    private static LocationManager lm;
    private static ConflictChecker cc;
    private static ScheduleOptimizer so;
    private static ArrayList<User> users;
    private static BufferedReader br;
    private static DataManager dm;

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

    /**
     * Runs student mode, which allows a user to log in or create a profile and manage their schedule through a submenu.
     * Options include viewing schedule, adding/removing activities, and running the schedule optimizer
     * @throws IOException
     */
    private static void studentMode() throws IOException{
        System.out.println("Enter your name to login/create account: ");
        String name = br.readLine().trim();

        // Search for an existing user profile that is case-insensitive
        User curUser = null;
        for (User u : users){
            if (u.getName().toLowerCase().equals(name.toLowerCase())){
                curUser = u;
                break;
            }
        }

        // Create a new profile if no match was found
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

    /**
     * Prints a formatted list of the schedule to the terminal.
     * If the list is empty, a message is displayed
     * @param activities The list of activities to display
     */
    private static void printSchedule(ArrayList<Activity> activities){
        if (activities.isEmpty()){
            System.out.println("No Activities Found");
        }
        for (Activity a : activities){
            System.out.println(" - " + a.getName() + " [" + a.getDaysString() + " | " + a.getStartTime() + "-" + a.getEndTime() + "] " +  "@" + a.getLocation() + " (P:" + a.getPriority() + ")");
        }
    }

    /**
     * Prompts the user to add all details relating to an activity and adds it to their schedule.
     * If it conflicts with an existing schedule, warns the user and gets confirmation before proceeding
     * @param u The user to add the activity to
     * @throws IOException If reading input fails
     */
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

        System.out.println("Enter days it repeats (1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat, 7=Sun).");
        System.out.println("Example: Type '135' for Mon/Wed/Fri, or '12345' for Every Weekday:");
        String daysInput = br.readLine().trim();

        //parse digit string into boolean array
        boolean[] repeatingDays = new boolean[7];
        for (char c : daysInput.toCharArray()){
            int dayNum = Character.getNumericValue(c);
            if (dayNum >= 1 && dayNum <= 7){
                repeatingDays[dayNum - 1] = true;
            }
        }

        Activity a = new Activity(name, start, end, location, priority, repeatingDays);

        //checks for conflicts with existing activities before adding
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

    /**
     * Prompts the user to enter an activity name and remove it from their schedule if found.
     * @param u The user to remove the activity from
     * @throws IOException If reading input fails
     */
    public static void removeActivity(User u) throws IOException{
        System.out.println("Enter the name of the activity you wish to remove: ");
        String name = br.readLine();
        for (Activity a : u.getActivities()){
            if (a.getName().toLowerCase().equals(name.toLowerCase())){
                u.getActivities().remove(a);
                System.out.println("Activity Removed");
                return;
            }
        }
        System.out.println("Didn't find Activity");
    }

    /**
     * Runs club leader mode, allowing user to select multiple profiles to find a shared time for a meeting.
     * Goes through every weekday (M~F) and scans for common time slots. Any slot where all students are free is considered as an option.
     * The confirmed meeting/activity is added to all selected users' profiles
     * @throws IOException If reading user input fails
     */
    private static void leaderMode() throws IOException{
        System.out.println("\n--- Club Leader Mode ---");
        if (users.isEmpty()) {
            System.out.println("No users in the database to select.");
            return;
        }
        ArrayList<User> selected = new ArrayList<>();

        // Student selection loop
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

        //collecting meeting info
        System.out.println("\n--- Meeting Details ---");
        System.out.print("Meeting Name: ");
        String meetingName = br.readLine();
        System.out.print("Meeting Location: ");
        String meetingLoc = br.readLine();
        System.out.print("Duration (in minutes): ");
        int duration = Integer.parseInt(br.readLine());
        System.out.print("Priority Score (1-10): ");
        int priority = Integer.parseInt(br.readLine());

        //brute force scan
        System.out.println("\nScanning for common times between 8:00 and 16:00...");
        ArrayList<Activity> possibleSlots = new ArrayList<>();
        for (int day = 0; day < 5; day++){
            for (int hour = 8; hour <= 16; hour++){
                for (int min = 0; min <= 45; min += 15){
                    int startTime = hour * 100 + min;
                    int endTime = addMinutes(startTime, duration);

                    //skips slots past 5 p.m.
                    if (endTime > 1700){
                        continue;
                    }
                    boolean[] meetingDays = new boolean[7];
                    meetingDays[day] = true;
                    Activity proposed = new Activity(meetingName, startTime, endTime, meetingLoc, priority, meetingDays);

                    //check that no selected student has a conflicting activity
                    boolean isSltoFree = true;
                    for (User u : selected){
                        for (Activity existing : u.getActivities()){
                            if (cc.hasConflict(existing, proposed)){
                                isSltoFree = false;
                                break;
                            }
                        }
                        if (!isSltoFree){
                            break;
                        }
                    }
                    if (isSltoFree){
                        possibleSlots.add(proposed);
                    }
                }
            }
        }
        if (possibleSlots.isEmpty()) {
            System.out.println("FAILED: No common time found for all selected students.");
            return;
        }

        //display all valid slots and let the leader pick one
        System.out.println("\n--- Available Time Slots ---");
        for (int i = 0; i < possibleSlots.size(); i++){
            Activity slot = possibleSlots.get(i);
            System.out.println((i + 1) + ". " + slot.getDaysString() + " " + slot.getStartTime() + " to " + slot.getEndTime());
        }
        
        System.out.print("\nSelect a slot to confirm (or 0 to cancel): ");

        int choice = Integer.parseInt(br.readLine());
        if (choice > 0 && choice <= possibleSlots.size()) {
            Activity confirmedMeeting = possibleSlots.get(choice - 1);

            // Add the confirmed meeting to all selected student and save
            for (User u : selected) {
                u.addActivity(confirmedMeeting);
            }
            DataManager.saveUser(filePathUser, users);
            System.out.println("\nSUCCESS: '" + meetingName + "' added to all selected profiles and saved!");
        }
        else {
            System.out.println("Action Cancelled.");
        }
    }

    /**
     * Adds a duration in minutes to a time in HHMM format and returns the result in HHMM format.
     * @param startTime Start time in HHMM format
     * @param duration Duration to add in minutes
     * @return Resulting time in HHMM
     */
    private static int addMinutes(int startTime, int duration){
        int hours = startTime / 100;
        int minutes = startTime % 100;
        minutes += duration;
        hours += minutes / 60;
        minutes %= 60;
        return (hours * 100) + minutes;
    }

    /**
     * Intialize all system components: locationManager, ConflictChecker, ScheduleOptimizer, and loads all user profiles.
     * Called once at the start of the program before the main menu loop begins
     * @throws IOException If any csv files could not be read
     */
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
