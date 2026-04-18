package my.edu.utar.service;

import my.edu.utar.model.Booking;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportGenerator {
	
	 public void generateUserReport(String userID, ArrayList<Booking> bookingList) {

	        int totalBookings = 0;
	        int totalHours = 0;

	        HashMap<String, Integer> facilityCount = new HashMap<>();

	        for (Booking b : bookingList) {

	            if (b.getUserID().equals(userID) &&
	                (b.getStatus().equals("Approved") || b.getStatus().equals("Rejected"))) {

	                totalBookings++;

	                totalHours += 1;

	                String facilityID = b.getFacilityID();
	                facilityCount.put(facilityID,
	                        facilityCount.getOrDefault(facilityID, 0) + 1);
	            }
	        }

	        String mostUsedFacility = null;
	        int maxCount = 0;

	        for (String facility : facilityCount.keySet()) {
	            int count = facilityCount.get(facility);
	            if (count > maxCount) {
	                maxCount = count;
	                mostUsedFacility = facility;
	            }
	        }

	        System.out.println("===== BOOKING REPORT =====");
	        System.out.println("Total Bookings : " + totalBookings);
	        System.out.println("Total Hours    : " + totalHours);

	        if (mostUsedFacility != null) {
	            System.out.println("Most Used Facility : " + mostUsedFacility +
	                               " (" + maxCount + " times)");
	        } else {
	            System.out.println("Most Used Facility : None");
	        }
	    }

}
