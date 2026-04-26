package my.edu.utar.service;

import my.edu.utar.model.Booking;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportGenerator {

	public void generateUserReport(String userID, ArrayList<Booking> bookingList) {
	    int totalBookings = 0;
	    int cancelledBookings = 0;
	    int approvedBookings = 0;
	    HashMap<String, Integer> facilityCount = new HashMap<>();

	    for (Booking b : bookingList) {
	        if (b.getUserID().equals(userID)) {
	            totalBookings++;
	            
	            if (b.getStatus().equalsIgnoreCase("Cancelled")) {
	                cancelledBookings++;
	            } else if (b.getStatus().equalsIgnoreCase("Approved")) {
	                approvedBookings++;
	            }
	            String facilityID = b.getFacilityID();
	            facilityCount.put(facilityID, facilityCount.getOrDefault(facilityID, 0) + 1);
	        }
	    }

	    double cancellationRate = (totalBookings > 0) ? ((double) cancelledBookings / totalBookings) * 100 : 0;
	    double reliabilityScore = 100 - cancellationRate;

	    String reliabilityRating;
	    if (totalBookings == 0) reliabilityRating = "N/A (No History)";
	    else if (reliabilityScore >= 90) reliabilityRating = "EXCELLENT ";
	    else if (reliabilityScore >= 70) reliabilityRating = "GOOD ";
	    else reliabilityRating = "NEEDS IMPROVEMENT ";

	    String mostUsedFacility = "None";
	    int maxCount = 0;
	    for (String facility : facilityCount.keySet()) {
	        if (facilityCount.get(facility) > maxCount) {
	            maxCount = facilityCount.get(facility);
	            mostUsedFacility = facility;
	        }
	    }

	    System.out.println("\n╔════════════════════════════════════════════════════╗");
	    System.out.println(  "║             PERSONAL USAGE ANALYTICS               ║");
	    System.out.println(  "╠════════════════════════════════════════════════════╣");
	    System.out.printf(   "║ Total Bookings Made    : %-25d ║\n", totalBookings);
	    System.out.printf(   "║ Completed/Approved     : %-25d ║\n", approvedBookings);
	    System.out.printf(   "║ Total Cancellations    : %-25d ║\n", cancelledBookings);
	    System.out.println(  "╟────────────────────────────────────────────────────╢");
	    System.out.printf(   "║ Cancellation Rate  (%%) : %-24.1f%% ║\n", cancellationRate);
	    System.out.printf(   "║ Reliability Score  (%%) : %-24.1f%% ║\n", reliabilityScore);
	    System.out.printf(   "║ Overall Rating         : %-25s ║\n", reliabilityRating);
	    System.out.println(  "╟────────────────────────────────────────────────────╢");
	    System.out.printf(   "║ Most Booked Facility   : %-25s ║\n", mostUsedFacility + " (" + maxCount + "time(s))");
	    System.out.println(  "╚════════════════════════════════════════════════════╝");
	}
}