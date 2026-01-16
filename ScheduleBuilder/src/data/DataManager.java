package ScheduleBuilder.src.data;

import java.io.*;
import java.util.*;
import ScheduleBuilder.src.logic.*;
import ScheduleBuilder.src.model.*;

public class DataManager {
    public static void loadLoaction(String filename, LocationManager lm) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        while (!line.equals(null)){
            String[] parts = line.split(",");
            String origin = parts[0].trim().toLowerCase();
            String destination = parts[1].trim().toLowerCase();
            int time = Integer.parseInt(parts[2].trim());
        }
    }
    
}