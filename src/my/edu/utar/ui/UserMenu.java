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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Scanner;

public class UserMenu {

    private Scanner sc;
    private User currentUser;
    private BookingManager bookingManager;

    public UserMenu(Scanner sc, User currentUser, BookingManager bookingManager ) {
        this.sc = sc;
        this.currentUser = currentUser;
        this.bookingManager = bookingManager;
    }

    public void show() {
        while (true) {
             
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
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("hh:mm a"));

        System.out.println("============================================");
        System.out.println("    UTAR Smart Campus Management System     ");
        System.out.println("============================================");
        System.out.println("  Date : " + currentDate);
        System.out.println("  Time : " + currentTime);
        System.out.println("\n  Welcome, " + currentUser.getName());
        System.out.println("  Role: " + currentUser.getRole());
        showReminders();
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

    private void showReminders() {
        System.out.println("\n--- REMINDERS ---");
        NotificationService ns = new NotificationService();
        ns.showReminders(currentUser.getId(), bookingManager.getBookingList());
        System.out.println("-----------------");
    }

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
                case "1": updateName();             break;
                case "2": updatePhone();            break;
                case "3": updateFaculty();          break;
                case "4": updateProgrammeDept();    break;
                case "5": updatePassword();         break;
                default:
                    System.out.println("Invalid selection, please try again.");
            }
        }
    }

    private void updateName() {
        while (true) {
            System.out.print("Enter new name: ");
            String input = sc.nextLine().trim();
            if (Validator.isEmpty(input)) { 
                System.out.println("Name cannot be empty."); 
                continue; 
            }
            currentUser.setName(input.toUpperCase());
            FileManager.updateUser(currentUser);
            System.out.println("Name updated successfully to: " + currentUser.getName());
            break;
        }
    }

    private void updatePhone() {
        while (true) {
            System.out.print("Enter new phone number (e.g. 0112345678): ");
            String input = sc.nextLine().trim();
            if (Validator.isEmpty(input)) { 
                System.out.println("Cannot be empty."); 
                continue; 
            }
            if (!Validator.isValidPhone(input)) {
                System.out.println("Invalid phone number. Format: 01xxxxxxxxx (10-11 digits)");
                continue;
            }
            currentUser.setPhone(input);
            FileManager.updateUser(currentUser);
            System.out.println("Phone number updated successfully to: " + currentUser.getPhone());
            break;
        }
    }

    private void updateFaculty() {
        while (true) {
            System.out.println("\nSelect new Faculty:");
            for (int i = 0; i < Constants.FACULTIES.length; i++) {
                System.out.println("[" + (i + 1) + "] " + Constants.FACULTIES[i]);
            }
            System.out.println("[B] Back");
            System.out.print("Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
            if ("B".equals(input)) return;
            if (!Validator.isValidMenuChoice(input, 1, Constants.FACULTIES.length)) {
                System.out.println("Invalid selection, please try again.");
                continue;
            }
            int index = Integer.parseInt(input) - 1;
            currentUser.setFaculty(Constants.FACULTIES[index]);
            FileManager.updateUser(currentUser);
            System.out.println("Faculty updated successfully to: " + currentUser.getFaculty());
            
            // Ask if they want to update programme too
            System.out.print("Do you want to update Programme/Department too? [Y/N]: ");
            String confirm = sc.nextLine().trim().toUpperCase();
            if ("Y".equals(confirm)) {
                updateProgrammeDept();
            }
            break;
        }
    }

    private void updateProgrammeDept() {
        String type = Constants.ROLE_STUDENT.equals(currentUser.getRole()) ? "Programme" : "Department";
        String example = Constants.ROLE_STUDENT.equals(currentUser.getRole()) ? "SE" : "IT";

        System.out.println("\nUpdate " + type);
        System.out.println("Enter your " + type + " abbreviation (e.g., " + example + "):");
        System.out.println("[B] Back");
        System.out.print("Enter choice: ");
        
        String input = sc.nextLine().trim();

        // 1. Check if user wants to go back
        if (input.equalsIgnoreCase("B")) return;

        // 2. Validate empty input
        if (Validator.isEmpty(input)) {
            System.out.println("Error: " + type + " cannot be empty.");
            return;
        }

        // 3. Store as Capitalized (Uppercase)
        String formattedInput = input.toUpperCase();
        
        currentUser.setProgramme(formattedInput);
        FileManager.updateUser(currentUser);
        
        System.out.println(type + " updated successfully to: " + formattedInput);
    }

    private void updatePassword() {
        while (true) {
            System.out.print("Enter current password: ");
            String oldPass = sc.nextLine().trim();
            if (Validator.isEmpty(oldPass)) { 
                System.out.println("Cannot be empty."); 
                continue; 
            }
            // Verify current password
            if (!oldPass.equals(currentUser.getPassword())) {
                System.out.println("Incorrect current password. Please try again.");
                continue;
            }
            System.out.print("Enter new password (min 8 characters): ");
            String newPass = sc.nextLine().trim();
            if (!Validator.isValidPassword(newPass)) {
                System.out.println("Password must be at least 8 characters.");
                continue;
            }
            System.out.print("Confirm new password: ");
            String confirmPass = sc.nextLine().trim();
            if (!Validator.passwordsMatch(newPass, confirmPass)) {
                System.out.println("Passwords do not match. Please try again.");
                continue;
            }
            currentUser.setPassword(newPass);
            FileManager.updateUser(currentUser);
            System.out.println("Password updated successfully!");
            break;
        }
    }
    
    

    // ===================== MEMBER 2: SEARCH FACILITY =====================
    /**
     * Multi-step facility search.
     * TODO Member 2: Implement this method.
     * Steps: Block > Facility Type > Floor > Room > Date > Time Slot
     */
    private String selectFromMenu(Scanner sc, List<String> options, String title) {
        System.out.println("\nAvailable " + title + ":");

        for (int i = 0; i < options.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + options.get(i));
        }

        while (true) {
            System.out.print("Select " + title + " (1-" + options.size() + "): ");

            try {
                int choice = Integer.parseInt(sc.nextLine().trim());

                if (choice >= 1 && choice <= options.size()) {
                    return options.get(choice - 1);
                }

                System.out.println("Invalid selection. Try again.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private void searchAvailableFacility() {
        System.out.println("\n========== SEARCH AVAILABLE FACILITY ==========");

        List<Facility> facilities = FileManager.loadAllFacilities();
        if (facilities.isEmpty()) {
            System.out.println("No facilities found in the system.");
            return;
        }

        Scanner sc = this.sc;

        // ---------------- Step 1: Block ----------------
        Set<String> blockSet = new HashSet<>();
        for (Facility f : facilities) blockSet.add(f.getBlock());

        String block = selectFromMenu(sc, new ArrayList<>(blockSet), "Block");

        facilities = facilities.stream()
                .filter(f -> f.getBlock().equalsIgnoreCase(block))
                .collect(Collectors.toList());

        if (facilities.isEmpty()) {
            System.out.println("No facilities found for this block.");
            return;
        }

        // ---------------- Step 2: Facility Type ----------------
        Set<String> typeSet = new HashSet<>();
        for (Facility f : facilities) typeSet.add(f.getType());

        String type = selectFromMenu(sc, new ArrayList<>(typeSet), "Facility Type");

        facilities = facilities.stream()
                .filter(f -> f.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());

        if (facilities.isEmpty()) {
            System.out.println("No facilities found for this type.");
            return;
        }

        // ---------------- Step 3: Floor ----------------
        Set<String> floorSet = new HashSet<>();
        for (Facility f : facilities) floorSet.add(f.getFloor());

        String floor = selectFromMenu(sc, new ArrayList<>(floorSet), "Floor");

        facilities = facilities.stream()
                .filter(f -> f.getFloor().equalsIgnoreCase(floor))
                .collect(Collectors.toList());

        if (facilities.isEmpty()) {
            System.out.println("No facilities found for this floor.");
            return;
        }

        // ---------------- Step 4: Room ----------------
        Set<String> roomSet = new HashSet<>();
        for (Facility f : facilities) roomSet.add(f.getRoomNo());

        String room = selectFromMenu(sc, new ArrayList<>(roomSet), "Room");

        facilities = facilities.stream()
                .filter(f -> f.getRoomNo().equalsIgnoreCase(room))
                .collect(Collectors.toList());

        if (facilities.isEmpty()) {
            System.out.println("No facilities found for this room.");
            return;
        }

        // ---------------- Step 5: Date ----------------
        String date;
        while (true) {
            System.out.print("\nEnter Booking Date (YYYY-MM-DD): ");
            date = sc.nextLine().trim();

            try {
                java.time.LocalDate.parse(date); // validate format
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        facilities = bookingManager.getAvailableFacilities(facilities, date);

        if (facilities.isEmpty()) {
            System.out.println("No facilities available on this date.");
            return;
        }

        // ---------------- Step 6: Time Slot ----------------
        String timeSlot;
        while (true) {
            System.out.print("Enter Time Slot (HH:MM-HH:MM): ");
            timeSlot = sc.nextLine().trim();

            String regex = "^([01]\\d|2[0-3]):[0-5]\\d-([01]\\d|2[0-3]):[0-5]\\d$";

            if (!timeSlot.matches(regex)) {
                System.out.println("Invalid format. Example: 09:00-10:00");
                continue;
            }

            String[] parts = timeSlot.split("-");
            if (parts[0].compareTo(parts[1]) >= 0) {
                System.out.println("Start time must be earlier than end time.");
                continue;
            }

            break;
        }

        facilities = bookingManager.getAvailableFacilities(facilities, date, timeSlot);

        if (facilities.isEmpty()) {
            System.out.println("No facilities available at this time slot.");
            return;
        }

        // ---------------- Step 7: Display Results ----------------
        System.out.println("\n--- AVAILABLE FACILITIES ---");

        for (Facility f : facilities) {
            f.display();
            System.out.println("----------------------------");
        }
    }

    // ===================== MEMBER 2: NEW BOOKING =====================
    /**
     * Creates a new booking after facility search.
     * TODO Member 2: Implement this method.
     */
    private String generateBookingID() {
        String date = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

        int seq = bookingManager.getBookingList().size() + 1;

        return "B" + date + String.format("%04d", seq);
    }
    
    private void newBooking() {
        System.out.println("\n--- NEW BOOKING ---");

        List<Facility> facilities = FileManager.loadAllFacilities();

        if (facilities.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        Scanner sc = this.sc;

        // ================= STEP 1: BLOCK =================
        Set<String> blockSet = new HashSet<>();
        for (Facility f : facilities) {
            blockSet.add(f.getBlock());
        }

        String block = selectFromMenu(sc, new ArrayList<>(blockSet), "Block");

        List<Facility> filtered = new ArrayList<>();
        for (Facility f : facilities) {
            if (f.getBlock().equalsIgnoreCase(block)) {
                filtered.add(f);
            }
        }
        facilities = filtered;

        if (facilities.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        // ================= STEP 2: TYPE =================
        Set<String> typeSet = new HashSet<>();
        for (Facility f : facilities) {
            typeSet.add(f.getType());
        }

        String type = selectFromMenu(sc, new ArrayList<>(typeSet), "Facility Type");

        filtered = new ArrayList<>();
        for (Facility f : facilities) {
            if (f.getType().equalsIgnoreCase(type)) {
                filtered.add(f);
            }
        }
        facilities = filtered;

        if (facilities.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        // ================= STEP 3: FLOOR =================
        Set<String> floorSet = new HashSet<>();
        for (Facility f : facilities) {
            floorSet.add(f.getFloor());
        }

        String floor = selectFromMenu(sc, new ArrayList<>(floorSet), "Floor");

        filtered = new ArrayList<>();
        for (Facility f : facilities) {
            if (f.getFloor().equalsIgnoreCase(floor)) {
                filtered.add(f);
            }
        }
        facilities = filtered;

        if (facilities.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        // ================= STEP 4: ROOM =================
        Set<String> roomSet = new HashSet<>();
        for (Facility f : facilities) {
            roomSet.add(f.getRoomNo());
        }

        String room = selectFromMenu(sc, new ArrayList<>(roomSet), "Room");

        filtered = new ArrayList<>();
        for (Facility f : facilities) {
            if (f.getRoomNo().equalsIgnoreCase(room)) {
                filtered.add(f);
            }
        }
        facilities = filtered;

        if (facilities.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        Facility selectedFacility = facilities.get(0);

        // ================= STEP 5: DATE VALIDATION =================
        String date;
        while (true) {
            System.out.print("\nEnter booking date (YYYY-MM-DD): ");
            date = sc.nextLine().trim();

            try {
                java.time.LocalDate.parse(date);
                break;
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        }

        // ================= STEP 6: TIME VALIDATION =================
        String timeSlot;
        while (true) {
            System.out.print("Enter time slot (HH:MM-HH:MM): ");
            timeSlot = sc.nextLine().trim();

            if (timeSlot.length() == 11 && timeSlot.contains("-")) {
                String start = timeSlot.split("-")[0];
                String end = timeSlot.split("-")[1];

                if (start.compareTo(end) < 0) {
                    break;
                } else {
                    System.out.println("Start time must be earlier than end time.");
                }
            } else {
                System.out.println("Invalid format. Example: 09:00-10:00");
            }
        }

        // ================= STEP 7: PURPOSE =================
        System.out.print("Enter purpose of booking: ");
        String purpose = sc.nextLine().trim();

        // ================= STEP 8: PAX =================
        int pax;
        while (true) {
            System.out.print("Enter number of people (pax): ");

            try {
                pax = Integer.parseInt(sc.nextLine().trim());

                if (pax > 0) break;

                System.out.println("Pax must be greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
        int slot = -1;

        for (int i = 0; i < Constants.TIME_SLOTS.length; i++) {
            if (Constants.TIME_SLOTS[i].equals(timeSlot)) {
                slot = i;
                break;
            }
        }

        // ================= CREATE BOOKING =================
        Booking b = new Booking(
                generateBookingID(),
                currentUser.getId(),
                selectedFacility.getFacilityID(),
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")),
                date,
                slot,
                purpose,
                pax,
                "Pending",
                ""
        );
        bookingManager.addBooking(b);

        System.out.println("Booking request submitted successfully!");
    }
    

    // ===================== MEMBER 2: MODIFY/CANCEL BOOKING =====================
    /**
     * Shows pending bookings, allows modify or cancel.
     * TODO Member 2: Implement this method.
     */
    private void modifyBooking() {

        List<Booking> userBookings =
                bookingManager.getBookingsByUser(currentUser.getId());

        if (userBookings.isEmpty()) {
            System.out.println("No pending bookings to modify.");
            return;
        }

        System.out.println("\n--- YOUR PENDING BOOKINGS ---");

        List<Booking> safeList = new ArrayList<>();

        // ================= FILTER VALID BOOKINGS ONLY =================
        for (Booking b : userBookings) {
            if (b.getTimeSlot() >= 0 &&
                b.getTimeSlot() < Constants.TIME_SLOTS.length) {

                safeList.add(b);
            } else {
                System.out.println("[WARNING] Skipping corrupted booking: " + b.getBookingID());
            }
        }

        if (safeList.isEmpty()) {
            System.out.println("No valid bookings available to modify.");
            return;
        }

        // ================= DISPLAY SAFE BOOKINGS =================
        for (int i = 0; i < safeList.size(); i++) {
            System.out.print("[" + (i + 1) + "] ");
            safeList.get(i).display();
            System.out.println("----------------------------");
        }

        // ================= SELECT BOOKING =================
        System.out.print("Select booking number: ");

        int index;
        try {
            index = Integer.parseInt(sc.nextLine().trim()) - 1;
        } catch (Exception e) {
            System.out.println("Invalid input.");
            return;
        }

        if (index < 0 || index >= safeList.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Booking selected = safeList.get(index);

        // ================= ACTION =================
        System.out.println("[1] Modify");
        System.out.println("[2] Cancel");
        System.out.print("Choose action: ");

        String action = sc.nextLine().trim();

        // ================= CANCEL =================
        if ("2".equals(action)) {
            bookingManager.cancelBooking(selected.getBookingID());
            System.out.println("Booking cancelled.");
            return;
        }

        // ================= MODIFY =================
        if ("1".equals(action)) {

            // ---------- NEW DATE ----------
            String newDate;
            while (true) {
                System.out.print("Enter new date (YYYY-MM-DD): ");
                newDate = sc.nextLine().trim();

                try {
                    java.time.LocalDate.parse(newDate);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid date format.");
                }
            }

            // ---------- NEW TIME SLOT ----------
            System.out.print("Enter new time slot (HH:MM-HH:MM): ");
            String newSlot = sc.nextLine().trim();

            int slotIndex = -1;

            for (int i = 0; i < Constants.TIME_SLOTS.length; i++) {
                if (Constants.TIME_SLOTS[i].equals(newSlot)) {
                    slotIndex = i;
                    break;
                }
            }

            if (slotIndex == -1) {
                System.out.println("Invalid time slot.");
                return;
            }

            // ---------- UPDATE ----------
            selected.setBookingDate(newDate);
            selected.setTimeSlot(slotIndex);

            bookingManager.updateBooking(selected);

            System.out.println("Booking modified successfully!");
            return;
        }

        System.out.println("Invalid action selected.");
    }

    // ===================== MEMBER 2: VIEW BOOKING STATUS =====================
    /**
     * Shows all upcoming booking requests and their status.
     * TODO Member 2: Implement this method.
     */
    private void viewBookingRequestStatus() {

        List<Booking> userBookings =
                bookingManager.getBookingsByUser(currentUser.getId());

        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings.");
            return;
        }

        System.out.println("\n--- YOUR BOOKINGS ---");

        for (Booking b : userBookings) {

            // ================= SAFETY CHECK =================
            if (b.getTimeSlot() < 0 ||
                b.getTimeSlot() >= Constants.TIME_SLOTS.length) {

                System.out.println(b.getBookingID()
                        + " | INVALID TIME SLOT | "
                        + b.getBookingDate()
                        + " | " + b.getStatus());
            } else {

                // safe print instead of toString()
                System.out.println(
                        b.getBookingID() + " | "
                        + b.getFacilityID() + " | "
                        + b.getBookingDate() + " | "
                        + Constants.TIME_SLOTS[b.getTimeSlot()] + " | "
                        + b.getPurpose() + " | "
                        + b.getPax() + " | "
                        + b.getStatus()
                );
            }
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
