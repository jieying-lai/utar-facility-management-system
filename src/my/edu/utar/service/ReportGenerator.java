package my.edu.utar.service;

import java.util.List; 
import java.util.ArrayList;
import my.edu.utar.model.Booking; 
import my.edu.utar.model.Facility; 

public class ReportGenerator {
	
	public void generateUserReport(String userID, my.edu.utar.model.Booking[] allBookings, my.edu.utar.model.Facility[] allFacilities) {
	    int totalBookings = 0;
	    int totalHours = 0;

	    String[] types = new String[10];
	    int[] typeCounts = new int[10];
	    int uniqueTypeCount = 0;

	    for (int i = 0; i < allBookings.length; i++) {
	        my.edu.utar.model.Booking b = allBookings[i];
	        
	        if (b != null && b.getUserID().equals(userID) && b.getStatus().equalsIgnoreCase("Approved")) {
	            totalBookings++;
	            totalHours += 2; 

	            String currentType = "Unknown";
	            for (int j = 0; j < allFacilities.length; j++) {
	                if (allFacilities[j] != null && allFacilities[j].getFacilityID().equals(b.getFacilityID())) {
	                    currentType = allFacilities[j].getType(); 
	                    break;
	                }
	            }

	            boolean found = false;
	            for (int k = 0; k < uniqueTypeCount; k++) {
	                if (types[k].equals(currentType)) {
	                    typeCounts[k]++;
	                    found = true;
	                    break;
	                }
	            }
	            if (!found && uniqueTypeCount < 10) {
	                types[uniqueTypeCount] = currentType;
	                typeCounts[uniqueTypeCount] = 1;
	                uniqueTypeCount++;
	            }
	        }
	    }

	    String mostUsedType = "None";
	    int max = 0;
	    for (int i = 0; i < uniqueTypeCount; i++) {
	        if (typeCounts[i] > max) {
	            max = typeCounts[i];
	            mostUsedType = types[i];
	        }
	    }

	    System.out.println("\n===== PERSONAL BOOKING SUMMARY =====");
	    System.out.println("Total Approved Bookings : " + totalBookings);
	    System.out.println("Total Hours Booked      : " + totalHours + " hours"); // [cite: 138]
	    System.out.println("Most Frequently Booked  : " + mostUsedType); // [cite: 139]
	    System.out.println("------------------------------------");
	}

    
    public void generateAdminSummaryReport(Booking[] allBookings, Facility[] allFacilities, String period) {
        System.out.println("\n--- Admin Analytics Report (" + period + ") ---");
        
        for (Facility f : allFacilities) {
            if (f == null) continue;
            int bookedCount = 0;
            String fID = f.getFacilityID();

            for (Booking b : allBookings) {
                if (b != null && b.getFacilityID().equals(fID) && 
                    b.getBookingDate().contains(period) && 
                    b.getStatus().equalsIgnoreCase("Approved")) {
                    bookedCount++;
                }
            }

            double utilization = (bookedCount / 20.0) * 100;
            System.out.printf("Facility: %-10s | Utilization: %5.1f%%", fID, utilization);
            
            if (bookedCount == 0) {
                System.out.print(" [UNDERUSED]");
            }
            System.out.println();
        }

        showTop5Users(allBookings);
    }
    
	 public void showTop5Users(my.edu.utar.model.Booking[] allBookings) {
	     String[] userIDs = new String[allBookings.length];
	     int[] counts = new int[allBookings.length];
	     int uniqueUserCount = 0;

	     for (int i = 0; i < allBookings.length; i++) {
	         if (allBookings[i] == null || !allBookings[i].getStatus().equalsIgnoreCase("Approved")) continue;

	         String id = allBookings[i].getUserID();
	         boolean found = false;
	         for (int j = 0; j < uniqueUserCount; j++) {
	             if (userIDs[j].equals(id)) {
	                 counts[j]++;
	                 found = true;
	                 break;
	             }
	         }
	         if (!found) {
	             userIDs[uniqueUserCount] = id;
	             counts[uniqueUserCount] = 1;
	             uniqueUserCount++;
	         }
	     }

	     for (int i = 0; i < uniqueUserCount - 1; i++) {
	         for (int j = 0; j < uniqueUserCount - i - 1; j++) {
	             if (counts[j] < counts[j + 1]) {
	                 int tempCount = counts[j];
	                 counts[j] = counts[j + 1];
	                 counts[j + 1] = tempCount;
	                 String tempID = userIDs[j];
	                 userIDs[j] = userIDs[j + 1];
	                 userIDs[j + 1] = tempID;
	             }
	         }
	     }

	     System.out.println("\n--- Top 5 Most Active Users ---");
	     for (int i = 0; i < 5 && i < uniqueUserCount; i++) {
	         System.out.println((i + 1) + ". User ID: " + userIDs[i] + " (" + counts[i] + " bookings)");
	     }
	 }

}
