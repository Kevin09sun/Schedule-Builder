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

    private static final String filePathLocation = "ScheduleBuilder/resources/location.csv";
    private static final String filePathUser = "ScheduleBuilder/resources/user.csv";

    public static void main(String[] args) throws Exception {
        initialize();
        while (true){
            //prints the initial options
            System.out.println("   CLUB CONFLICT SCHEDULER - MAIN MENU");
            System.out.println("1. Student Mode (Manage Personal Schedule)");
            System.out.println("2. Club Leader Mode (Find Common Times)");
            System.out.println("3. Save & Exit");
            int choice = readInt("Select an option: ");
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
            //main menu for student mode
            System.out.println("\n--- Student Menu: " + curUser.getName() + " ---");
            System.out.println("1. View Current Schedule");
            System.out.println("2. Add Activity");
            System.out.println("3: Remove an Activity From my Schedule");
            System.out.println("4. Optimize My Schedule (Knapsack)");
            System.out.println("5. Back to Main Menu");
            int choice = readInt("Choice: ");
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
                    //uses recursion to find the most optimal schedule
                    System.out.println("Calculating optimal schedule based on priorities...");
                    ArrayList<Activity> optimized = so.getOptimalSchedule(curUser.getActivities());
                    System.out.println("--- OPTIMIZED SCHEDULE SUGGESTION ---");
                    printSchedule(optimized);
                    System.out.println("Do you wish to use this schedule? (Respond with Yes or No): ");
                    String ans = br.readLine().toLowerCase().trim();
                    if (ans.equals("yes")){
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

        int start = readTime("Enter Activity Start Time (Form {hour}{minute}, e.g. 14:30 should be entered as 1430): ");
        int end;
        //continues prompting for valid end time that is later than the start time
        while (true){
            end = readTime("Enter Activity End Time (Form {hour}{minute}, e.g. 14:30 should be entered as 1430): ");
            if (end > start){
                break;
            }
            System.out.println("Invalid time: end time must be after start time.");
        }
        ArrayList<String> locationList = new ArrayList<>(lm.getLocations());
        Collections.sort(locationList);
        System.out.println("Select a location: ");
        for (int i = 0; i < locationList.size(); i++){
            System.out.println((i + 1) + ". " + locationList.get(i));
        }
        int locChoice = readInt("Enter Number: ");
        //ensure that the number is actually valid location
        while (locChoice < 1 || locChoice > locationList.size()) {
            locChoice = readInt("Invalid. Enter a number between 1 and " + locationList.size() + ": ");
        }
        String location = locationList.get(locChoice - 1);
        int priority = readIntInRange("Enter Activity Priority (1 = low, 10 = high): ", 1, 10);

        boolean[] repeatingDays = new boolean[7];
        while (true){
            System.out.println("Enter days it repeats (1=Mon, 2=Tue, 3=Wed, 4=Thu, 5=Fri, 6=Sat, 7=Sun).");
            System.out.println("Example: Type '135' for Mon/Wed/Fri, or '12345' for Every Weekday:");
            String daysInput = br.readLine().trim();

            //parse digit string into boolean array
            boolean day = false;
            for (char c : daysInput.toCharArray()){
                int dayNum = Character.getNumericValue(c);
                if (dayNum >= 1 && dayNum <= 7){
                    repeatingDays[dayNum - 1] = true;
                    day = true;
                }
            }

            if (day){
                break;
            }
            System.out.println("Invalid input: please enter at least one valid day digit (1-7).");
            //reset in case some invalid chars flipped bits that we want to discard
            repeatingDays = new boolean[7];
        }

        Activity a = new Activity(name, start, end, location, priority, repeatingDays);
        ArrayList<Activity> conflicts = new ArrayList<>();
        for (Activity acts : u.getActivities()){
            if (cc.hasConflict(acts, a)){
                conflicts.add(acts);
            }
        }
        //if any conflicts exist, display them all and ask for a single confirmation
        if (!conflicts.isEmpty()){
            System.out.println("The new activity conflicts with " + conflicts.size() + " existing activity/activities:");
            for (Activity c : conflicts){
                System.out.println(" - " + c.getName() + " [" + c.getDaysString() + "| " + c.getStartTime() + "-" + c.getEndTime() + "] @" + c.getLocation());
            }
            System.out.print("Are you sure you want to add this activity anyway? (Yes/No): ");
            String response = br.readLine().trim();
            if (response.equalsIgnoreCase("yes")){
                u.addActivity(a);
                System.out.println("Activity Added");
            }
            else {
                System.out.println("Ok, going back to menu");
            }
            return;
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

            //checks if the given student user profile actually exists or not
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

        if (selected.isEmpty()){
            System.out.println("No students selected. Returning to main menu");
            return;
        }

        //collecting meeting info
        System.out.println("\n--- Meeting Details ---");
        System.out.print("Meeting Name: ");
        String meetingName = br.readLine();
        ArrayList<String> locationList = new ArrayList<>(lm.getLocations());
        Collections.sort(locationList);
        System.out.println("Select a location:");
        for (int i = 0; i < locationList.size(); i++) {
            System.out.println((i + 1) + ". " + locationList.get(i));
        }
        int locChoice = readInt("Enter Number: ");
        while (locChoice < 1 || locChoice > locationList.size()) {
            locChoice = readInt("Invalid. Enter a number between 1 and " + locationList.size() + ": ");
        }
        String meetingLoc = locationList.get(locChoice - 1);
        int duration = readIntInRange("Duration (in minutes): ", 1, 480);
        int priority = readIntInRange("Priority Score (1-10): ", 1, 10);

        //brute force scan, even tho this looks bad, the outer 3 loops are only around 160 iterations in total. So really, it is a double nested for loop that is ran 160 times, which is O(N^2), not that bad
        System.out.println("\nScanning for common times between 8:00 and 16:00...");
        ArrayList<Activity> possibleSlots = new ArrayList<>();
        for (int day = 0; day < 5; day++){
            for (int hour = 8; hour < 16; hour++){
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

        //no valid time slot found
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
        
        int choice = readInt("\nSelect a slot to confirm (or 0 to cancel): ");
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
     * Initialize all system components: locationManager, ConflictChecker, ScheduleOptimizer, and loads all user profiles.
     * Called once at the start of the program before the main menu loop begins
     * @throws IOException If any csv files could not be read
     */
    private static void initialize() throws IOException{
        lm = new LocationManager();
        br = new BufferedReader(new InputStreamReader(System.in));
        DataManager.loadLocation(filePathLocation, lm);
        cc = new ConflictChecker(lm);
        so = new ScheduleOptimizer(cc);
        users = DataManager.loadUsers(filePathUser);
        System.out.println("System initialized with " + users.size() + " users");
    }

    /**
     * Reads an integer from the user, reprompting if the input is invalid
     * @param prompt The prompt/question to display to the user
     * @return A valid integer entered by the user
     * @throws IOException If reading fails
     */
    private static int readInt(String prompt) throws IOException{
        while (true){
            System.out.println(prompt);
            String raw = br.readLine();
            if (raw == null){
                return 0;
            }
            try {
                return Integer.parseInt(raw.trim());
            } catch (NumberFormatException e){
                System.out.println("Invalid input: please enter a whole number");
            }
        }
    }

    /**
     * Reads an integer between the range of min and max, flagging for invalid input.
     * @param prompt The prompt to display
     * @param min The minimum value
     * @param max The maximum value
     * @return A valid integer in the specified range
     * @throws IOException If reading fails
     */
    private static int readIntInRange(String prompt, int min, int max) throws IOException{
        while (true){
            int val = readInt(prompt);
            if (val >= min && val <= max){
                return val;
            }
            System.out.println("Invalid input: please enter a number between " + min + " and " + max + ".");
        }
    }

    /**
     * Reads a time value in HHMM and ensures that it is valid
     * @param prompt The prompt to display
     * @return A valid time
     * @throws IOException If reading fails
     */
    private static int readTime(String prompt) throws IOException{
        while (true){
            int val = readInt(prompt);
            int hours = val / 100;
            int minutes = val % 100;
            if (val >= 0 && val <= 2359 && hours <= 23 && minutes <= 59){
                return val;
            }
            System.out.println("Invalid time: please enter a time between 0000 and 2359 in HHMM format.");
        }
    }
}
