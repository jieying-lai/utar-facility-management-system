package my.edu.utar.service;

import my.edu.utar.model.Booking;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportGenerator {

    public void generateUserReport(String userID, ArrayList<Booking> bookingList) {
        int totalBookings = 0;
        HashMap<String, Integer> facilityCount = new HashMap<>();

        for (Booking b : bookingList) {
            if (b.getUserID().equals(userID)) {
                totalBookings++;
                String facilityID = b.getFacilityID();
                facilityCount.put(facilityID, facilityCount.getOrDefault(facilityID, 0) + 1);
            }
        }

        String mostUsedFacility = "None";
        int maxCount = 0;

        for (String facility : facilityCount.keySet()) {
            if (facilityCount.get(facility) > maxCount) {
                maxCount = facilityCount.get(facility);
                mostUsedFacility = facility;
            }
        }

        System.out.println("\n------ QUICK SUMMARY -------");
        System.out.println("Total Bookings recorded: " + totalBookings);
        System.out.println("Most Booked Facility: " + mostUsedFacility + " (" + maxCount + " times)");
        System.out.println("----------------------------------");
    }
}