package my.edu.utar.model;

import my.edu.utar.util.Constants;

public class Booking {

    private String bookingID;
    private String userID;
    private String facilityID;
    private String applyDate; 
    private String bookingDate;
    private int    timeSlot; 
    private String purpose;
    private int    pax;       
    private String status; 
    private String rejectReason;

    public Booking(String bookingID, String userID, String facilityID,
                   String applyDate, String bookingDate, int timeSlot,
                   String purpose, int pax, String status, String rejectReason) {
        this.bookingID    = bookingID;
        this.userID       = userID;
        this.facilityID   = facilityID;
        this.applyDate    = applyDate;
        this.bookingDate  = bookingDate;
        this.timeSlot     = timeSlot;
        this.purpose      = purpose;
        this.pax          = pax;
        this.status       = status;
        this.rejectReason = rejectReason != null ? rejectReason : "";
    }
    

    public Booking() {}

    public String toFileString() {
        return bookingID + Constants.DELIMITER + userID + Constants.DELIMITER +
               facilityID + Constants.DELIMITER + applyDate + Constants.DELIMITER +
               bookingDate + Constants.DELIMITER + timeSlot + Constants.DELIMITER +
               purpose + Constants.DELIMITER + pax + Constants.DELIMITER +
               status + Constants.DELIMITER + rejectReason;
    }
    
    @Override
    public String toString() {
        return bookingID + " | " + userID + " | " + facilityID + " | " + bookingDate +
               " | " + Constants.TIME_SLOTS[timeSlot] + " | " + purpose + " | " + pax + " | " + status;
    }

    public void display() {
    	System.out.println("Booking ID: " + bookingID);
        System.out.println("Facility ID: " + facilityID);
        System.out.println("Date: " + bookingDate);
        System.out.println("Time Slot: " + Constants.TIME_SLOTS[timeSlot]);
        System.out.println("Purpose: " + purpose);
        System.out.println("Pax: " + pax);
        System.out.println("Status: " + status);
        if (rejectReason != null && !rejectReason.isEmpty()) {
            System.out.println("Reject Reason: " + rejectReason);
        }
    }

    public String getBookingID()    { return bookingID; }
    public String getUserID()       { return userID; }
    public String getFacilityID()   { return facilityID; }
    public String getApplyDate()    { return applyDate; }
    public String getBookingDate()  { return bookingDate; }
    public int    getTimeSlot()     { return timeSlot; }
    public String getPurpose()      { return purpose; }
    public int    getPax()          { return pax; }
    public String getStatus()       { return status; }
    public String getRejectReason() { return rejectReason; }

    public void setBookingID(String id)         { this.bookingID = id; }
    public void setUserID(String id)            { this.userID = id; }
    public void setFacilityID(String id)        { this.facilityID = id; }
    public void setApplyDate(String d)          { this.applyDate = d; }
    public void setBookingDate(String d)        { this.bookingDate = d; }
    public void setTimeSlot(int slot)           { this.timeSlot = slot; }
    public void setPurpose(String p)            { this.purpose = p; }
    public void setPax(int p)                   { this.pax = p; }
    public void setStatus(String s)             { this.status = s; }
    public void setRejectReason(String r)       { this.rejectReason = r; }
}
