package ScheduleBuilder.src.model;

public class LocationEdge {
    private String destination;
    private int min;

    public LocationEdge(String destination, int min){
        this.destination = destination;
        this.min = min;
    }
    
    public String getDestionation(){
        return destination.toLowerCase();
    }

    public int getMinutes(){
        return min;
    }
}
