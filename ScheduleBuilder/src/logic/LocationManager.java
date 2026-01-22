package ScheduleBuilder.src.logic;

import ScheduleBuilder.src.model.*;
import ScheduleBuilder.src.data.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
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
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(Map.Entry.comparingByValue());
        HashMap<String, Integer> shortestTime = new HashMap<>();
        pq.add(new SimpleEntry<>(from, 0));
        shortestTime.put(from, 0);
        while (!pq.isEmpty()){
            Map.Entry<String, Integer> cur = pq.poll();
            if (cur.getKey().equals(to)){
                return cur.getValue();
            }
            if (cur.getValue() > shortestTime.getOrDefault(cur.getKey(), Integer.MAX_VALUE)){
                continue;
            }
            if (adjMap.containsKey(cur.getKey())){
                for(LocationEdge edge : adjMap.get(cur.getKey())){
                    if (cur.getValue() + edge.getMinutes() < shortestTime.getOrDefault(edge.getDestionation(), Integer.MAX_VALUE)){
                        pq.add(new SimpleEntry<>(edge.getDestionation(), cur.getValue() + edge.getMinutes()));
                        shortestTime.put(edge.getDestionation(), cur.getValue() + edge.getMinutes());
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
    }
}
