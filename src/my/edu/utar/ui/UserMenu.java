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
            System.out.print("Enter new name (or [C] to cancel): ");
            String input = sc.nextLine().trim();
            
            if ("C".equalsIgnoreCase(input)) {
                System.out.println("Update name action cancelled.");
                return;
            }
            
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
            System.out.print("Enter new phone number (or [C] to cancel): ");
            String input = sc.nextLine().trim();
            
            if ("C".equalsIgnoreCase(input)) {
                System.out.println("Update phone action cancelled.");
                return;
            }

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
            System.out.println("[C] Cancel");
            System.out.print("Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if ("C".equals(input)) {
                System.out.println("Update faculty action cancelled.");
                return;
            }
            if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
            
            if (!Validator.isValidMenuChoice(input, 1, Constants.FACULTIES.length)) {
                System.out.println("Invalid selection, please try again.");
                continue;
            }
            
            int index = Integer.parseInt(input) - 1;
            String selectedFaculty = Constants.FACULTIES[index];
            currentUser.setFaculty(selectedFaculty);
            
            System.out.println("Faculty updated to: " + selectedFaculty);
            System.out.println("Note: You must now update your Programme/Department to maintain consistency.");
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
            System.out.print("Enter " + type + " abbreviation (or [C] to cancel): ");
            String input = sc.nextLine().trim().toUpperCase();

            if ("C".equals(input)) {
                System.out.println("Update " + type + " action cancelled.");
                return;
            }
            if (Validator.isEmpty(input)) {
                System.out.println("Error: " + type + " cannot be empty.");
                continue;
            }

            Map<String, String> validMap = Constants.FACULTY_PROGRAMME_MAP.get(faculty);
            String fullName = (validMap != null) ? validMap.get(input) : null;

            if (fullName != null) {
                System.out.println("  Verified " + type + ": " + fullName);
                System.out.print("  Confirm update? [Y] Yes / [N] Re-enter / [C] Cancel: ");
            } else {
                System.out.println("  [WARNING] \"" + input + "\" is not registered under " + faculty + ".");
                System.out.print("  Are you sure? [Y] Yes / [N] Re-enter / [C] Cancel: ");
            }

            String confirm = sc.nextLine().trim().toUpperCase();
            if ("C".equals(confirm)) {
                System.out.println("Update " + type + " action cancelled.");
                return;
            }
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
            System.out.print("Enter current password (or [C] to cancel): ");
            String oldPass = sc.nextLine().trim();
            
            if ("C".equalsIgnoreCase(oldPass)) {
                System.out.println("Update password action cancelled.");
                return;
            }

            if (!oldPass.equals(currentUser.getPassword())) {
                System.out.println("Incorrect current password.");
                continue;
            }

            System.out.print("Enter new password (min 8 chars) (or [C] to cancel): ");
            String newPass = sc.nextLine().trim();
            if ("C".equalsIgnoreCase(newPass)) return;

            if (!Validator.isValidPassword(newPass)) {
                System.out.println("Password must be at least 8 characters.");
                continue;
            }

            System.out.print("Confirm new password: ");
            String confirmPass = sc.nextLine().trim();
            if (!Validator.passwordsMatch(newPass, confirmPass)) {
                System.out.println("Passwords do not match. Restarting password update...");
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

        String block = "", type = "", floor = "", room = "", formattedDate = "";
        int selectedSlot = -1;
        int step = 1;
        
        List<Facility> allFacilities = FileManager.loadAllFacilities();
        if (allFacilities.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        boolean useSpecificRoom = false;

        while (step <= 7) {
            switch (step) {
            case 1: // Block Selection
                List<String> blocks = allFacilities.stream()
                        .map(Facility::getBlock)
                        .distinct()
                        .collect(Collectors.toList());

                block = selectWithBack(blocks, "Block");

                if (block == null) return;

                if (block.equals("BACK")) {
                    while (true) { 
                        System.out.print("\nAre you sure you want to cancel this action and back to the previous page? (Y/N): ");
                        String confirm = sc.nextLine().trim().toUpperCase();
                        
                        if (confirm.equals("Y")) {
                            System.out.println("Returning to Main Menu...");
                            return; 
                        } else if (confirm.equals("N")) {
                            System.out.println("Continuing with selection...");
                            break;
                        } else {
                            System.out.println(">> Invalid input. Please enter 'Y' for Yes or 'N' for No.");
                        }
                    }
                    break;
                }

                step++;
                break;

                case 2: // Facility Type
                    final String currentBlock = block;
                    List<String> types = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(currentBlock))
                            .map(Facility::getType).distinct().collect(java.util.stream.Collectors.toList());
                    type = selectWithBack(types, "Facility Type");
                    if (type == null) return;
                    if (type.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 3:
                    final String b3 = block, t3 = type;
                    List<String> floors = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b3) && f.getType().equalsIgnoreCase(t3))
                            .map(Facility::getFloor).distinct().collect(java.util.stream.Collectors.toList());
                    floor = selectWithBack(floors, "Floor");
                    if (floor == null) return;
                    if (floor.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 4: 
                    while (true) {
                        System.out.print("\nEnter Date (YYYY-MM-DD) [B: Back, C: Cancel]: ");
                        String dInput = sc.nextLine().trim().toUpperCase();
                        if (dInput.equals("C")) return;
                        if (dInput.equals("B")) { step--; break; }
                        
                        if (my.edu.utar.util.Validator.isEmpty(dInput)) {
                            System.out.println("Invalid input, please try again.");
                            continue;
                        }

                        try {
                            java.time.LocalDate d = java.time.LocalDate.parse(dInput);
                            if (d.isBefore(java.time.LocalDate.now())) {
                                System.out.println("Error: Past date. Please try again.");
                            } else if (d.isAfter(java.time.LocalDate.now().plusMonths(1))) {
                                showAdminContact(); // Booking too far in advance
                            } else {
                                formattedDate = d.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                                step++; break;
                            }
                        } catch (Exception e) { 
                            System.out.println("Invalid date format, please use YYYY-MM-DD."); 
                        }
                    }
                    break;

                case 5: 
                    while (true) {
                        System.out.println("\n--- Time Slots ---");
                        for (int i = 1; i < Constants.TIME_SLOTS.length; i++) 
                            System.out.println("[" + i + "] " + Constants.TIME_SLOTS[i]);
                        System.out.print("Select (1-5) [B: Back, C: Cancel]: ");
                        String sInput = sc.nextLine().trim().toUpperCase();

                        if (sInput.equals("C")) return;
                        if (sInput.equals("B")) { step--; break; }
                        
                        if (my.edu.utar.util.Validator.isValidMenuChoice(sInput, 1, 5)) {
                            selectedSlot = Integer.parseInt(sInput);
                            step++; break;
                        } else {
                            System.out.println("Invalid selection, please try again.");
                        }
                    }
                    break;

                case 6: // ---------------- Results & Decision ----------------
                    final String fBlock = block, fType = type, fFloor = floor;
                    
                    // 1. Get ALL rooms matching the criteria (regardless of availability)
                    List<Facility> filtered = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(fBlock))
                            .filter(f -> f.getType().equalsIgnoreCase(fType))
                            .filter(f -> f.getFloor().equalsIgnoreCase(fFloor))
                            .collect(Collectors.toList());

                    if (filtered.isEmpty()) {
                        System.out.println("\n>> No facilities found for the selected criteria.");
                        System.out.println("Press Enter to return to the main menu...");
                        sc.nextLine();
                        return;
                    }

                    // 2. Determine availability status for each room to show in the result table
                    // We use the bookingManager to check which ones are free
                    List<Facility> availableList = bookingManager.getAvailableFacilities(filtered, formattedDate, selectedSlot);
                    java.time.LocalDate displayDate = java.time.LocalDate.parse(formattedDate, 
                            java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                    String clearDate = displayDate.format(Constants.DATE_DISPLAY_FORMAT);
                        
                    System.out.println("\n========== SEARCH FACILITY RESULTS ==========");
                    System.out.println("Block: " + block + " | Type: " + type + " | Floor: " + floor);
                    System.out.println("Date : " + clearDate + " | Slot: " + Constants.TIME_SLOTS[selectedSlot]);
                    
                    // Custom display showing ALL filtered rooms with their current status for that slot
                    System.out.printf("%-4s | %-10s | %-35s | %-10s\n", "No.", "Room", "Name", "Status");
                    System.out.println("----------------------------------------------------------------------");
                    for (int i = 0; i < filtered.size(); i++) {
                        Facility f = filtered.get(i);
                        boolean isAvailable = availableList.contains(f);
                        String currentStatus = isAvailable ? "AVAILABLE" : "OCCUPIED";
                        
                        System.out.printf("%-4d | %-10s | %-35s | %-10s\n", 
                            (i + 1), f.getRoomNo(), f.getName(), currentStatus);
                    }
                    System.out.println("----------------------------------------------------------------------");

                    // 3. Ask to continue
                    while (true) {
                        System.out.print("\nDo you want to continue to create a booking? (Y/N): ");
                        String proceed = sc.nextLine().trim().toUpperCase();

                        if (proceed.equals("N")) {
                            System.out.println("\nReturning to previous menu...");
                            System.out.println("Press Enter to back to main menu.");
                            sc.nextLine();
                            return; // Back to main menu
                        } 
                        
                        if (proceed.equals("Y")) {
                            // 4. Selection Process
                            while (true) {
                                System.out.print("Select the respective result number to book: ");
                                String choice = sc.nextLine().trim();

                                if (Validator.isValidMenuChoice(choice, 1, filtered.size())) {
                                    int index = Integer.parseInt(choice) - 1;
                                    Facility selected = filtered.get(index);

                                    // Check if the specific chosen room is available
                                    if (availableList.contains(selected)) {
                                        createNewBooking(selected.getFacilityID(), formattedDate, selectedSlot);
                                        return; // Done
                                    } else {
                                        System.out.println(">> This facility is OCCUPIED. Please try again and select an AVAILABLE room.");
                                    }
                                } else {
                                    System.out.println("Invalid input, please enter a number between 1 and " + filtered.size());
                                }
                            }
                        } else {
                            System.out.println("Invalid input, please enter 'Y' or 'N'.");
                        }
                    }
            }
        }
    }

    private void createNewBooking(String facilityID, String dateStr, int slot) {
        System.out.println("\n--- Booking Details ---");
        
        Facility selectedFacility = FileManager.loadAllFacilities().stream()
                .filter(f -> f.getFacilityID().equalsIgnoreCase(facilityID))
                .findFirst()
                .orElse(null);

        if (selectedFacility == null) {
            System.out.println("Error: Facility information not found.");
            return;
        }

        String purpose;
        while (true) {

            System.out.print("Enter purpose of booking (Max 50 chars) [B: Back, C: Cancel]: ");
            purpose = sc.nextLine().trim();
            if (purpose.equalsIgnoreCase("C")) return;
            if (purpose.equalsIgnoreCase("B")) { break; }
            if (my.edu.utar.util.Validator.isEmpty(purpose)) {
                System.out.println("Invalid input: Purpose cannot be empty.");
            } else if (purpose.length() > 50) {
                System.out.println("Invalid input: Purpose is too long (Max 50 characters).");
            } else {
                break;
            }
        }
        int pax = 1;
        int capacity = selectedFacility.getCapacity();
        
        while (true) {
            System.out.print("Enter number of people (Pax) [B: Back, C: Cancel]: ");
            String paxInput = sc.nextLine().trim().toUpperCase();
            if (paxInput.equals("C")) return;
            if (paxInput.equals("B")) { break; } 
            
            if (my.edu.utar.util.Validator.isEmpty(paxInput) || !my.edu.utar.util.Validator.isNumeric(paxInput)) {
                System.out.println("Invalid input: Please enter a valid number.");
                continue;
            }
            
            pax = Integer.parseInt(paxInput);
            if (pax <= 0) {
                System.out.println("Invalid input: Pax must be at least 1.");
                continue;
            }

            if (pax > capacity) {
                System.out.println("\n[WARNING] Over Capacity!");
                System.out.println("Room " + selectedFacility.getRoomNo() + " capacity is only " + capacity + ".");
                System.out.println("You entered " + pax + " people.");
                System.out.print("Are you sure you want to proceed? [Y] Yes / [N] Re-enter Pax: ");
                
                String confirm = sc.nextLine().trim().toUpperCase();
                if (!"Y".equals(confirm)) {
                    continue; 
                }
            }
            break; 
        }

        String bookingID = generateBookingID();
        String applyDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));

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
            ""
        );

        if (bookingManager.addBooking(newBooking)) {
            System.out.println("\n============================================");
            System.out.println("   SUCCESS: Booking Request Submitted!      ");
            System.out.println("============================================");
            System.out.println("Booking ID   : " + bookingID);
            System.out.println("Facility     : " + selectedFacility.getRoomNo() + " (" + selectedFacility.getType() + ")");
            System.out.println("Status       : Pending Admin Approval");
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

        int step = 1;
        String block = "", type = "", floor = "", room = "", bookingDateStr = "", purpose = "";
        java.time.LocalDate selectedDate = null;
        int selectedSlot = -1;
        int pax = 0;
        Facility selectedFacility = null;

        while (step <= 8) {
            switch (step) {
            case 1: 
                List<String> blocks = allFacilities.stream()
                        .map(Facility::getBlock)
                        .distinct()
                        .collect(Collectors.toList());

                block = selectWithBack(blocks, "Block");

                if (block == null) return;

                if (block.equals("BACK")) {
                    while (true) { 
                        System.out.print("\nAre you sure you want to cancel this action and back to the previous page? (Y/N): ");
                        String confirm = sc.nextLine().trim().toUpperCase();
                        
                        if (confirm.equals("Y")) {
                            System.out.println("Returning to Main Menu...");
                            return;
                        } else if (confirm.equals("N")) {
                            System.out.println("Continuing with selection...");
                            break; 
                        } else {
                            System.out.println(">> Invalid input. Please enter 'Y' for Yes or 'N' for No.");

                        }
                    }
                    break; 
                }
                step++;
                break;

                case 2: 
                    final String b2 = block;
                    List<String> types = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b2))
                            .map(Facility::getType).distinct().collect(Collectors.toList());
                    type = selectWithBack(types, "Facility Type");
                    if (type == null) return;
                    if (type.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 3: // Floor Selection
                    final String b3 = block, t3 = type;
                    List<String> floors = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b3) && f.getType().equalsIgnoreCase(t3))
                            .map(Facility::getFloor).distinct().collect(Collectors.toList());
                    floor = selectWithBack(floors, "Floor");
                    if (floor == null) return;
                    if (floor.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 4: // Room Selection
                    final String b4 = block, t4 = type, f4 = floor;
                    List<Facility> filteredRooms = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b4) && f.getType().equalsIgnoreCase(t4) && f.getFloor().equalsIgnoreCase(f4))
                            .collect(Collectors.toList());
                    
                    List<String> roomNumbers = filteredRooms.stream().map(Facility::getRoomNo).collect(Collectors.toList());
                    room = selectWithBack(roomNumbers, "Room");
                    if (room == null) return;
                    if (room.equals("BACK")) { step--; break; }
                    
                    final String selectedRoomNo = room;
                    selectedFacility = filteredRooms.stream()
                            .filter(f -> f.getRoomNo().equalsIgnoreCase(selectedRoomNo))
                            .findFirst().orElse(null);
                    step++;
                    break;

                case 5: // Date Selection & Full-Day Occupancy Validation
                    while (true) {
                        System.out.print("\nEnter Booking Date (YYYY-MM-DD) [B: Back, C: Cancel]: ");
                        String dateIn = sc.nextLine().trim().toUpperCase();
                        if (dateIn.equals("C")) return;
                        if (dateIn.equals("B")) { step--; break; }
                        
                        try {
                            java.time.LocalDate d = java.time.LocalDate.parse(dateIn);
                            if (d.isBefore(java.time.LocalDate.now())) {
                                System.out.println(">> Error: Cannot book a date in the past.");
                                continue;
                            }

                            String tempDateStr = d.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                            
                            // Check if ANY slots are available for this specific room
                            boolean hasAnySlot = false;
                            for (int i = 1; i <= 5; i++) {
                                if (bookingManager.isSlotAvailable(selectedFacility.getFacilityID(), tempDateStr, i)) {
                                    hasAnySlot = true;
                                    break;
                                }
                            }

                            if (!hasAnySlot) {
                                System.out.println("\n[!] SORRY: No available time slots for " + selectedFacility.getRoomNo() + " on " + d.format(Constants.DATE_DISPLAY_FORMAT));
                                System.out.println("Please select a different date or press [B] to choose another room.");
                                continue;
                            }

                            selectedDate = d;
                            bookingDateStr = tempDateStr;
                            step++; break;
                        } catch (Exception e) {
                            System.out.println("Invalid format. Please use YYYY-MM-DD.");
                        }
                    }
                    break;

                case 6: // Time Slot Selection (Filtered)
                    while (true) {
                        System.out.println("\n--- Available Time Slots for " + selectedDate.format(Constants.DATE_DISPLAY_FORMAT) + " ---");
                        List<Integer> freeSlots = new ArrayList<>();
                        
                        for (int i = 1; i < Constants.TIME_SLOTS.length; i++) {
                            if (bookingManager.isSlotAvailable(selectedFacility.getFacilityID(), bookingDateStr, i)) {
                                System.out.println("[" + i + "] " + Constants.TIME_SLOTS[i]);
                                freeSlots.add(i);
                            } else {
                                System.out.println("[X] " + Constants.TIME_SLOTS[i] + " (Occupied)");
                            }
                        }

                        System.out.print("Select Slot (1-5) [B: Back, C: Cancel]: ");
                        String slotIn = sc.nextLine().trim().toUpperCase();
                        if (slotIn.equals("C")) return;
                        if (slotIn.equals("B")) { step--; break; }

                        if (Validator.isNumeric(slotIn)) {
                            int choice = Integer.parseInt(slotIn);
                            if (freeSlots.contains(choice)) {
                                selectedSlot = choice;
                                step++; break;
                            } else {
                                System.out.println(">> Error: This slot is occupied. Please pick one without an [X].");
                            }
                        } else {
                            System.out.println("Invalid input.");
                        }
                    }
                    break;

                case 7: // Purpose & Pax
                    // Sub-step: Purpose
                    while (true) {
                        System.out.print("\nEnter purpose of booking (Max 50 chars) [B: Back, C: Cancel]: ");
                        purpose = sc.nextLine().trim();
                        if (purpose.equalsIgnoreCase("C")) return;
                        if (purpose.equalsIgnoreCase("B")) { step--; break; }
                        if (!Validator.isEmpty(purpose) && purpose.length() <= 50) break;
                        System.out.println("Invalid purpose (Cannot be empty or > 50 chars).");
                    }
                    if (step < 7) break; 

                    // Sub-step: Pax
                    while (true) {
                        System.out.print("Enter Pax (Capacity: " + selectedFacility.getCapacity() + ") [B: Back, C: Cancel]: ");
                        String paxIn = sc.nextLine().trim().toUpperCase();
                        if (paxIn.equals("C")) return;
                        if (paxIn.equals("B")) break; // Goes back to Purpose
                        
                        if (Validator.isNumeric(paxIn)) {
                            pax = Integer.parseInt(paxIn);
                            if (pax <= 0) {
                                System.out.println("Pax must be at least 1.");
                                continue;
                            }
                            if (pax > selectedFacility.getCapacity()) {
                                System.out.println("\n[WARNING] Over Capacity! (Limit: " + selectedFacility.getCapacity() + ")");
                                System.out.print("Proceed anyway? [Y/N]: ");
                                if (!sc.nextLine().trim().equalsIgnoreCase("Y")) continue;
                            }
                            step++; break;
                        } else {
                            System.out.println("Invalid input.");
                        }
                    }
                    break;

                case 8: // Final Submission
                    String bookingID = generateBookingID();
                    String appDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));

                    Booking b = new Booking(
                        bookingID, currentUser.getId(), selectedFacility.getFacilityID(),
                        appDate, bookingDateStr, selectedSlot, purpose, pax, Constants.STATUS_PENDING, ""
                    );

                    if (bookingManager.addBooking(b)) {
                        System.out.println("\n============================================");
                        System.out.println("   SUCCESS: Booking " + bookingID + " Submitted!");
                        System.out.println("============================================");
                        System.out.println("Press [Enter] to return to the main menu...");
                        sc.nextLine(); 
                        return; 
                    } else {
                        System.out.println(">> Error: System could not save the booking.");
                        return;
                    }
            }
        }
    }
    private void modifyBooking() {
        while (true) { // OUTER LOOP: Returns here if user cancels an action or presses 'B'
            // 1. Refresh data from the manager
            List<Booking> allBookings = bookingManager.getBookingList();
            List<Booking> userBookings = allBookings.stream()
                    .filter(b -> b.getUserID().equals(currentUser.getId()))
                    .collect(Collectors.toList());

            List<Booking> pendingBookings = userBookings.stream()
                    .filter(b -> b.getStatus().equalsIgnoreCase("Pending"))
                    .collect(Collectors.toList());

            long confirmedCount = userBookings.stream()
                    .filter(b -> b.getStatus().equalsIgnoreCase("Approved")).count();

            // 2. Summary Header
            System.out.println("\n========== MODIFY / CANCEL BOOKING ==========");
            System.out.println("Status: Only PENDING bookings can be modified or cancelled.");
            System.out.println("Confirmed Bookings Count: [" + confirmedCount + "]");

            if (pendingBookings.isEmpty()) {
                System.out.println("\nNo pending bookings found to manage.");
                System.out.println("Press Enter to return to Main Menu...");
                sc.nextLine();
                return;
            }

         // 3. Display Selection Table
            System.out.println("\n--- PENDING BOOKING LIST ---");
            System.out.printf("%-4s | %-10s | %-20s | %-15s\n", "No.", "Room No", "Booking Date", "Time Slot");
            System.out.println("------------------------------------------------------------------");

            for (int i = 0; i < pendingBookings.size(); i++) {
                Booking b = pendingBookings.get(i);
                
                // LOOKUP FACILITY HERE to get Room No instead of Facility ID
                Facility f = FileManager.getFacilityById(b.getFacilityID());
                String roomDisplay = (f != null) ? f.getRoomNo() : b.getFacilityID();

                // Format ddMMyyyy -> 21 April 2026
                String dateDisplay = java.time.LocalDate.parse(b.getBookingDate(), 
                        java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                        .format(Constants.DATE_DISPLAY_FORMAT);
                
                // Use roomDisplay instead of b.getFacilityID()
                System.out.printf("%-4d | %-10s | %-20s | %-15s\n", 
                        (i + 1), roomDisplay, dateDisplay, Constants.TIME_SLOTS[b.getTimeSlot()]);
            }

            // 4. Selection Input
            System.out.print("\nSelect number to manage [B: Back to Main Menu]: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("B")) return;
            if (!Validator.isValidMenuChoice(input, 1, pendingBookings.size())) {
                System.out.println(">> Invalid selection. Please try again.");
                continue; // Re-run the table display
            }

            Booking selected = pendingBookings.get(Integer.parseInt(input) - 1);
            Facility f = FileManager.getFacilityById(selected.getFacilityID());

            // 5. Display Full Details of Selection
            String fullDate = java.time.LocalDate.parse(selected.getBookingDate(), 
                    java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                    .format(Constants.DATE_DISPLAY_FORMAT);

            System.out.println("\n--- SELECTED BOOKING DETAILS ---");
            System.out.println("Booking ID      : " + selected.getBookingID());
            System.out.println("Selected Date   : " + fullDate);
            System.out.println("Booking Time    : " + Constants.TIME_SLOTS[selected.getTimeSlot()]);
            System.out.println("Room No         : " + (f != null ? f.getRoomNo() : selected.getFacilityID()));
            System.out.println("Facility Name   : " + (f != null ? f.getName() : "N/A"));
            System.out.println("Facility Type   : " + (f != null ? f.getType() : "N/A"));
            System.out.println("Booking Pax     : " + selected.getPax());
            System.out.println("Purpose         : " + selected.getPurpose());
            System.out.println("---------------------------------");

            // 6. Action Choice Validation Loop
            String action = "";
            while (true) {
                System.out.println("\nWhat would you like to do?");
                System.out.println("[1] Update Booking");
                System.out.println("[2] Cancel Booking");
                System.out.println("[B] Back to List");
                System.out.print("Choice: ");
                action = sc.nextLine().trim().toUpperCase();

                if (action.equals("1") || action.equals("2") || action.equals("B")) break;
                System.out.println(">> Invalid choice. Please enter [1], [2], or [B].");
            }

            if (action.equals("B")) continue; // Goes back to table list

            // ================= CANCEL BOOKING =================
            if (action.equals("2")) {
                while (true) {
                    System.out.print("\nAre you sure you want to cancel this booking? (Y/N): ");
                    String confirm = sc.nextLine().trim().toUpperCase();
                    
                    if (confirm.equals("Y")) {
                        selected.setStatus(Constants.STATUS_CANCELLED);
                        bookingManager.updateBooking(selected);
                        System.out.println("\nSUCCESS: Booking successfully cancelled.");
                        System.out.println("Press Enter to return to Main Menu...");
                        sc.nextLine();
                        return; // EXIT TO MAIN MENU
                    } else if (confirm.equals("N")) {
                        System.out.println("\nAction cancelled. Press Enter to back to Pending Booking list...");
                        sc.nextLine();
                        break; // EXIT confirmation loop to re-run 'continue' below
                    } else {
                        System.out.println(">> Invalid input. Please type 'Y' or 'N'.");
                    }
                }
                continue; // Back to table list
            }

            // ================= UPDATE BOOKING =================
            if (action.equals("1")) {
                boolean success = false;
                System.out.println("\n--- UPDATE MENU ---");
                System.out.println("Note: Only Time Slot, Pax, and Purpose can be modified.");
                System.out.println("[1] Modify Time Slot");
                System.out.println("[2] Modify Pax");
                System.out.println("[3] Modify Purpose");
                System.out.println("[B] Back to List");
                System.out.print("Selection: ");
                String updateChoice = sc.nextLine().trim().toUpperCase();

                if (updateChoice.equals("B")) continue;

                switch (updateChoice) {
                    case "1": // Time Slot
                        System.out.println("\nAvailable slots for " + fullDate + ":");
                        List<Integer> freeSlots = new ArrayList<>();
                        for (int i = 1; i <= 5; i++) {
                            if (bookingManager.isSlotAvailable(selected.getFacilityID(), selected.getBookingDate(), i)) {
                                System.out.println("[" + i + "] " + Constants.TIME_SLOTS[i]);
                                freeSlots.add(i);
                            }
                        }
                        if (freeSlots.isEmpty()) {
                            System.out.println("No other slots available for this date.");
                        } else {
                            System.out.print("Select new slot No: ");
                            String sIn = sc.nextLine().trim();
                            if (Validator.isNumeric(sIn) && freeSlots.contains(Integer.parseInt(sIn))) {
                                selected.setTimeSlot(Integer.parseInt(sIn));
                                success = true;
                            } else {
                                System.out.println("Invalid selection or slot occupied.");
                            }
                        }
                        break;

                    case "2": // Pax
                        System.out.print("\nEnter new Pax (Capacity: " + (f != null ? f.getCapacity() : "N/A") + "): ");
                        String pIn = sc.nextLine().trim();
                        if (Validator.isNumeric(pIn)) {
                            int p = Integer.parseInt(pIn);
                            if (f != null && p > f.getCapacity()) {
                                System.out.print("Warning: Over capacity. Proceed? (Y/N): ");
                                if (!sc.nextLine().trim().equalsIgnoreCase("Y")) break;
                            }
                            selected.setPax(p);
                            success = true;
                        } else {
                            System.out.println("Invalid input.");
                        }
                        break;

                    case "3": // Purpose
                        System.out.print("\nEnter new purpose (Max 50 chars): ");
                        String purp = sc.nextLine().trim();
                        if (!purp.isEmpty() && purp.length() <= 50) {
                            selected.setPurpose(purp);
                            success = true;
                        } else {
                            System.out.println("Invalid length.");
                        }
                        break;
                }

                if (success) {
                    bookingManager.updateBooking(selected);
                    System.out.println("\nSUCCESS: Booking updated successfully!");
                    System.out.println("Press Enter to return to Main Menu...");
                    sc.nextLine();
                    return; // EXIT TO MAIN MENU
                } else {
                    System.out.println("\nNo changes were made. Press Enter to back to list...");
                    sc.nextLine();
                    continue; // Back to Table
                }
            }
        }
    }

    // ===================== MEMBER 2: VIEW BOOKING STATUS =====================
 // ===================== MEMBER 2: VIEW BOOKING STATUS =====================
    private void viewBookingRequestStatus() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            // Fetch all bookings for the user
            List<Booking> allUserBookings = bookingManager.getBookingsByUser(currentUser.getId());
            
            // 1. Filter bookings to exclude "Cancelled"
            List<Booking> filteredBookings = new java.util.ArrayList<>();
            for (Booking b : allUserBookings) {
                String status = b.getStatus().toLowerCase();
                // Only add if NOT cancelled
                if (!status.equals("cancelled")) {
                    filteredBookings.add(b);
                }
            }

            if (filteredBookings.isEmpty()) {
                System.out.println("\n>> You have no active booking requests (Pending/Confirmed/Rejected).");
                return;
            }

            // 2. Display Table Header
            System.out.println("\n--- Booking Request Status ---");
            System.out.printf("%-4s | %-10s | %-20s | %-20s | %-10s\n", 
                              "No.", "Room No", "Booking Date", "Time Slot", "Status");
            System.out.println("---------------------------------------------------------------------------------");

            for (int i = 0; i < filteredBookings.size(); i++) {
                Booking b = filteredBookings.get(i);
                
                Facility f = FileManager.getFacilityById(b.getFacilityID());
                String roomNo = (f != null) ? f.getRoomNo() : b.getFacilityID();

                String dateDisplay = java.time.LocalDate.parse(b.getBookingDate(), 
                        java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));

                System.out.printf("%-4d | %-10s | %-20s | %-20s | %-10s\n", 
                        (i + 1), 
                        roomNo, 
                        dateDisplay, 
                        Constants.TIME_SLOTS[b.getTimeSlot()], 
                        b.getStatus());
            }

            // 3. User Selection
            System.out.print("\nSelect number to view the detail [B: Back to Main Menu]: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("B")) break;

            try {
                int choice = Integer.parseInt(input);
                // Use filteredBookings.size() to ensure valid selection
                if (choice >= 1 && choice <= filteredBookings.size()) {
                    Booking selected = filteredBookings.get(choice - 1);
                    Facility f = FileManager.getFacilityById(selected.getFacilityID());
                    
                    displayFullDetails(selected, f);

                    System.out.println("\nPress Enter to return to the list...");
                    sc.nextLine();
                } else {
                    System.out.println(">> Invalid selection. Please try again.");
                }
            } catch (Exception e) {
                System.out.println(">> Invalid input. Please enter a number or 'B'.");
            }
        }
    }

    private void displayFullDetails(Booking b, Facility f) {
        String dateDisplay = java.time.LocalDate.parse(b.getBookingDate(), 
                java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        System.out.println("\n--- SELECTED BOOKING DETAILS ---");
        System.out.println("Booking ID      : " + b.getBookingID());
        System.out.println("Selected Date   : " + dateDisplay);
        System.out.println("Booking Time    : " + Constants.TIME_SLOTS[b.getTimeSlot()]);
        
        // Facility info from facility.txt
        System.out.println("Room No         : " + (f != null ? f.getRoomNo() : "N/A"));
        System.out.println("Facility Name   : " + (f != null ? f.getName() : "N/A"));
        System.out.println("Facility Type   : " + (f != null ? f.getType() : "N/A"));
        
        System.out.println("Booking Pax     : " + b.getPax());
        System.out.println("Purpose         : " + b.getPurpose());
        System.out.println("Status          : " + b.getStatus());
        
        // Use your specific method name: getRejectReason()
        if (b.getStatus().equalsIgnoreCase("Rejected")) {
            String reason = b.getRejectReason(); 
            System.out.println("Reason          : " + (reason != null && !reason.isEmpty() ? reason : "No reason provided."));
        }
        System.out.println("---------------------------------");
    }
 // ===================== MEMBER 3: REPORT ISSUE =====================
    private void reportIssue() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n========== REPORT FACILITY ISSUE ==========");
        System.out.println("(Type 'B' to go back, 'C' to cancel)");

        String block = "", type = "", floor = "";
        Facility selectedFacility = null;
        int step = 1;

        List<Facility> allFacilities = FileManager.loadAllFacilities();
        if (allFacilities.isEmpty()) {
            System.out.println("No facilities found in system.");
            return;
        }

        while (step <= 5) {
            switch (step) {
                case 1: // 1. Select Block
                    List<String> blocks = allFacilities.stream()
                            .map(Facility::getBlock).distinct().collect(Collectors.toList());
                    block = selectWithBack(blocks, "Block");
                    if (block == null) return; // 'C' pressed
                    if (block.equals("BACK")) return; // Already at start
                    step++;
                    break;

                case 2: // 2. Select Facility Type
                    final String b2 = block;
                    List<String> types = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b2))
                            .map(Facility::getType).distinct().collect(Collectors.toList());
                    type = selectWithBack(types, "Facility Type");
                    if (type == null) return;
                    if (type.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 3: // 3. Select Floor
                    final String b3 = block, t3 = type;
                    List<String> floors = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b3) && f.getType().equalsIgnoreCase(t3))
                            .map(Facility::getFloor).distinct().collect(Collectors.toList());
                    floor = selectWithBack(floors, "Floor");
                    if (floor == null) return;
                    if (floor.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 4: // 4. Select Specific Room
                    final String b4 = block, t4 = type, fl4 = floor;
                    List<Facility> filtered = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b4) && 
                                         f.getType().equalsIgnoreCase(t4) && 
                                         f.getFloor().equalsIgnoreCase(fl4))
                            .collect(Collectors.toList());

                    System.out.println("\n--- Select Facility ---");
                    for (int i = 0; i < filtered.size(); i++) {
                        System.out.printf("[%d] %s - %s\n", (i + 1), filtered.get(i).getRoomNo(), filtered.get(i).getName());
                    }
                    System.out.print("Choice [B: Back, C: Cancel]: ");
                    String fInput = sc.nextLine().trim().toUpperCase();
                    if (fInput.equals("C")) return;
                    if (fInput.equals("B")) { step--; break; }

                    try {
                        int idx = Integer.parseInt(fInput) - 1;
                        if (idx >= 0 && idx < filtered.size()) {
                            selectedFacility = filtered.get(idx);
                            step++;
                        } else { System.out.println("Invalid selection."); }
                    } catch (Exception e) { System.out.println("Please enter a number."); }
                    break;

                case 5: // 5. Issue Details & Confirmation
                    System.out.println("\n--- Select Issue Type ---");
                    for (int i = 0; i < Constants.ISSUE_TYPES.length; i++) {
                        System.out.printf("[%d] %s\n", (i + 1), Constants.ISSUE_TYPES[i]);
                    }
                    System.out.print("Select (1-" + Constants.ISSUE_TYPES.length + ") [B: Back]: ");
                    String issueChoice = sc.nextLine().trim();
                    if (issueChoice.equalsIgnoreCase("B")) { step--; break; }

                    try {
                        int issueIdx = Integer.parseInt(issueChoice) - 1;
                        if (issueIdx < 0 || issueIdx >= Constants.ISSUE_TYPES.length) {
                            System.out.println(">> Invalid choice.");
                            break; // Goes back to Select Issue Type
                        }
                        String selectedIssueType = Constants.ISSUE_TYPES[issueIdx];

                        System.out.print("Enter short description: ");
                        String desc = sc.nextLine().trim();
                        if (desc.isEmpty()) {
                            System.out.println(">> Description cannot be empty.");
                            break; // Goes back to Select Issue Type
                        }

                        // Prepare Data
                        String reportDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                        String reportTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                        String issueID = "M" + reportDate + String.format("%04d", System.currentTimeMillis() % 10000);

                        // --- INNER LOOP FOR CONFIRMATION ---
                        boolean confirmed = false;
                        while (true) {
                            System.out.println("\n==============================================");
                            System.out.println("         PRE-REPORT SUMMARY");
                            System.out.println("==============================================");
                            System.out.printf("%-20s : %s\n", "Issue ID", issueID);
                            System.out.printf("%-20s : %s\n", "Reported Date", java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")));
                            System.out.printf("%-20s : %s\n", "Reported Time", reportTime);
                            System.out.printf("%-20s : %s\n", "Reported Block", selectedFacility.getBlock());
                            System.out.printf("%-20s : %s\n", "Reported Room No", selectedFacility.getRoomNo());
                            System.out.printf("%-20s : %s\n", "Facility Name", selectedFacility.getName());
                            System.out.printf("%-20s : %s\n", "Facility Type", selectedFacility.getType());
                            System.out.printf("%-20s : %s\n", "Issue Type", selectedIssueType);
                            System.out.printf("%-20s : %s\n", "Description", desc);
                            System.out.println("----------------------------------------------");

                            System.out.print("Confirm report? [Y: Confirm, B: Back to Edit, C: Cancel]: ");
                            String confirm = sc.nextLine().trim().toUpperCase();

                            if (confirm.equals("Y")) {
                                String maintenanceData = String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s",
                                        issueID, selectedFacility.getFacilityID(), currentUser.getId(),
                                        selectedIssueType, desc, reportDate, Constants.MAINT_REPORTED, "None", "None");
                                
                                saveMaintenanceRecord(maintenanceData);
                                System.out.println("\n>> Issue reported successfully!");
                                System.out.println(">> Admin will resolve the problem as soon as possible.");
                                System.out.println("\nPress Enter to return to main page...");
                                sc.nextLine();
                                return; 
                            } 
                            else if (confirm.equals("B")) {
                                confirmed = false;
                                break;
                            } 
                            else if (confirm.equals("C")) {
                                System.out.println("\n>> Issue report action cancelled.");
                                System.out.println("Press Enter to return to main menu...");
                                sc.nextLine();
                                return;
                            } 
                            else {
                                System.out.println(">> Invalid input. Please enter 'Y', 'B', or 'C'.");
                            }
                        }

                    } catch (Exception e) {
                        System.out.println(">> Invalid input. Please enter numbers only.");
                    }
                    break;
            }
        }
    }

    private void saveMaintenanceRecord(String data) {
        try (java.io.FileWriter fw = new java.io.FileWriter("maintenance.txt", true);
             java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
            pw.println(data);
        } catch (java.io.IOException e) {
            System.out.println("Error saving maintenance record: " + e.getMessage());
        }
    }

    // ===================== MEMBER 4: VIEW BOOKING HISTORY =====================
    private void viewBookingHistory() {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            // 1. Get user-specific bookings
            ArrayList<Booking> allBookings = bookingManager.getBookingList();
            ArrayList<Booking> userBookings = new ArrayList<>();
            for (Booking b : allBookings) {
                if (b.getUserID().equals(currentUser.getId())) {
                    userBookings.add(b);
                }
            }

            if (userBookings.isEmpty()) {
                System.out.println("\n>> No booking history found.");
                break;
            }

            // 2. Display Table
            System.out.println("\n--- YOUR BOOKING HISTORY ---");
            // Updated Header to match your request
            System.out.printf("%-4s | %-10s | %-20s | %-20s | %-10s\n", 
                              "No.", "Room No", "Booking Date", "Time Slot", "Status");
            System.out.println("---------------------------------------------------------------------------------");

            for (int i = 0; i < userBookings.size(); i++) {
                Booking b = userBookings.get(i);
                
                // Get Room No from Facility Manager
                Facility f = FileManager.getFacilityById(b.getFacilityID());
                String roomNo = (f != null) ? f.getRoomNo() : b.getFacilityID();

                // Format Date
                String dateDisplay = java.time.LocalDate.parse(b.getBookingDate(), 
                        java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));

                // Convert Time Slot index to String (e.g., 1 -> "10 a.m. - 12 p.m.")
                String timeDisplay = Constants.TIME_SLOTS[b.getTimeSlot()];

                System.out.printf("%-4d | %-10s | %-20s | %-20s | %-10s\n", 
                        (i + 1), roomNo, dateDisplay, timeDisplay, b.getStatus());
            }

            // 3. Generate Summary Report
            ReportGenerator rg = new ReportGenerator();
            rg.generateUserReport(currentUser.getId(), allBookings);

            // 4. Interaction Logic
            System.out.print("\nSelect number to view details [B: Back to Main Menu]: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("B")) break;

            try {
                int index = Integer.parseInt(input) - 1;
                if (index >= 0 && index < userBookings.size()) {
                    showBookingDetail(userBookings.get(index));
                    
                    System.out.println("\nPress Enter to return to the list...");
                    sc.nextLine();
                } else {
                    System.out.println(">> Invalid selection.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Please enter a valid number or 'B'.");
            }
        }
    }

    // Helper method with detailed Facility information
    private void showBookingDetail(Booking selected) {
        // Fetch facility object to get detailed info
        Facility f = FileManager.getFacilityById(selected.getFacilityID());
        
        String fullDate = java.time.LocalDate.parse(selected.getBookingDate(), 
                java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                .format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));

        String timeDisplay = Constants.TIME_SLOTS[selected.getTimeSlot()];

        System.out.println("\n--- SELECTED BOOKING DETAILS ---");
        System.out.printf("Booking ID      : %s\n", selected.getBookingID());
        System.out.printf("Selected Date   : %s\n", fullDate);
        System.out.printf("Booking Time    : %s\n", timeDisplay);
        
        if (f != null) {
            System.out.printf("Room No         : %s\n", f.getRoomNo());
            System.out.printf("Facility Name   : %s\n", f.getName());
            System.out.printf("Facility Type   : %s\n", f.getType());
        } else {
            System.out.printf("Room No         : %s (Data not found)\n", selected.getFacilityID());
        }
        
        System.out.printf("Booking Pax     : %d\n", selected.getPax());
        System.out.printf("Purpose         : %s\n", selected.getPurpose());
        System.out.printf("Status          : %s\n", selected.getStatus());
        System.out.println("---------------------------------");
    }
    
    private String selectWithBack(List<String> options, String label) {
        while (true) {
            System.out.println("\n--- Select " + label + " ---");
            for (int i = 0; i < options.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + options.get(i));
            }
            System.out.println("[B] Back | [C] Cancel");
            System.out.print("Choice: ");
            
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("C")) return null;
            if (input.equals("B")) return "BACK"; 

            if (Validator.isValidMenuChoice(input, 1, options.size())) {
                int index = Integer.parseInt(input) - 1;
                return options.get(index);
            }

            System.out.println("Invalid input, please try again.");
        }
    }

    private void displayAvailable(List<Facility> list) {
        System.out.println("\n--- AVAILABLE FACILITIES ---");
        
        if (list.isEmpty()) {
            System.out.println("No facilities available for the selected criteria.");
            return;
        }
        System.out.printf("%-4s | %-10s | %-35s | %-15s | %-5s | %-10s\n", 
                          "No.", "Room", "Name", "Type", "Cap", "Status");
        System.out.println("----------------------------------------------------------------------------------------------------");

        for (int i = 0; i < list.size(); i++) {
            Facility f = list.get(i);
            System.out.printf("%-4d | %-10s | %-35s | %-15s | %-5d | %-10s\n",
                (i + 1),
                f.getRoomNo(),
                (f.getName() != null ? f.getName() : "N/A"),
                f.getType(),
                f.getCapacity(),
                f.getStatus()
            );
        }
        System.out.println("----------------------------------------------------------------------------------------------------");
    }

    private void showAdminContact() {
        System.out.println("\n[!] Online booking limit is 1 month.");
        System.out.println("Contact " + Constants.ADMIN_NAME + " (" + Constants.ADMIN_PHONE + ") for advanced bookings.");
    }
}
