package my.edu.utar.model;

import my.edu.utar.util.Constants;

//Represents a bookable campus facility (lecture hall, lab, sports court, etc.)
public class Facility {
    private String facilityID;
    private String block;
    private String floor;
    private String roomNo;
    private String name;
    private String type;   
    private int capacity;
    private String status;

    public Facility() {}

    public Facility(String facilityID, String block, String floor, String roomNo, 
                    String name, String type, int capacity, String status) {
        this.facilityID = facilityID;
        this.block = block;
        this.floor = floor;
        this.roomNo = roomNo;
        this.name = name; 
        this.type = type;       
        this.capacity = capacity;
        this.status = status;
    }

    public String toFileString() {
        return facilityID + Constants.DELIMITER + 
               block + Constants.DELIMITER + 
               floor + Constants.DELIMITER + 
               roomNo + Constants.DELIMITER + 
               name + Constants.DELIMITER + 
               type + Constants.DELIMITER + 
               capacity + Constants.DELIMITER + 
               status;
    }
    
    @Override
    public String toString() {
        return toFileString();
    }
    
    public void display() {
        System.out.println("ID: " + facilityID + " | Room: " + roomNo);
        System.out.println("Type: " + type + " | Capacity: " + capacity);
        System.out.println("Name: " + name);
        System.out.println("Status: " + status);
    }

    public String getFacilityID() { return facilityID; }
    public String getBlock()      { return block; }
    public String getFloor()      { return floor; }
    public String getRoomNo()     { return roomNo; }
    public String getName()       { return name; }
    public String getType()       { return type; }
    public int getCapacity()      { return capacity; }
    public String getStatus()     { return status; }

    public void setFacilityID(String id)       { this.facilityID = id; }
    public void setBlock(String block)         { this.block = block; }
    public void setFloor(String floor)         { this.floor = floor; }
    public void setRoomNo(String roomNo)       { this.roomNo = roomNo; }
    public void setName(String name)           { this.name = name; }
    public void setType(String type)           { this.type = type; }
    public void setCapacity(int capacity)      { this.capacity = capacity; }
    public void setStatus(String status)       { this.status = status; }
}