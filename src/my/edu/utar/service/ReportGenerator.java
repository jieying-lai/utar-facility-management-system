package my.edu.utar.service;

import my.edu.utar.model.Booking;
import my.edu.utar.model.Facility;
import my.edu.utar.data.FileManager;
import java.util.ArrayList;
import java.util.HashMap;

//Generates the personal usage analytics summary for a user
public class ReportGenerator {

    public void generateUserReport(String userID, ArrayList<Booking> bookingList) {
        int totalBookings = 0;
        int cancelledBookings = 0;
        int approvedBookings = 0;
        int totalHoursBooked = 0;
        HashMap<String, Integer> facilityTypeCount = new HashMap<>();

        for (Booking b : bookingList) {
            if (b.getUserID().equals(userID)) {
                totalBookings++;

                if (b.getStatus().equalsIgnoreCase("Cancelled")) {
                    cancelledBookings++;
                } else if (b.getStatus().equalsIgnoreCase("Approved")) {
                    approvedBookings++;
                    totalHoursBooked += 2;
                }

                Facility facility = FileManager.getFacilityById(b.getFacilityID());
                String facilityType = (facility != null) ? facility.getType() : b.getFacilityID();
                facilityTypeCount.put(facilityType, facilityTypeCount.getOrDefault(facilityType, 0) + 1);
            }
        }

        double cancellationRate = (totalBookings > 0) ? ((double) cancelledBookings / totalBookings) * 100 : 0;
        double reliabilityScore = 100 - cancellationRate;

        String reliabilityRating;
        if (totalBookings == 0)          reliabilityRating = "N/A (No History)";
        else if (reliabilityScore >= 90) reliabilityRating = "EXCELLENT";
        else if (reliabilityScore >= 70) reliabilityRating = "GOOD";
        else                             reliabilityRating = "NEEDS IMPROVEMENT";

        String mostUsedType = "None";
        int maxCount = 0;
        for (String type : facilityTypeCount.keySet()) {
            if (facilityTypeCount.get(type) > maxCount) {
                maxCount = facilityTypeCount.get(type);
                mostUsedType = type;
            }
        }

        System.out.println("\n╔═════════════════════════════════════════════════════════╗");
        System.out.println(  "║                PERSONAL USAGE ANALYTICS                 ║");
        System.out.println(  "╠═════════════════════════════════════════════════════════╣");
        System.out.printf(   "║ Total Bookings Made    : %-30d ║%n", totalBookings);
        System.out.printf(   "║ Completed/Approved     : %-30d ║%n", approvedBookings);
        System.out.printf(   "║ Total Cancellations    : %-30d ║%n", cancelledBookings);
        System.out.printf(   "║ Total Hours Booked     : %-26d hrs ║%n", totalHoursBooked);
        System.out.println(  "╟─────────────────────────────────────────────────────────╢");
        System.out.printf(   "║ Cancellation Rate  (%%) : %-29.1f%% ║%n", cancellationRate);
        System.out.printf(   "║ Reliability Score  (%%) : %-29.1f%% ║%n", reliabilityScore);
        System.out.printf(   "║ Overall Rating         : %-30s ║%n", reliabilityRating);
        System.out.println(  "╟─────────────────────────────────────────────────────────╢");
        System.out.printf(   "║ Most Used Facility Type: %-30s ║%n",
                             mostUsedType + " (" + maxCount + " time(s))");
        System.out.println(  "╚═════════════════════════════════════════════════════════╝");
    }
}