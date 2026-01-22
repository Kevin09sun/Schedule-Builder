package ScheduleBuilder.src;
import ScheduleBuilder.src.logic.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        LocationManager lm = new LocationManager();
        lm.addPath("Rm 204", "Gym", 6);
        lm.addPath("Gym", "Cafeteria", 4);
        lm.addPath("Rm 204", "Cafeteria", 15);
        System.out.println(lm.getTravelTime("Rm 204", "Cafeteria"));
    }
}
