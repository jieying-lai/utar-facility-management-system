package my.edu.utar.service;

import java.util.List; 
import java.util.ArrayList; 
import java.util.Map;
import java.util.HashMap;
import my.edu.utar.model.Booking; 
import my.edu.utar.model.Facility; 

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
	 public void generateAdminAnalytics(List<Booking> allBookings, List<Facility> allFacilities) {
	     System.out.println("\n===== SYSTEM ANALYTICS REPORT =====");
	     
	     for (Facility f : allFacilities) {
	         int bookedSlots = 0;
	         int[] slotDistribution = new int[6];

	         for (Booking b : allBookings) {
	             if (b.getFacilityID().equals(f.getFacilityID()) && b.getStatus().equalsIgnoreCase("Approved")) {
	                 bookedSlots++;
	                 if (b.getTimeSlot() >= 1 && b.getTimeSlot() <= 5) {
	                     slotDistribution[b.getTimeSlot()]++;
	                 }
	             }
	         }

	         double utilization = (bookedSlots / 5.0) * 100; 

	         int peakSlot = 1;
	         for (int i = 2; i <= 5; i++) {
	             if (slotDistribution[i] > slotDistribution[peakSlot]) peakSlot = i;
	         }

	         System.out.printf("Facility: %-10s | Utilization: %.1f%% | Peak Slot: %d | Total Hours: %d\n", 
	             f.getFacilityID(), utilization, (bookedSlots > 0 ? peakSlot : 0), (bookedSlots * 2));
	     }
	 }
	 
	 public void showTopUsers(List<Booking> allBookings) {
		    HashMap<String, Integer> userActivity = new HashMap<>();
		    for (Booking b : allBookings) {
		        if (b.getStatus().equalsIgnoreCase("Approved")) {
		            userActivity.put(b.getUserID(), userActivity.getOrDefault(b.getUserID(), 0) + 1);
		        }
		    }
		    System.out.println("Top Active Users based on Approved Bookings: " + userActivity);
		}

}
