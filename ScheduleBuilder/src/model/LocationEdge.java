package ScheduleBuilder.src.model;
/**
 * Represents a directed and weighted edge in the location graph.
 * Each edge connects one location to another with an associated travel time, there are no self-loops.
 * Used by LocationManager to build the graph.
 */
public class LocationEdge {
    private String destination;
    private int min; //travel time in minutes

    /**
     * Constructs a LocationEdge to a given destination with a specified travel time
     * @param destination The name of the destination location
     * @param min Travel time in min to reach the destionation
     */
    public LocationEdge(String destination, int min){
        this.destination = destination;
        this.min = min;
    }
    
    /**
     * Returns the destination location in lowercase
     * @return The destination name in lowercase
     */
    public String getDestination(){
        return destination.toLowerCase();
    }

    /**
     * Returns the travel time to the destination in minutes
     * @return Travel time in minutes
     */
    public int getMinutes(){
        return min;
    }
}
