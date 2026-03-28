package my.edu.utar.ui;

import my.edu.utar.service.BookingManager;
import my.edu.utar.service.NotificationService;
import my.edu.utar.service.ReportGenerator;
import my.edu.utar.model.Facility;
import my.edu.utar.model.Booking;
import my.edu.utar.data.FileManager;
import my.edu.utar.model.User;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Scanner;

/**
 * UserMenu.java
 * The main menu page for logged-in Students and Staff.
 *
 * Member 1 owns: updateProfile(), reminderBooking() display
 * Member 2 owns: searchAvailableFacility(), newBooking(),
 *                modifyBooking(), viewBookingRequestStatus()
 * Member 3 owns: reportIssue()
 * Member 4 owns: viewBookingHistory() summary portion
 */
public class UserMenu {

    private Scanner sc;
    private User currentUser;
    private BookingManager bookingManager;

    public UserMenu(Scanner sc, User currentUser, BookingManager bookingManager ) {
        this.sc = sc;
        this.currentUser = currentUser;
        this.bookingManager = bookingManager;
    }

    // ===================== MAIN USER MENU =====================
    public void show() {
        while (true) {
            showReminders();     // Always show reminders at top
            printUserMenu();

            String choice = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }

            switch (choice) {
                case "1": updateProfile();              break;
                case "2": searchAvailableFacility();    break;
                case "3": newBooking();                 break;
                case "4": modifyBooking();              break;
                case "5": viewBookingRequestStatus();   break;
                case "6": reportIssue();                break;
                case "7": viewBookingHistory();         break;
                case "L":
                    System.out.println("Logged out successfully. Goodbye, " + currentUser.getName() + "!");
                    return;
                default:
                    System.out.println("Invalid selection, please try again.");
            }
        }
    }

    private void printUserMenu() {
        System.out.println("\n============================================");
        System.out.println("   Welcome, " + currentUser.getName());
        System.out.println("   Role: " + currentUser.getRole());
        System.out.println("============================================");
        System.out.println("[1] Update Profile");
        System.out.println("[2] Search Available Facility");
        System.out.println("[3] New Booking");
        System.out.println("[4] Modify / Cancel Booking");
        System.out.println("[5] View Booking Request Status");
        System.out.println("[6] Report Issue");
        System.out.println("[7] View Booking History & Summary");
        System.out.println("[L] Logout");
        System.out.println("--------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    // ===================== MEMBER 1: SHOW REMINDERS =====================
    /**
     * Displays upcoming approved bookings as reminders.
     * TODO Member 1: Implement this method.
     * Logic:
     *   - Read bookings.txt
     *   - Filter by currentUser.getId() AND status = Approved
     *   - Show bookings where booking date is today or within next 3 days
     *   - If none: show "No upcoming reminders."
     */
    private void showReminders() {
        System.out.println("\n--- REMINDERS ---");
        NotificationService ns = new NotificationService();
        ns.showReminders(currentUser.getId(), bookingManager.getBookingList());
        System.out.println("-----------------");
    }

    // ===================== MEMBER 1: UPDATE PROFILE =====================
    /**
     * Allows user to update editable fields.
     * Role, ID, and Email are NOT editable.
     * TODO Member 1: Implement this method.
     */
    private void updateProfile() {
        System.out.println("\n========== UPDATE PROFILE ==========");
        currentUser.displayProfile();

        while (true) {
            System.out.println("\nWhat would you like to update?");
            System.out.println("[1] Name");
            System.out.println("[2] Phone Number");
            System.out.println("[3] Faculty");
            System.out.println("[4] Programme / Department");
            System.out.println("[5] Password");
            System.out.println("[B] Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim().toUpperCase();
            if (Validator.isEmpty(choice)) { System.out.println("Cannot be empty."); continue; }
            if ("B".equals(choice)) return;

            switch (choice) {
                case "1":
                    // TODO: update name (validate non-empty, UPPERCASE)
                    System.out.println("[TODO] Update name");
                    break;
                case "2":
                    // TODO: update phone (validate phone format)
                    System.out.println("[TODO] Update phone");
                    break;
                case "3":
                    // TODO: update faculty (show list, choose)
                    System.out.println("[TODO] Update faculty");
                    break;
                case "4":
                    // TODO: update programme/department (based on faculty)
                    System.out.println("[TODO] Update programme/department");
                    break;
                case "5":
                    // TODO: update password (validate min 8, confirm twice)
                    System.out.println("[TODO] Update password");
                    break;
                default:
                    System.out.println("Invalid selection, please try again.");
            }

            // After each update: call FileManager.updateUser(currentUser)
        }
    }

    // ===================== MEMBER 2: SEARCH FACILITY =====================
    /**
     * Multi-step facility search.
     * TODO Member 2: Implement this method.
     * Steps: Block > Facility Type > Floor > Room > Date > Time Slot
     */
    private void searchAvailableFacility() {
        System.out.println("\n========== SEARCH AVAILABLE FACILITY ==========");

        // Step 0: Load all facilities
        List<Facility> facilities = FileManager.loadAllFacilities();
        if (facilities.isEmpty()) {
            System.out.println("No facilities found in the system.");
            return;
        }

        Scanner sc = this.sc;

        // ---------------- Step 1: Block ----------------
        Set<String> blocks = new HashSet<>();
        for (Facility f : facilities) blocks.add(f.getBlock());
        System.out.println("Available Blocks: " + blocks);
        System.out.print("Select Block: ");
        String block = sc.nextLine().trim();
        facilities = facilities.stream()
                .filter(f -> f.getBlock().equalsIgnoreCase(block))
                .collect(Collectors.toList());
        if (facilities.isEmpty()) {
            System.out.println("No facilities found for this block.");
            return;
        }

        // ---------------- Step 2: Facility Type ----------------
        Set<String> types = new HashSet<>();
        for (Facility f : facilities) types.add(f.getType());
        System.out.println("Available Facility Types: " + types);
        System.out.print("Select Facility Type: ");
        String type = sc.nextLine().trim();
        facilities = facilities.stream()
                .filter(f -> f.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
        if (facilities.isEmpty()) {
            System.out.println("No facilities found for this type.");
            return;
        }

        // ---------------- Step 3: Floor ----------------
        Set<String> floors = new HashSet<>();
        for (Facility f : facilities) floors.add(f.getFloor());
        System.out.println("Available Floors: " + floors);
        System.out.print("Select Floor: ");
        String floor = sc.nextLine().trim();
        facilities = facilities.stream()
                .filter(f -> f.getFloor().equalsIgnoreCase(floor))
                .collect(Collectors.toList());
        if (facilities.isEmpty()) {
            System.out.println("No facilities found for this floor.");
            return;
        }

        // ---------------- Step 4: Room ----------------
        Set<String> rooms = new HashSet<>();
        for (Facility f : facilities) rooms.add(f.getRoomNo());
        System.out.println("Available Rooms: " + rooms);
        System.out.print("Select Room: ");
        String room = sc.nextLine().trim();
        facilities = facilities.stream()
                .filter(f -> f.getRoomNo().equalsIgnoreCase(room))
                .collect(Collectors.toList());
        if (facilities.isEmpty()) {
            System.out.println("No facilities found for this room.");
            return;
        }

        // ---------------- Step 5: Date ----------------
        System.out.print("Enter Booking Date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();
        facilities = bookingManager.getAvailableFacilities(facilities, date);
        if (facilities.isEmpty()) {
            System.out.println("No facilities available on this date.");
            return;
        }

        // ---------------- Step 6: Time Slot ----------------
        System.out.print("Enter Time Slot (e.g., 09:00-10:00): ");
        String timeSlot = sc.nextLine().trim();
        facilities = bookingManager.getAvailableFacilities(facilities, date, timeSlot);
        if (facilities.isEmpty()) {
            System.out.println("No facilities available at this time slot.");
            return;
        }

        // ---------------- Step 7: Display Results ----------------
        System.out.println("\n--- AVAILABLE FACILITIES ---");
        for (Facility f : facilities) {
            f.display(); // use display() method from Facility class
        }
        System.out.println("----------------------------");
    }

    // ===================== MEMBER 2: NEW BOOKING =====================
    /**
     * Creates a new booking after facility search.
     * TODO Member 2: Implement this method.
     */
    private String generateBookingID() {
        // Format: B + YYYYMMDD + 4-digit sequence
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        int seq = bookingManager.getBookingList().size() + 1; // simple sequence
        return String.format("B%s%04d", date, seq);
    }
    
    private Facility searchAvailableFacilityForBooking() {
        System.out.println("\n========== SEARCH AVAILABLE FACILITY ==========");

        List<Facility> facilities = FileManager.loadAllFacilities();
        if (facilities.isEmpty()) {
            System.out.println("No facilities found in the system.");
            return null;
        }

        // Step 1: Block
        Set<String> blocks = new HashSet<>();
        for (Facility f : facilities) blocks.add(f.getBlock());
        System.out.println("Available Blocks: " + blocks);
        System.out.print("Select Block: ");
        String block = sc.nextLine().trim();
        facilities = facilities.stream()
                .filter(f -> f.getBlock().equalsIgnoreCase(block))
                .collect(Collectors.toList());
        if (facilities.isEmpty()) return null;

        // Step 2: Facility Type
        Set<String> types = new HashSet<>();
        for (Facility f : facilities) types.add(f.getType());
        System.out.println("Available Facility Types: " + types);
        System.out.print("Select Facility Type: ");
        String type = sc.nextLine().trim();
        facilities = facilities.stream()
                .filter(f -> f.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
        if (facilities.isEmpty()) return null;

        // Step 3: Floor
        Set<String> floors = new HashSet<>();
        for (Facility f : facilities) floors.add(f.getFloor());
        System.out.println("Available Floors: " + floors);
        System.out.print("Select Floor: ");
        String floor = sc.nextLine().trim();
        facilities = facilities.stream()
                .filter(f -> f.getFloor().equalsIgnoreCase(floor))
                .collect(Collectors.toList());
        if (facilities.isEmpty()) return null;

        // Step 4: Room
        Set<String> rooms = new HashSet<>();
        for (Facility f : facilities) rooms.add(f.getRoomNo());
        System.out.println("Available Rooms: " + rooms);
        System.out.print("Select Room: ");
        String room = sc.nextLine().trim();
        facilities = facilities.stream()
                .filter(f -> f.getRoomNo().equalsIgnoreCase(room))
                .collect(Collectors.toList());
        if (facilities.isEmpty()) return null;

        // Step 5: Display available facilities
        System.out.println("\n--- AVAILABLE FACILITIES ---");
        for (int i = 0; i < facilities.size(); i++) {
            System.out.print("[" + (i + 1) + "] ");
            facilities.get(i).display();
        }

        System.out.print("Select a facility by number: ");
        int choice = Integer.parseInt(sc.nextLine().trim()) - 1;
        if (choice < 0 || choice >= facilities.size()) {
            System.out.println("Invalid selection.");
            return null;
        }
        return facilities.get(choice);
    }
    
    private void newBooking() {
    	System.out.println("\n--- NEW BOOKING ---");
        
        // 1. Call searchAvailableFacility() to select facility
        Facility selectedFacility = searchAvailableFacilityForBooking(); 
        
        // 2. Ask user for date, time, duration
        System.out.print("Enter booking date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();

        int startHour = -1;
        while (startHour < 0 || startHour > 23) {
            System.out.print("Enter start hour (0-23): ");
            try {
                startHour = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                startHour = -1; // reset on invalid input
            }
            if (startHour < 0 || startHour > 23) {
                System.out.println("Invalid input. Please enter a number between 0 and 23.");
            }
        }
        
        // 3. Create Booking object
        Booking b = new Booking(
        	    generateBookingID(),                     // bookingID
        	    currentUser.getId(),                     // userID
        	    selectedFacility.getFacilityID(),        // facilityID
        	    java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")), // applyDate
        	    date,                                    // bookingDate (user input)
        	    startHour,                               // timeSlot
        	    "General Purpose",                       // purpose (or ask user)
        	    1,                                       // pax (number of people)
        	    "Pending",                               // status
        	    ""                                       // rejectReason
        	);

        // 4. Add booking via BookingManager
        bookingManager.addBooking(b);

        System.out.println("Booking request submitted successfully!");
    }

    // ===================== MEMBER 2: MODIFY/CANCEL BOOKING =====================
    /**
     * Shows pending bookings, allows modify or cancel.
     * TODO Member 2: Implement this method.
     */
    private void modifyBooking() {
    	 List<Booking> userBookings = bookingManager.getBookingsByUser(currentUser.getId());

    	    if (userBookings.isEmpty()) {
    	        System.out.println("No pending bookings to modify.");
    	        return;
    	    }

    	    // Display bookings with index
    	    for (int i = 0; i < userBookings.size(); i++) {
    	    	System.out.print("[" + (i+1) + "] ");
    	    	userBookings.get(i).display(); // uses display() from Booking
    	    	System.out.println("----------------------------");
    	    }

    	    System.out.print("Select booking to modify/cancel: ");
    	    int choice = Integer.parseInt(sc.nextLine().trim()) - 1;

    	    Booking selected = userBookings.get(choice);

    	    System.out.println("[1] Modify");
    	    System.out.println("[2] Cancel");
    	    String action = sc.nextLine().trim();

    	    if ("2".equals(action)) {
    	        bookingManager.cancelBooking(selected.getBookingID());
    	        System.out.println("Booking cancelled.");
    	    } else if ("1".equals(action)) {
    	        // Ask new date/time and update
    	        System.out.print("Enter new date (YYYY-MM-DD): ");
    	        selected.setBookingDate(sc.nextLine().trim());

    	        System.out.print("Enter new start hour (0-23): ");
    	        selected.setTimeSlot(Integer.parseInt(sc.nextLine().trim()));

    	        bookingManager.updateBooking(selected);
    	        System.out.println("Booking modified successfully!");
    	    }
    }

    // ===================== MEMBER 2: VIEW BOOKING STATUS =====================
    /**
     * Shows all upcoming booking requests and their status.
     * TODO Member 2: Implement this method.
     */
    private void viewBookingRequestStatus() {
    	List<Booking> userBookings = bookingManager.getBookingsByUser(currentUser.getId());

        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings.");
            return;
        }

        System.out.println("\n--- YOUR BOOKINGS ---");
        for (Booking b : userBookings) {
            System.out.println(b);
        }
    }

    // ===================== MEMBER 3: REPORT ISSUE =====================
    /**
     * User selects a facility and reports a maintenance issue.
     * TODO Member 3: Implement this method.
     */
    private void reportIssue() {
        System.out.println("[TODO - Member 3] Report Issue");
    }

    // ===================== MEMBER 4: VIEW BOOKING HISTORY =====================
    /**
     * Shows past (approved/rejected) bookings and personal summary.
     * TODO Member 4: Implement summary analytics portion.
     * TODO Member 2: Implement the booking list display portion.
     */
    private void viewBookingHistory() {
    	System.out.println("\n--- BOOKING HISTORY ---");
    	bookingManager.viewUserBookings(currentUser.getId());
    	
    	ReportGenerator rg = new ReportGenerator();
        rg.generateUserReport(currentUser.getId(), bookingManager.getBookingList());

    }
}
