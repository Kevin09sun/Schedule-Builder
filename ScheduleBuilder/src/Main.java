package ScheduleBuilder.src;
import ScheduleBuilder.src.data.*;
import ScheduleBuilder.src.logic.*;
import ScheduleBuilder.src.model.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = "ScheduleBuilder\\resources\\location.csv";
        File f = new File(filePath);
        System.out.println("Hello world");
        LocationManager lm = new LocationManager();
        DataManager.loadLoaction(filePath, lm);
        System.out.println(lm.getTravelTime("Rm 204", "Cafeteria"));
    }
}
