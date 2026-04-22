package my.edu.utar.service;

import my.edu.utar.model.Booking;
import my.edu.utar.model.Facility;
import my.edu.utar.data.FileManager;
import my.edu.utar.util.Constants;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class BookingManager {
	
	private ArrayList<Booking> bookingList;
	
	public BookingManager() {
		bookingList = new ArrayList<>();
		loadFromFile();
		
	}
	
	public ArrayList<Booking> getBookingList(){
		return bookingList;
	}
	
	public boolean createBooking(Booking booking) {
	    if (booking.getPurpose() == null || booking.getPurpose().trim().isEmpty()) {
	        System.out.println("Purpose cannot be empty!");
	        return false;
	    }

	    if (booking.getPax() <= 0) {
	        System.out.println("Number of people must be greater than 0!");
	        return false;
	    }

	    if (!isTimeSlotAvailable(booking.getFacilityID(),booking.getBookingDate(),booking.getTimeSlot())) {
	        System.out.println("Time slot is not available!");
	        return false;
	    }
	    booking.setStatus("Pending");
	    saveToFile();
	    bookingList.add(booking);
	    System.out.println("Booking created successfully (Pending approval)");
	    return true;
	}
	
	public boolean addBooking(Booking booking) {
		
		if(!isTimeSlotAvailable(booking.getFacilityID(),booking.getBookingDate(),booking.getTimeSlot()))
		{
			System.out.println("Time slot not available");
			return false;
		}
		bookingList.add(booking);
		saveToFile();
		return true;
	}
	
	public Booking findBooking(String bookingID) {
		for(Booking b: bookingList) {
			if(b.getBookingID().equals(bookingID)) {
				return b;
			}
		}
		return null;
	}

	public boolean cancelBooking(String bookingID) {
		Booking booking=findBooking(bookingID);
		if(booking !=null && booking.getStatus().equals("Pending")) {
			booking.setStatus("Cancelled");
			saveToFile();
			return true;
		}
		return false;
	}
	
	public boolean modifyBooking(String bookingID, String newDate, int newTimeSlot) {
		Booking booking=findBooking(bookingID);
		if(booking!=null && booking.getStatus().equals("Pending")) {
			booking.setBookingDate(newDate);
			booking.setTimeSlot(newTimeSlot);
			saveToFile();
			return true;
		}
		System.out.println("Only Pending bookings can be modified.");
		return false;
	}

	public boolean rejectBooking(String bookingID, String reason) {
		Booking booking=findBooking(bookingID);
		if(booking!=null) {
			booking.setStatus("Rejected");
			booking.setRejectReason(reason);
			saveToFile();
			return true;
		}
		return false;
	}
	
	public boolean approveBooking(String bookingID) {
	    Booking b = findBooking(bookingID);
	    if (b != null) {
	        b.setStatus("Approved");
	        saveToFile();
	        return true;
	    }
	    return false;
	}

	public void viewUserBookings(String userID) {
		for(Booking b: bookingList) {
			if(b.getUserID().equals(userID)) {
				b.display();
				System.out.println("--------------------------");
			}
		}
	}
	
	public void displayAllBookings() {
		for(Booking b: bookingList) {
			b.display();
			System.out.println("----------------------------");
		}
	}
	
	public boolean isTimeSlotAvailable(String facilityID, String date, int timeSlot) {
	    for (Booking b : bookingList) {

	        if (b.getFacilityID().equals(facilityID) && b.getBookingDate().equals(date) &&
	            b.getTimeSlot() == timeSlot && (b.getStatus().equals("Approved") || b.getStatus().equals("Pending"))) {

	            return false;
	        }
	    }
	    return true;
	}
	
	
	public void viewUpcomingBookings(String userID) {
        for (Booking b : bookingList) {
            if (b.getUserID().equals(userID) &&
               (b.getStatus().equalsIgnoreCase("Pending") || b.getStatus().equalsIgnoreCase("Approved"))) {
                b.display();
                System.out.println("----------------------------");
            }
        }
    }
	
	public boolean isSlotAvailable(String facilityID, String date, int slot) {
	    return isTimeSlotAvailable(facilityID, date, slot);
	}
	
	public List<Facility> getAvailableFacilities(List<Facility> facilities, String date, int slot) {
	    List<Facility> available = new ArrayList<>();
	    for (Facility f : facilities) {
	        // Use your existing isTimeSlotAvailable method here!
	        if (isTimeSlotAvailable(f.getFacilityID(), date, slot) && f.getStatus().equalsIgnoreCase("Available")) {
	            available.add(f);
	        }
	    }
	    return available;
	}

	public List<Facility> getAvailableFacilities(List<Facility> facilities, String date, String timeSlotStr) {
	    List<Facility> available = new ArrayList<>();
	    int timeSlot = -1;

	    for (int i = 0; i < Constants.TIME_SLOTS.length; i++) {
	        if (Constants.TIME_SLOTS[i].equals(timeSlotStr)) {
	            timeSlot = i;
	            break;
	        }
	    }
	    if (timeSlot == -1) return available; 

	    for (Facility f : facilities) {
	        boolean isBooked = false;
	        for (Booking b : bookingList) {
	            if (b.getFacilityID().equals(f.getFacilityID()) &&
	                b.getBookingDate().equals(date) &&
	                b.getTimeSlot() == timeSlot &&
	                (b.getStatus().equalsIgnoreCase("Approved") || b.getStatus().equalsIgnoreCase("Pending"))) {
	                isBooked = true;
	                break;
	            }
	        }
	        if (!isBooked && f.getStatus().equalsIgnoreCase("Available")) {
	            available.add(f);
	        }
	    }
	    return available;
	}
	
    public void viewBookingHistory(String userID) {
        for (Booking b : bookingList) {
            if (b.getUserID().equals(userID) &&
               (b.getStatus().equalsIgnoreCase("Approved") || b.getStatus().equalsIgnoreCase("Rejected"))) {
                b.display();
                System.out.println("----------------------------");
            }
        }
    }
    
    public boolean updateBooking(Booking booking) {
        for (int i = 0; i < bookingList.size(); i++) {
            if (bookingList.get(i).getBookingID().equals(booking.getBookingID())) {
                bookingList.set(i, booking);
                saveToFile();
                return true;
            }
        }
        return false;
    }
    
    public List<Booking> getPendingBookingsSorted() {
        List<Booking> pending = new ArrayList<>();
        for (Booking b : bookingList) {
            if (b.getStatus().equalsIgnoreCase("Pending")) {
                pending.add(b);
            }
        }
        pending.sort((b1, b2) -> b1.getApplyDate().compareTo(b2.getApplyDate()));
        return pending;
    }
    
    public boolean hasConflict(Booking request) {
        for (Booking b : bookingList) {
            if (b.getStatus().equalsIgnoreCase("Approved") &&
                b.getFacilityID().equals(request.getFacilityID()) &&
                b.getBookingDate().equals(request.getBookingDate()) &&
                b.getTimeSlot() == request.getTimeSlot()) {
                return true; 
            }
        }
        return false;
    }
    
    public int rejectAllPendingForFacility(String facilityID, String reason) {
        int count = 0;
        for (Booking b : bookingList) {
            if (b.getFacilityID().equalsIgnoreCase(facilityID) && b.getStatus().equalsIgnoreCase("Pending")) {
                b.setStatus("Rejected");
                b.setRejectReason(reason);
                count++;
            }
        }
        if (count > 0) saveToFile();
        return count;
    }

    public boolean hasApprovedBookings(String facilityID) {
        for (Booking b : bookingList) {
            if (b.getFacilityID().equalsIgnoreCase(facilityID) && b.getStatus().equalsIgnoreCase("Approved")) {
                return true;
            }
        }
        
        return false; 
    }
    
    public boolean hasFutureBookings(String userId) {
        LocalDate today = LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy");
        
        for (Booking b : bookingList) {
            if (b.getUserID().equals(userId)) {
                try {
                    LocalDate bookingDate = LocalDate.parse(b.getBookingDate(), formatter); 
                    if (!bookingDate.isBefore(today) && 
                        !b.getStatus().equals(Constants.STATUS_CANCELLED)) {
                        return true;
                    }
                } catch (Exception e) {
                    continue; 
                }
            }
        }
        return false;
    }
    
    public List<Booking> getBookingsByUser(String userID) {
        List<Booking> list = new ArrayList<>();
        for (Booking b : bookingList) {
            if (b.getUserID().equals(userID)) {
                list.add(b); 
            }
        }
        return list;
    }
    
    public void loadFromFile() {
        bookingList = new ArrayList<>();
        List<String> lines = FileManager.readAllLines(Constants.FILE_BOOKINGS);
        for (String line : lines) {
            try {
                String[] data = line.split(Constants.DELIMITER_REGEX, -1);
                Booking b = new Booking();
                b.setBookingID(data[0]);
                b.setUserID(data[1]);
                b.setFacilityID(data[2]);
                b.setApplyDate(data[3]);
                b.setBookingDate(data[4]);
                b.setTimeSlot(Integer.parseInt(data[5]));
                b.setPurpose(data[6]);
                b.setPax(Integer.parseInt(data[7]));
                b.setStatus(data[8]);
                b.setRejectReason(data.length > 9 ? data[9] : "");
                bookingList.add(b);
            } catch (Exception e) {
                System.out.println("[WARNING] Error parsing booking: " + line);
            }
        }
    }
	
    public boolean isAvailable(String facilityID, String date, int slot) {
        // Make sure you are calling the method or variable that holds your data
        List<Booking> bookings = getBookingList(); 
        
        for (Booking b : bookings) {
            if (b.getFacilityID().equals(facilityID) && 
                b.getBookingDate().equals(date) && 
                b.getTimeSlot() == slot) {
                
                String status = b.getStatus();
                if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Confirmed")) {
                    return false; 
                }
            }
        }
        return true;
    }
    
    private void saveToFile() {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookingList) {
            lines.add(b.toFileString());
        }
        FileManager.writeAllLines(Constants.FILE_BOOKINGS, lines);
    }
}
