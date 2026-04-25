package ScheduleBuilder.src.logic;

import ScheduleBuilder.src.model.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Manages the location graph and computes shortest travel times between locations.
 * Internally stores a weighted bidirectional graph as an adjacency list, 
 * where each node is a location name and each edge is a LocationEdge with
 * a travel time in minutes. 
 * Shortest path queries are answered using Dijkstra's algorithm on weighted graph.
 */
public class LocationManager {
    // Adjacency list: maps each location name to it's list of outgoing weighted edges
    private HashMap<String, ArrayList<LocationEdge>> adjMap;

    /**
     * Constructs a LocationManager with an empty location graph
     */
    public LocationManager(){
        this.adjMap = new HashMap<>();
    }

    /**
     * Returns all known locations names in the graph
     * @return A set of all location name strings
     */
    public Set<String> getLocations(){
        return adjMap.keySet();
    }

    /**
     * Adds a bidirectional edge between two locations given a travel time.
     * Both directions are added so that travel time is equal (a to b = b to a).
     * All location names are lowercased to ensure consistent lookups.
     * @param from The origin location name
     * @param to The destionation location name
     * @param min Travel time in mins between the locations
     */
    public void addPath(String from, String to, int min){
        from = from.toLowerCase().trim();
        to = to.toLowerCase().trim();
        adjMap.putIfAbsent(from, new ArrayList<>());
        adjMap.putIfAbsent(to, new ArrayList<>());

        //adding edges
        adjMap.get(from).add(new LocationEdge(to, min));
        adjMap.get(to).add(new LocationEdge(from, min));
    }
    
    /**
     * Computes the shortest time it takes to travel between these two locations using Dijkstra's
     * 
     * <p> 
     * The algorithm maintains a priority queue ordered by total travel time.
     * Starting from the origin, it repeatedly tries to visit the closest unvisited node.
     * A node is skipped if a shorter path to it has already been found - this prevents infinite loops. 
     * The search terminates as soon as the destination is popped from the queue, guaranteeing the shortest path is returned.
     * </p>
     *
     * <p>
     * If no path exists between the two locations, Integer.MAX_VALUE is returned.
     * </p>
     * 
     * @param from - The start location
     * @param to - The end location
     * @return An integer representing the minimum time it takes to travel between these two locations, Integer.MAX_VALUE if no path exists
     */
    public int getTravelTime(String from, String to){
        from = from.toLowerCase().trim();
        to = to.toLowerCase().trim();
        // check if origin = destionation, then no travel time needed
        if (from.equals(to)){
            return 0;
        }

        //if either location doesn't exist in the database, return no travel time
        if (!adjMap.containsKey(from) || !adjMap.containsKey(to)){
            return 0;
        }

        // priority queue, entries are compared by their integer value
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(Map.Entry.comparingByValue());

        //tracks the best time so far to every visited node
        HashMap<String, Integer> shortestTime = new HashMap<>();

        //adds the starting location at cost 0
        pq.add(new SimpleEntry<>(from, 0));
        shortestTime.put(from, 0);

        while (!pq.isEmpty()){
            Map.Entry<String, Integer> cur = pq.poll();
            
            // destination reached
            if (cur.getKey().equals(to)){
                return cur.getValue();
            }

            //if current path is slower than a previously found path
            if (cur.getValue() > shortestTime.getOrDefault(cur.getKey(), Integer.MAX_VALUE)){
                continue;
            }

            // if neither case above, goes through every outgoing edge from current node
            if (adjMap.containsKey(cur.getKey())){
                for(LocationEdge edge : adjMap.get(cur.getKey())){
                    // if the node we are trying to get to currently has a travel time higher than our proposed value, go to it
                    if (cur.getValue() + edge.getMinutes() < shortestTime.getOrDefault(edge.getDestination(), Integer.MAX_VALUE)){
                        pq.add(new SimpleEntry<>(edge.getDestination(), cur.getValue() + edge.getMinutes()));
                        shortestTime.put(edge.getDestination(), cur.getValue() + edge.getMinutes());
                    }
                }
            }
        }

        //no path found
        return Integer.MAX_VALUE;
    }
}
