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
import java.util.Map;

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
            String selectedFaculty = Constants.FACULTIES[index];
            currentUser.setFaculty(selectedFaculty);
            
            // Automatically trigger Programme/Dept update to ensure consistency with the new Faculty
            System.out.println("Faculty updated to: " + selectedFaculty);
            System.out.println("You must now update your Programme/Department to match the new Faculty.");
            updateProgrammeDept(); 
            
            FileManager.updateUser(currentUser);
            break;
        }
    }

    private void updateProgrammeDept() {
        String role = currentUser.getRole();
        String type = Constants.ROLE_STUDENT.equals(role) ? "Programme" : "Department";
        String faculty = currentUser.getFaculty();

        while (true) {
            System.out.println("\nUpdate " + type + " for " + faculty);
            System.out.print("Enter " + type + " abbreviation (or [B] to cancel): ");
            
            String input = sc.nextLine().trim().toUpperCase();

            if ("B".equals(input)) return;
            if (Validator.isEmpty(input)) {
                System.out.println("Error: " + type + " cannot be empty.");
                continue;
            }

            // Verification Logic (Matching your register behavior)
            Map<String, String> validMap = Constants.FACULTY_PROGRAMME_MAP.get(faculty);
            String fullName = (validMap != null) ? validMap.get(input) : null;

            if (fullName != null) {
                System.out.println("  Verified " + type + ": " + fullName);
                System.out.print("  Confirm update? [Y] Yes / [N] Re-enter: ");
            } else {
                System.out.println("  [WARNING] \"" + input + "\" is not registered under " + faculty + ".");
                System.out.print("  Are you sure you want to use this code? [Y] Yes / [N] Re-enter: ");
            }

            String confirm = sc.nextLine().trim().toUpperCase();
            if ("Y".equals(confirm)) {
                currentUser.setProgramme(input);
                FileManager.updateUser(currentUser);
                System.out.println(type + " updated successfully to: " + input);
                break;
            }
        }
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

    private String selectFromMenu(Scanner sc, List<String> options, String title) {
        java.util.Collections.sort(options);
        System.out.println("\n--- Available " + title + " ---");
        for (int i = 0; i < options.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + options.get(i));
        }
        System.out.println("[C] Cancel and Return to Menu");

        while (true) {
            System.out.print("Select " + title + " (1-" + options.size() + ") or 'C': ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("C")) {
                return null; // Return null to signal cancellation
            }

            if (my.edu.utar.util.Validator.isValidMenuChoice(input, 1, options.size())) {
                return options.get(Integer.parseInt(input) - 1);
            }
            System.out.println(">> Invalid selection. Enter 1-" + options.size() + " or 'C'.");
        }
    }
    private void searchAvailableFacility() {
        System.out.println("\n========== SEARCH AVAILABLE FACILITY ==========");
        System.out.println("(Type 'B' to go back, 'C' to cancel completely)");

        List<Facility> allFacilities = FileManager.loadAllFacilities();
        if (allFacilities.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        int step = 1;
        String block = "", type = "", floor = "", room = "", formattedDate = "";
        int selectedSlot = -1;
        boolean useSpecificRoom = false;

        while (step <= 7) {
            switch (step) {
                case 1: // ---------------- Step 1: Block ----------------
                    List<String> blocks = allFacilities.stream().map(Facility::getBlock).distinct().collect(java.util.stream.Collectors.toList());
                    block = selectWithBack(blocks, "Block");
                    if (block == null) return; // Cancelled
                    step++;
                    break;

                case 2: // ---------------- Step 2: Facility Type ----------------
                    final String currentBlock = block;
                    List<String> types = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(currentBlock))
                            .map(Facility::getType).distinct().collect(java.util.stream.Collectors.toList());
                    type = selectWithBack(types, "Facility Type");
                    if (type == null) return; // Cancelled
                    if (type.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 3: // ---------------- Step 3: Floor ----------------
                    final String b3 = block, t3 = type;
                    List<String> floors = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b3) && f.getType().equalsIgnoreCase(t3))
                            .map(Facility::getFloor).distinct().collect(java.util.stream.Collectors.toList());
                    floor = selectWithBack(floors, "Floor");
                    if (floor == null) return;
                    if (floor.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 4: // ---------------- Step 4: Room Choice ----------------
                    System.out.println("\n--- Room Selection ---");
                    System.out.println("[1] Select a specific room");
                    System.out.println("[2] Search all rooms on this floor");
                    System.out.println("[B] Back | [C] Cancel");
                    System.out.print("Choice: ");
                    String rChoice = sc.nextLine().trim().toUpperCase();
                    if (rChoice.equals("C")) return;
                    if (rChoice.equals("B")) { step--; break; }
                    
                    if (rChoice.equals("1")) {
                        final String b4 = block, t4 = type, f4 = floor;
                        List<String> rooms = allFacilities.stream()
                                .filter(f -> f.getBlock().equalsIgnoreCase(b4) && f.getType().equalsIgnoreCase(t4) && f.getFloor().equalsIgnoreCase(f4))
                                .map(Facility::getRoomNo).distinct().collect(java.util.stream.Collectors.toList());
                        room = selectWithBack(rooms, "Room");
                        if (room == null) return;
                        if (room.equals("BACK")) break; // stay on step 4 to re-choose 1 or 2
                        useSpecificRoom = true;
                    } else {
                        useSpecificRoom = false;
                    }
                    step++;
                    break;

                case 5: // ---------------- Step 5: Date ----------------
                    System.out.print("\nEnter Date (YYYY-MM-DD) [B: Back, C: Cancel]: ");
                    String dInput = sc.nextLine().trim().toUpperCase();
                    if (dInput.equals("C")) return;
                    if (dInput.equals("B")) { step--; break; }
                    try {
                        java.time.LocalDate d = java.time.LocalDate.parse(dInput);
                        if (d.isBefore(java.time.LocalDate.now())) {
                            System.out.println("Error: Past date.");
                        } else if (d.isAfter(java.time.LocalDate.now().plusMonths(1))) {
                            showAdminContact(); // Helper for Mr Lee's info
                        } else {
                            formattedDate = d.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                            step++;
                        }
                    } catch (Exception e) { System.out.println("Invalid format."); }
                    break;

                case 6: // ---------------- Step 6: Slot ----------------
                    System.out.println("\n--- Time Slots ---");
                    for (int i = 1; i < Constants.TIME_SLOTS.length; i++) System.out.println("[" + i + "] " + Constants.TIME_SLOTS[i]);
                    System.out.print("Select (1-5) [B: Back, C: Cancel]: ");
                    String sInput = sc.nextLine().trim().toUpperCase();
                    if (sInput.equals("C")) return;
                    if (sInput.equals("B")) { step--; break; }
                    if (my.edu.utar.util.Validator.isValidMenuChoice(sInput, 1, 5)) {
                        selectedSlot = Integer.parseInt(sInput);
                        step++;
                    }
                    break;

                case 7: // ---------------- Step 7: Results & Book ----------------
                    final String fb = block, ft = type, ff = floor, fr = room, fd = formattedDate;
                    final boolean useR = useSpecificRoom;
                    List<Facility> filtered = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(fb) && f.getType().equalsIgnoreCase(ft) && f.getFloor().equalsIgnoreCase(ff))
                            .filter(f -> !useR || f.getRoomNo().equalsIgnoreCase(fr))
                            .collect(java.util.stream.Collectors.toList());

                    List<Facility> available = bookingManager.getAvailableFacilities(filtered, fd, selectedSlot);
                    if (available.isEmpty()) {
                        System.out.println("\nNo availability. [B] to go back and change criteria.");
                        if (sc.nextLine().trim().equalsIgnoreCase("B")) { step--; break; }
                        return;
                    }
                    
                    displayAvailable(available); // Helper to show table
                    System.out.print("\nEnter ID to book [B: Back, C: Cancel]: ");
                    String targetID = sc.nextLine().trim().toUpperCase();
                    if (targetID.equals("C")) return;
                    if (targetID.equals("B")) { step--; break; }
                    
                    if (available.stream().anyMatch(f -> f.getFacilityID().equalsIgnoreCase(targetID))) {
                        createNewBooking(targetID, fd, selectedSlot);
                        return;
                    }
                    System.out.println("Invalid ID.");
                    break;
            }
        }
    }

    private void createNewBooking(String facilityID, String dateStr, int slot) {
        System.out.println("\n--- Booking Details ---");
        
        // 1. Get Purpose
        System.out.print("Enter purpose of booking (e.g., Study, Meeting): ");
        String purpose = sc.nextLine().trim();
        if (purpose.isEmpty()) purpose = "General Use";

        // 2. Get Pax
        int pax = 1;
        while (true) {
            System.out.print("Enter number of people (Pax): ");
            String paxInput = sc.nextLine().trim();
            if (my.edu.utar.util.Validator.isNumeric(paxInput)) {
                pax = Integer.parseInt(paxInput);
                break;
            }
            System.out.println("Invalid number. Please try again.");
        }

        // 3. Generate internal data
        // Format: B + Today's Date + Sequence
        String todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String bookingID = "B" + todayStr + String.format("%04d", bookingManager.getBookingList().size() + 1);
        
        // applyDate is today in DDMMYYYY format
        String applyDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));

        // 4. Create the Booking Object with all 10 parameters
        // Order: ID, UserID, FacilityID, ApplyDate, BookingDate, Slot, Purpose, Pax, Status, RejectReason
        Booking newBooking = new Booking(
            bookingID, 
            currentUser.getId(), 
            facilityID, 
            applyDate, 
            dateStr, 
            slot, 
            purpose, 
            pax, 
            "Pending", 
            "" // Reject reason is empty for new bookings
        );

        // 5. Save
        if (bookingManager.addBooking(newBooking)) {
            System.out.println("\n============================================");
            System.out.println("   SUCCESS: Booking Request Submitted!      ");
            System.out.println("============================================");
            System.out.println("Booking ID : " + bookingID);
            System.out.println("Status     : Pending Admin Approval");
            System.out.println("============================================");
        } else {
            System.out.println(">> Error: Failed to save booking.");
        }
    }
    private String generateBookingID() {
        String date = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);

        int seq = bookingManager.getBookingList().size() + 1;

        return "B" + date + String.format("%04d", seq);
    }
    
    private void newBooking() {
        System.out.println("\n========== NEW BOOKING ==========");
        System.out.println("(Type 'B' to go back, 'C' to cancel completely)");

        List<Facility> allFacilities = FileManager.loadAllFacilities();
        if (allFacilities.isEmpty()) {
            System.out.println("No facilities found in the system.");
            return;
        }

        // Variables to hold selections
        int step = 1;
        String block = "", type = "", floor = "", room = "", bookingDateStr = "", purpose = "";
        java.time.LocalDate selectedDate = null;
        int selectedSlot = -1;
        int pax = 0;
        Facility selectedFacility = null;

        while (step <= 8) {
            switch (step) {
                case 1: // ---------------- Step 1: Block ----------------
                    List<String> blocks = allFacilities.stream().map(Facility::getBlock).distinct().collect(Collectors.toList());
                    block = selectWithBack(blocks, "Block");
                    if (block == null) return; // Cancel
                    step++;
                    break;

                case 2: // ---------------- Step 2: Facility Type ----------------
                    final String b2 = block;
                    List<String> types = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b2))
                            .map(Facility::getType).distinct().collect(Collectors.toList());
                    type = selectWithBack(types, "Facility Type");
                    if (type == null) return;
                    if (type.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 3: // ---------------- Step 3: Floor ----------------
                    final String b3 = block, t3 = type;
                    List<String> floors = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b3) && f.getType().equalsIgnoreCase(t3))
                            .map(Facility::getFloor).distinct().collect(Collectors.toList());
                    floor = selectWithBack(floors, "Floor");
                    if (floor == null) return;
                    if (floor.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 4: // ---------------- Step 4: Room ----------------
                    final String b4 = block, t4 = type, f4 = floor;
                    List<Facility> filteredRooms = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b4) && f.getType().equalsIgnoreCase(t4) && f.getFloor().equalsIgnoreCase(f4))
                            .collect(Collectors.toList());
                    
                    List<String> roomNumbers = filteredRooms.stream().map(Facility::getRoomNo).collect(Collectors.toList());
                    room = selectWithBack(roomNumbers, "Room");
                    if (room == null) return;
                    if (room.equals("BACK")) { step--; break; }
                    
                    // Identify the specific facility object for capacity checks later
                    final String selectedRoomNo = room;
                    selectedFacility = filteredRooms.stream()
                            .filter(f -> f.getRoomNo().equalsIgnoreCase(selectedRoomNo))
                            .findFirst().orElse(null);
                    step++;
                    break;

                case 5: // ---------------- Step 5: Date ----------------
                    System.out.print("\nEnter Booking Date (YYYY-MM-DD) [B: Back, C: Cancel]: ");
                    String dateIn = sc.nextLine().trim().toUpperCase();
                    if (dateIn.equals("C")) return;
                    if (dateIn.equals("B")) { step--; break; }
                    
                    try {
                        java.time.LocalDate d = java.time.LocalDate.parse(dateIn);
                        java.time.LocalDate today = java.time.LocalDate.now();
                        if (d.isBefore(today)) {
                            System.out.println(">> Error: Cannot book a date in the past.");
                        } else if (d.isAfter(today.plusMonths(1))) {
                            showAdminContact(); // Defined in previous response
                        } else {
                            selectedDate = d;
                            bookingDateStr = d.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                            step++;
                        }
                    } catch (Exception e) {
                        System.out.println(">> Invalid format. Use YYYY-MM-DD.");
                    }
                    break;

                case 6: // ---------------- Step 6: Time Slot ----------------
                    System.out.println("\n--- Time Slots ---");
                    for (int i = 1; i < Constants.TIME_SLOTS.length; i++) 
                        System.out.println("[" + i + "] " + Constants.TIME_SLOTS[i]);
                    System.out.print("Select (1-5) [B: Back, C: Cancel]: ");
                    String slotIn = sc.nextLine().trim().toUpperCase();
                    if (slotIn.equals("C")) return;
                    if (slotIn.equals("B")) { step--; break; }
                    
                    if (Validator.isValidMenuChoice(slotIn, 1, 5)) {
                        selectedSlot = Integer.parseInt(slotIn);
                        step++;
                    } else {
                        System.out.println(">> Invalid selection.");
                    }
                    break;

                case 7: // ---------------- Step 7: Purpose & Pax ----------------
                    System.out.print("\nEnter purpose [B: Back, C: Cancel]: ");
                    purpose = sc.nextLine().trim();
                    if (purpose.equalsIgnoreCase("C")) return;
                    if (purpose.equalsIgnoreCase("B")) { step--; break; }
                    
                    System.out.print("Enter Pax (Capacity: " + selectedFacility.getCapacity() + ") [B: Back, C: Cancel]: ");
                    String paxIn = sc.nextLine().trim().toUpperCase();
                    if (paxIn.equals("C")) return;
                    if (paxIn.equals("B")) { /* No step-- here because we stay in Case 7 to re-enter purpose/pax */ break; }
                    
                    if (Validator.isNumeric(paxIn)) {
                        pax = Integer.parseInt(paxIn);
                        if (pax > 0 && pax <= selectedFacility.getCapacity()) {
                            step++;
                        } else {
                            System.out.println(">> Error: Pax must be 1-" + selectedFacility.getCapacity());
                        }
                    } else {
                        System.out.println(">> Invalid number.");
                    }
                    break;

                case 8: // ---------------- Step 8: Final Submission ----------------
                    String bookingID = generateBookingID();
                    String appDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));

                    Booking b = new Booking(
                        bookingID, currentUser.getId(), selectedFacility.getFacilityID(),
                        appDate, bookingDateStr, selectedSlot, purpose, pax, Constants.STATUS_PENDING, ""
                    );

                    bookingManager.addBooking(b);
                    System.out.println("\nSUCCESS: Booking " + bookingID + " submitted!");
                    step++; // Exit loop
                    break;
            }
        }
    }
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
   
        List<Booking> bList = bookingManager.getBookingList();
        
        List<Facility> fList = my.edu.utar.data.FileManager.loadAllFacilities();

        Booking[] bArray = bList.toArray(new Booking[0]);
        Facility[] fArray = fList.toArray(new Facility[0]);

        rg.generateUserReport(currentUser.getId(), bArray, fArray);
    }
    
 // 1. New Helper for Selection with "Back" capability
    private String selectWithBack(List<String> options, String title) {
        java.util.Collections.sort(options);
        System.out.println("\n--- " + title + " ---");
        for (int i = 0; i < options.size(); i++) System.out.println("[" + (i + 1) + "] " + options.get(i));
        System.out.println("[B] Back | [C] Cancel");

        while (true) {
            System.out.print("Choice: ");
            String in = sc.nextLine().trim().toUpperCase();
            if (in.equals("C")) return null;
            if (in.equals("B")) return "BACK";
            if (my.edu.utar.util.Validator.isValidMenuChoice(in, 1, options.size())) {
                return options.get(Integer.parseInt(in) - 1);
            }
        }
    }

    // 2. Display Table Helper
    private void displayAvailable(List<Facility> list) {
        System.out.println("\n--- AVAILABLE FACILITIES ---");
        System.out.printf("%-6s | %-10s | %-5s | %-10s\n", "ID", "Room", "Cap", "Status");
        for (Facility f : list) {
            System.out.printf("%-6s | %-10s | %-5d | %-10s\n", f.getFacilityID(), f.getRoomNo(), f.getCapacity(), f.getStatus());
        }
    }

    // 3. Admin Info Helper
    private void showAdminContact() {
        System.out.println("\n[!] Online booking limit is 1 month.");
        System.out.println("Contact " + Constants.ADMIN_NAME + " (" + Constants.ADMIN_PHONE + ") for advanced bookings.");
    }
}
