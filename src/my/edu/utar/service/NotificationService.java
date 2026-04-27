package my.edu.utar.service;

import my.edu.utar.model.Booking;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

//Shows upcoming booking reminders and maintenance conflict alerts on login
public class NotificationService {
	public void showReminders(String userID, ArrayList<Booking> bookingList, BookingManager bm) {
	    LocalDate today = LocalDate.now();
	    DateTimeFormatter rawFormat = DateTimeFormatter.ofPattern("ddMMyyyy");
	    DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("dd MMM yyyy");

	    List<Booking> upcomingBookings = bookingList.stream()
	        .filter(b -> b.getUserID().equals(userID))
	        .filter(b -> b.getStatus().equalsIgnoreCase("Approved") || b.getStatus().equalsIgnoreCase("Pending"))
	        .filter(b -> {
	            try {
	                return !LocalDate.parse(b.getBookingDate(), rawFormat).isBefore(today);
	            } catch (Exception e) { return false; }
	        })
	        .limit(3) 
	        .collect(java.util.stream.Collectors.toList());

	    if (upcomingBookings.isEmpty()) {
	        System.out.println("  --- UPCOMING BOOKINGS & ALERTS ---");
	        System.out.println("  >> No upcoming bookings found.");
	        return;
	    }

	    List<my.edu.utar.model.MaintenanceReport> activeMaint = my.edu.utar.data.FileManager.loadAllMaintenanceReports().stream()
	        .filter(r -> !r.getStatus().equalsIgnoreCase(my.edu.utar.util.Constants.MAINT_RESOLVED))
	        .collect(java.util.stream.Collectors.toList());

	    my.edu.utar.service.FacilitiesService fs = new my.edu.utar.service.FacilitiesService();
	    List<my.edu.utar.model.Facility> allFacilities = fs.getAllFacilities();

	    System.out.println("  --- UPCOMING BOOKINGS & ALERTS ---");
	    System.out.println("  ┌───────────┬────────────────┬────────────────┬─────────────────┐");
	    System.out.printf("  │ %-9s │ %-14s │ %-14s │ %-15s │\n", "Room", "Date", "Time Slot", "Status");
	    System.out.println("  ├───────────┼────────────────┼────────────────┼─────────────────┤");

	    boolean maintConflict = false;

	    for (Booking b : upcomingBookings) {
	        LocalDate bDate = LocalDate.parse(b.getBookingDate(), rawFormat);

	        String roomNo = allFacilities.stream()
	            .filter(f -> f.getFacilityID().equals(b.getFacilityID()))
	            .map(my.edu.utar.model.Facility::getRoomNo)
	            .findFirst().orElse(b.getFacilityID());

	        boolean isUnderMaint = activeMaint.stream()
	            .anyMatch(r -> r.getFacilityID().equals(b.getFacilityID()));

	        String displayStatus = b.getStatus().toUpperCase();
	        if (isUnderMaint) {
	            maintConflict = true;
	            displayStatus = "!! MAINTENANCE";
	            
	            if (bDate.equals(today)) {
	                b.setStatus(my.edu.utar.util.Constants.STATUS_REJECTED);
	                bm.updateBooking(b);
	                displayStatus = "REJECTED";
	            }
	        }

	        System.out.printf("  │ %-9s │ %-14s │ %-14s │ %-15s │\n", 
	            roomNo, 
	            bDate.format(displayFormat), 
	            my.edu.utar.util.Constants.TIME_SLOTS[b.getTimeSlot()].split("-")[0].trim(), 
	            displayStatus);
	    }
	    System.out.println("  └───────────┴────────────────┴────────────────┴─────────────────┘");

	    if (maintConflict) {
	        System.out.println("\n  [!] WARNING: Some venues are currently under maintenance.");
	        System.out.print("      Would you like to change venue now? (Y/N): ");
	        java.util.Scanner sc = new java.util.Scanner(System.in);
	        if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
	            System.out.println("      >> Please select option [4] to modify your booking.");
	        }
	    }
	}
}