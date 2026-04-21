package my.edu.utar.model;

import my.edu.utar.util.Constants;

public class Facility {
    private String facilityID;
    private String block;
    private String floor;
    private String roomNo;
    private String name;    // Maps to description/name in file
    private String type;   
    private int capacity;
    private String status;

    // Default Constructor
    public Facility() {}

    /**
     * Full Constructor
     * Based on file structure: F180|KB|7|KB724|Name|Type|30|Available
     */
    public Facility(String facilityID, String block, String floor, String roomNo, 
                    String name, String type, int capacity, String status) {
        this.facilityID = facilityID;
        this.block = block;
        this.floor = floor;
        this.roomNo = roomNo;
        this.name = name;      // Correctly assigning the name parameter
        this.type = type;       
        this.capacity = capacity;
        this.status = status;
    }

    /**
     * Formats the facility object into a delimited string for file storage
     */
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
    
    /**
     * Console display for debugging or specific details
     */
    public void display() {
        System.out.println("ID: " + facilityID + " | Room: " + roomNo);
        System.out.println("Type: " + type + " | Capacity: " + capacity);
        System.out.println("Name: " + name);
        System.out.println("Status: " + status);
    }

    // ================= GETTERS =================
    public String getFacilityID() { return facilityID; }
    public String getBlock()      { return block; }
    public String getFloor()      { return floor; }
    public String getRoomNo()     { return roomNo; }
    public String getName()       { return name; }
    public String getType()       { return type; }
    public int getCapacity()      { return capacity; }
    public String getStatus()     { return status; }

    // ================= SETTERS =================
    public void setFacilityID(String id)       { this.facilityID = id; }
    public void setBlock(String block)         { this.block = block; }
    public void setFloor(String floor)         { this.floor = floor; }
    public void setRoomNo(String roomNo)       { this.roomNo = roomNo; }
    public void setName(String name)           { this.name = name; }
    public void setType(String type)           { this.type = type; }
    public void setCapacity(int capacity)      { this.capacity = capacity; }
    public void setStatus(String status)       { this.status = status; }
}