package my.edu.utar.service;

import my.edu.utar.model.Booking;
import java.util.ArrayList;

public class NotificationService {
	
	public void showReminders(String userID, ArrayList<Booking> bookingList) {
		boolean hasReminder = false;
		System.out.println("==== Reminder ====");
		
		for (Booking b: bookingList) {
			if(b.getUserID().equals(userID) && b.getStatus().equals("Approved")) {
				System.out.println("Reminder:");
                System.out.println("Your booking is coming soon:");
                System.out.println("Facility ID : " + b.getFacilityID());
                System.out.println("Date        : " + b.getBookingDate());
                System.out.println("Time Slot   : " + b.getTimeSlot());
                System.out.println("----------------------------");
                hasReminder = true;
			}
		}
		if(!hasReminder) {
			System.out.println("There is no booking.");
		}
	}
}
