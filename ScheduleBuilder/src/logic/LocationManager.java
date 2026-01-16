package ScheduleBuilder.src.logic;

import ScheduleBuilder.src.model.*;
import ScheduleBuilder.src.data.*;
import java.util.*;
import javafx.util.Pair;
public class LocationManager {
    private HashMap<String, ArrayList<LocationEdge>> adjMap;

    public LocationManager(){
        this.adjMap = new HashMap<>();
    }

    public void addPath(String from, String to, int min){
        adjMap.putIfAbsent(from, new ArrayList<>());
        adjMap.putIfAbsent(to, new ArrayList<>());
        adjMap.get(from).add(new LocationEdge(to, min));
        adjMap.get(to).add(new LocationEdge(from, min));
    }
    
    public int getTravelTime(String from, String to){
        if (from.toLowerCase().equals(to.toLowerCase())){
            return 0;
        }
        PriorityQueue<Pair<String, int>>;
    }
}
