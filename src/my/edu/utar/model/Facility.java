package my.edu.utar.model;

import my.edu.utar.util.Constants;

/**
 * Facility.java
 * Represents a campus facility (room/court/lab/hall).
 * Member 2 owns this class - add methods as needed.
 */
public class Facility {

    private String facilityID;
    private String block;       // KA or KB
    private String floor;       // G, 1, 2, 3...
    private String roomNo;      // e.g. KB104
    private String type;        // from Constants.FACILITY_TYPES
    private int    capacity;
    private String status;      // Available / Unavailable / Under Maintenance

    public Facility(String facilityID, String block, String floor,
                    String roomNo, String type, int capacity, String status) {
        this.facilityID = facilityID;
        this.block      = block;
        this.floor      = floor;
        this.roomNo     = roomNo;
        this.type       = type;
        this.capacity   = capacity;
        this.status     = status;
    }

    public Facility() {}

    /**
     * Converts to file string for facilities.txt
     * Format: facilityID|block|floor|roomNo|type|capacity|status
     */
    public String toFileString() {
        return facilityID + Constants.DELIMITER + block + Constants.DELIMITER +
               floor + Constants.DELIMITER + roomNo + Constants.DELIMITER +
               type + Constants.DELIMITER + capacity + Constants.DELIMITER + status;
    }

    public void display() {
        System.out.println("ID: " + facilityID + " | Block: " + block +
                           " | Floor: " + floor + " | Room: " + roomNo +
                           " | Type: " + type + " | Capacity: " + capacity +
                           " | Status: " + status);
    }

    // Getters
    public String getFacilityID() { return facilityID; }
    public String getBlock()      { return block; }
    public String getFloor()      { return floor; }
    public String getRoomNo()     { return roomNo; }
    public String getType()       { return type; }
    public int    getCapacity()   { return capacity; }
    public String getStatus()     { return status; }

    // Setters
    public void setFacilityID(String id)     { this.facilityID = id; }
    public void setBlock(String block)       { this.block = block; }
    public void setFloor(String floor)       { this.floor = floor; }
    public void setRoomNo(String roomNo)     { this.roomNo = roomNo; }
    public void setType(String type)         { this.type = type; }
    public void setCapacity(int capacity)    { this.capacity = capacity; }
    public void setStatus(String status)     { this.status = status; }
}
