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
import my.edu.utar.model.MaintenanceReport; 
import java.util.stream.Collectors;

import java.util.List;
import java.util.ArrayList;

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
                case "6":
                    System.out.println("\n--- ISSUE REPORTING ---");
                    System.out.println("[1] Report New Issue");
                    System.out.println("[2] View My Reported Issues");
                    System.out.println("[B] Back");
                    System.out.print("Choice: ");
                    String issueChoice = sc.nextLine().trim().toUpperCase();
                    
                    if (issueChoice.equals("1")) {
                        reportIssue(); 
                    } else if (issueChoice.equals("2")) {
                        viewMyReportedIssues(); 
                    }
                    break;
                case "7": viewBookingHistory();         break;
                case "L":
                    System.out.println("Logged out successfully. Goodbye, " + currentUser.getName() + "!\n\n");
                    return;
                default:
                    System.out.println("Invalid selection, please try again.");
            }
        }
    }

    public void printUserMenu() {
        System.out.println("\n============================================================");
        System.out.println("            UTAR Smart Campus Management System");
        System.out.println("============================================================");
        
        System.out.printf("  Date : %-15s | Time : %s\n", 
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy")),
            java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));
        System.out.println("  Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        System.out.println("------------------------------------------------------------");
        
        showReminders(); 
        
        System.out.println("------------------------------------------------------------");
        
        System.out.println("  [1] Update Profile            [2] Search Facility");
        System.out.println("  [3] New Booking               [4] Modify / Cancel");
        System.out.println("  [5] Request Status            [6] Report Issue");
        System.out.println("  [7] History & Summary         [L] Logout");
        System.out.println("------------------------------------------------------------");
        System.out.print("  Enter your choice: ");
    }

    private void showReminders() {
        NotificationService ns = new NotificationService();
        ns.showReminders(currentUser.getId(), bookingManager.getBookingList(), bookingManager);
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
    
    private void searchAvailableFacility() {
        System.out.println("\n========== SEARCH AVAILABLE FACILITY ==========");
        System.out.println("(Type 'B' to go back, 'C' to cancel completely)");

        String block = "", type = "", floor = "", formattedDate = "";
        int selectedSlot = -1;
        int step = 1;
        List<Facility> filtered = new ArrayList<>();
        List<Facility> availableList = new ArrayList<>();
        
        List<Facility> allFacilities = FileManager.loadAllFacilities();
        if (allFacilities.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        while (step <= 6) {
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
                                showAdminContact(); 
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

                case 6:
                    final String fBlock = block, fType = type, fFloor = floor;
                    
                    filtered = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(fBlock) && 
                                         f.getType().equalsIgnoreCase(fType) && 
                                         f.getFloor().equalsIgnoreCase(fFloor))
                            .collect(Collectors.toList());

                    if (filtered.isEmpty()) {
                        System.out.println("\n>> No facilities found for the selected criteria.");
                        System.out.println("Press Enter to return to menu...");
                        sc.nextLine();
                        return;
                    }

                    availableList = bookingManager.getAvailableFacilities(filtered, formattedDate, selectedSlot);
                    
                    java.time.LocalDate dDate = java.time.LocalDate.parse(formattedDate, java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                    
                    System.out.println("\n========== SEARCH FACILITY RESULTS ==========");
                    System.out.println("Date : " + dDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")));
                    System.out.println("Slot : " + Constants.TIME_SLOTS[selectedSlot]);
                    System.out.printf("%-4s | %-10s | %-30s | %-15s\n", "No.", "Room", "Name", "Status");
                    System.out.println("-".repeat(70));

                    for (int i = 0; i < filtered.size(); i++) {
                        Facility f = filtered.get(i);
                        
                        boolean isUnderRepair = FileManager.loadAllMaintenanceReports().stream()
                                .anyMatch(r -> r.getFacilityID().equalsIgnoreCase(f.getFacilityID()) && 
                                               r.getStatus().equalsIgnoreCase(Constants.MAINT_IN_PROGRESS));

                        String currentStatus;
                        if (isUnderRepair) {
                            currentStatus = "MAINTENANCE";
                        } else if (availableList.contains(f)) {
                            currentStatus = "AVAILABLE";
                        } else {
                            currentStatus = "OCCUPIED";
                        }

                        System.out.printf("%-4d | %-10s | %-30s | %-15s\n", 
                            (i + 1), f.getRoomNo(), f.getName(), currentStatus);
                    }
                    System.out.println("-".repeat(70));

                    while (true) {
                        System.out.print("\nDo you want to continue to create a booking? (Y/N): ");
                        String proceed = sc.nextLine().trim().toUpperCase();

                        if (proceed.equals("N")) return;
                        
                        if (proceed.equals("Y")) {
                            while (true) {
                                System.out.print("Select No. to book [B: Back]: ");
                                String choice = sc.nextLine().trim();
                                if (choice.equalsIgnoreCase("B")) break;

                                if (Validator.isValidMenuChoice(choice, 1, filtered.size())) {
                                    Facility selected = filtered.get(Integer.parseInt(choice) - 1);
                                    
                                    boolean isMaint = FileManager.loadAllMaintenanceReports().stream()
                                        .anyMatch(r -> r.getFacilityID().equalsIgnoreCase(selected.getFacilityID()) && 
                                                       r.getStatus().equalsIgnoreCase(Constants.MAINT_IN_PROGRESS));

                                    if (isMaint) {
                                        System.out.println(">> This room is under MAINTENANCE and cannot be booked.");
                                    } else if (availableList.contains(selected)) {
                                        createNewBooking(selected.getFacilityID(), formattedDate, selectedSlot);
                                        return; 
                                    } else {
                                        System.out.println(">> This facility is OCCUPIED. Please select an AVAILABLE room.");
                                    }
                                } else {
                                    System.out.println(">> Invalid input.");
                                }
                            }
                            break;
                        }
                        else {
                        	System.out.println(">> Invalid input.");
                        }
                    }
                    break;
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
            if (purpose.equalsIgnoreCase("C")) {
                System.out.println("\n>> Booking process cancelled. Press Enter to continue...");
                sc.nextLine();
                return; 
            }
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
            if (paxInput.equals("C")) {
                System.out.println("\n>> Booking process cancelled. Press Enter to continue...");
                sc.nextLine(); 
                return;
            }
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

                case 3:
                    final String b3 = block, t3 = type;
                    List<String> floors = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b3) && f.getType().equalsIgnoreCase(t3))
                            .map(Facility::getFloor).distinct().collect(Collectors.toList());
                    floor = selectWithBack(floors, "Floor");
                    if (floor == null) return;
                    if (floor.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 4: 
                    final String b4 = block, t4 = type, f4 = floor;
                    List<Facility> filteredRooms = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b4) && 
                                         f.getType().equalsIgnoreCase(t4) && 
                                         f.getFloor().equalsIgnoreCase(f4))
                            .collect(Collectors.toList());
                    
                    List<String> roomNumbers = filteredRooms.stream().map(Facility::getRoomNo).collect(Collectors.toList());
                    room = selectWithBack(roomNumbers, "Room");
                    if (room == null) return;
                    if (room.equals("BACK")) { step--; break; }
                    
                    final String targetRoom = room;
                    Facility found = filteredRooms.stream()
                            .filter(f -> f.getRoomNo().equalsIgnoreCase(targetRoom))
                            .findFirst().orElse(null);
                    
                    selectedFacility = found;
                    step++;
                    break;

                case 5:
                    while (true) {
                        System.out.print("\nEnter Booking Date (YYYY-MM-DD) [B: Back, C: Cancel]: ");
                        String dateIn = sc.nextLine().trim().toUpperCase();
                        if (dateIn.equals("C")) return;
                        if (dateIn.equals("B")) { step--; break; }
                        
                        try {
                            java.time.LocalDate d = java.time.LocalDate.parse(dateIn);
                            java.time.LocalDate today = java.time.LocalDate.now();

                            if (d.isBefore(today)) {
                                System.out.println(">> Error: Cannot book a date in the past.");
                                continue;
                            }

                            if (d.isAfter(today.plusMonths(1))) {
                                System.out.println("\n[!] Online booking limit is 1 month.");
                                System.out.println("Contact Mr Lee (012345678) for advanced bookings.");
                                continue;
                            }

                            String tempDateStr = d.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                            final Facility currentRoom = selectedFacility; 

                            boolean isUnderMaintenance = FileManager.loadAllMaintenanceReports().stream()
                                .anyMatch(r -> r.getFacilityID().equalsIgnoreCase(currentRoom.getFacilityID()) && 
                                               r.getStatus().equalsIgnoreCase(Constants.MAINT_IN_PROGRESS));

                            if (isUnderMaintenance) {
                                System.out.println("\n[!] SORRY: " + currentRoom.getRoomNo() + " is currently under MAINTENANCE.");
                                System.out.println("Please select another room or try again later.");
                                System.out.print("Press [B] to pick another room or any key to pick another date: ");
                                if(sc.nextLine().trim().equalsIgnoreCase("B")) {
                                    step = 4; break; 
                                }
                                continue;
                            }

                            boolean hasAnySlot = false;
                            for (int i = 1; i <= 5; i++) {
                                if (bookingManager.isTimeSlotAvailable(currentRoom.getFacilityID(), tempDateStr, i)) {
                                    hasAnySlot = true;
                                    break;
                                }
                            }

                            if (!hasAnySlot) {
                                System.out.println("\n[!] SORRY: No available time slots for " + currentRoom.getRoomNo() + " on " + d.format(Constants.DATE_DISPLAY_FORMAT));
                                System.out.println("The room is fully booked for this day.");
                                continue;
                            }

                            selectedDate = d;
                            bookingDateStr = tempDateStr;
                            step++; 
                            break; 
                        } catch (Exception e) {
                            System.out.println("Invalid format. Please use YYYY-MM-DD.");
                        }
                    }
                    break;

                case 6:
                    while (true) {
                        final Facility snapshot = selectedFacility;
                        
                        System.out.println("\n--- Slots for " + snapshot.getRoomNo() + " ---");
                        List<Integer> freeSlots = new ArrayList<>();
                        
                        for (int i = 1; i < Constants.TIME_SLOTS.length; i++) {
                            if (bookingManager.isTimeSlotAvailable(snapshot.getFacilityID(), bookingDateStr, i)) {
                                System.out.println("[" + i + "] " + Constants.TIME_SLOTS[i]);
                                freeSlots.add(i);
                            } else {
                                boolean isMaint = FileManager.loadAllMaintenanceReports().stream()
                                    .anyMatch(r -> r.getFacilityID().equalsIgnoreCase(snapshot.getFacilityID()) && 
                                                   r.getStatus().equalsIgnoreCase(Constants.MAINT_IN_PROGRESS));
                                
                                String reason = isMaint ? "(Maintenance)" : "(Occupied)";
                                System.out.println("[X] " + Constants.TIME_SLOTS[i] + " " + reason);
                            }
                        }
                        
                        System.out.print("Select Slot (1-5) [B: Back, C: Cancel]: ");
                        String sIn = sc.nextLine().trim().toUpperCase();
                        if (sIn.equals("C")) return;
                        if (sIn.equals("B")) { step--; break; }

                        if (my.edu.utar.util.Validator.isNumeric(sIn)) {
                            int choice = Integer.parseInt(sIn);
                            if (freeSlots.contains(choice)) {
                                selectedSlot = choice;
                                step++; break;
                            } else {
                                System.out.println(">> Slot unavailable.");
                            }
                        }
                    }
                    break;

                case 7: 
                    while (true) {
                        System.out.print("\nEnter purpose of booking (Max 50 chars) [B: Back, C: Cancel]: ");
                        purpose = sc.nextLine().trim();
                        if (purpose.equalsIgnoreCase("C")) return;
                        if (purpose.equalsIgnoreCase("B")) { step--; break; }
                        if (!Validator.isEmpty(purpose) && purpose.length() <= 50) break;
                        System.out.println("Invalid purpose (Cannot be empty or > 50 chars).");
                    }
                    if (step < 7) break; 

                    while (true) {
                        System.out.print("Enter Pax (Capacity: " + selectedFacility.getCapacity() + ") [B: Back, C: Cancel]: ");
                        String paxIn = sc.nextLine().trim().toUpperCase();
                        if (paxIn.equals("C")) return;
                        if (paxIn.equals("B")) break; 
                        
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

                case 8: 
                    String bookingID = generateBookingID();
                    String appDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));

                    Booking b = new Booking(
                        bookingID, 
                        currentUser.getId(), 
                        selectedFacility.getFacilityID(),
                        appDate, 
                        bookingDateStr, 
                        selectedSlot, 
                        purpose, 
                        pax, 
                        Constants.STATUS_PENDING, 
                        ""
                    );

                    if (bookingManager.addBooking(b)) {
                        System.out.println("\n============================================");
                        System.out.println("   SUCCESS: Booking Request Submitted!      ");
                        System.out.println("============================================");
                        System.out.println("Booking ID   : " + bookingID);
                        System.out.println("Facility     : " + selectedFacility.getRoomNo() + " (" + selectedFacility.getType() + ")");
                        System.out.println("Status       : Pending Admin Approval");
                        System.out.println("============================================");
                        
                        System.out.println("\nPress [Enter] to return to the main menu...");
                        sc.nextLine(); 
                        return; 
                    } else {
                        System.out.println(">> Error: Failed to save booking. Please try again.");
                        return;
                    }
            }
        }
    }
    
    private void modifyBooking() {
        while (true) { 
            List<Booking> allBookings = bookingManager.getBookingList();
            List<Booking> userBookings = allBookings.stream()
                    .filter(b -> b.getUserID().equals(currentUser.getId()))
                    .collect(Collectors.toList());

            List<Booking> pendingBookings = userBookings.stream()
                    .filter(b -> b.getStatus().equalsIgnoreCase("Pending"))
                    .collect(Collectors.toList());

            long confirmedCount = userBookings.stream()
                    .filter(b -> b.getStatus().equalsIgnoreCase("Approved")).count();

            System.out.println("\n========== MODIFY / CANCEL BOOKING ==========");
            System.out.println("Status: Only PENDING bookings can be modified or cancelled.");
            System.out.println("Confirmed Bookings Count: [" + confirmedCount + "]");

            if (pendingBookings.isEmpty()) {
                System.out.println("\nNo pending bookings found to manage.");
                System.out.println("Press Enter to return to Main Menu...");
                sc.nextLine();
                return;
            }

            System.out.println("\n--- PENDING BOOKING LIST ---");
            System.out.printf("%-4s | %-10s | %-20s | %-15s\n", "No.", "Room No", "Booking Date", "Time Slot");
            System.out.println("------------------------------------------------------------------");

            for (int i = 0; i < pendingBookings.size(); i++) {
                Booking b = pendingBookings.get(i);
                
                Facility f = FileManager.getFacilityById(b.getFacilityID());
                String roomDisplay = (f != null) ? f.getRoomNo() : b.getFacilityID();

                String dateDisplay = java.time.LocalDate.parse(b.getBookingDate(), 
                        java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                        .format(Constants.DATE_DISPLAY_FORMAT);
                
                System.out.printf("%-4d | %-10s | %-20s | %-15s\n", 
                        (i + 1), roomDisplay, dateDisplay, Constants.TIME_SLOTS[b.getTimeSlot()]);
            }

            System.out.print("\nSelect number to manage [B: Back to Main Menu]: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("B")) return;
            if (!Validator.isValidMenuChoice(input, 1, pendingBookings.size())) {
                System.out.println(">> Invalid selection. Please try again.");
                continue;
            }

            Booking selected = pendingBookings.get(Integer.parseInt(input) - 1);
            Facility f = FileManager.getFacilityById(selected.getFacilityID());

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

            if (action.equals("B")) continue;

            if (action.equals("2")) {
                while (true) {
                    System.out.print("\nAre you sure you want to cancel this booking? (Y/N): ");
                    String confirm = sc.nextLine().trim().toUpperCase();
                    
                    if (confirm.equals("Y")) {
                        selected.setStatus(Constants.STATUS_CANCELLED);
                        bookingManager.updateBooking(selected);
                        System.out.println("\nSUCCESS: Booking successfully cancelled. This time slot is now available for others.");
                        System.out.println("Press Enter to return to Main Menu...");
                        sc.nextLine();
                        return;
                    } else if (confirm.equals("N")) {
                        System.out.println("\nAction cancelled. Press Enter to back to Pending Booking list...");
                        sc.nextLine();
                        break; 
                    } else {
                        System.out.println(">> Invalid input. Please type 'Y' or 'N'.");
                    }
                }
                continue;
            }

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
                    case "1": 
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

                    case "2":
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

                    case "3": 
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
                    return; 
                } else {
                    System.out.println("\nNo changes were made. Press Enter to back to list...");
                    sc.nextLine();
                    continue; 
                }
            }
        }
    }

    private void viewBookingRequestStatus() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            List<Booking> allUserBookings = bookingManager.getBookingsByUser(currentUser.getId());
            
            List<Booking> filteredBookings = new java.util.ArrayList<>();
            for (Booking b : allUserBookings) {
                String status = b.getStatus().toLowerCase();
                if (!status.equals("cancelled")) {
                    filteredBookings.add(b);
                }
            }

            if (filteredBookings.isEmpty()) {
                System.out.println("\n>> You have no active booking requests (Pending/Confirmed/Rejected).");
                System.out.println("\nPress Enter to continue...");
                sc.nextLine();
                return;
            }

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

            System.out.print("\nSelect number to view the detail [B: Back to Main Menu]: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("B")) break;

            try {
                int choice = Integer.parseInt(input);
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
        
        System.out.println("Room No         : " + (f != null ? f.getRoomNo() : "N/A"));
        System.out.println("Facility Name   : " + (f != null ? f.getName() : "N/A"));
        System.out.println("Facility Type   : " + (f != null ? f.getType() : "N/A"));
        
        System.out.println("Booking Pax     : " + b.getPax());
        System.out.println("Purpose         : " + b.getPurpose());
        System.out.println("Status          : " + b.getStatus());
        
        if (b.getStatus().equalsIgnoreCase("Rejected")) {
            String reason = b.getRejectReason(); 
            System.out.println("Reason          : " + (reason != null && !reason.isEmpty() ? reason : "No reason provided."));
        }
        System.out.println("---------------------------------");
    }

    private void viewBookingHistory() {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            ArrayList<Booking> allBookings = bookingManager.getBookingList();
            ArrayList<Booking> userBookings = new ArrayList<>();
            for (Booking b : allBookings) {
                if (b.getUserID().equals(currentUser.getId())) {
                    userBookings.add(b);
                }
            }
            
            ReportGenerator rg = new ReportGenerator();
            rg.generateUserReport(currentUser.getId(), allBookings);


            if (userBookings.isEmpty()) {
                System.out.println("\n>> No booking history found.");
                System.out.println("\nPress Enter to continue...");
                sc.nextLine();
                break;
            }

            System.out.println("\n--- YOUR BOOKING HISTORY ---");
            System.out.printf("%-4s | %-10s | %-20s | %-20s | %-10s\n", 
                              "No.", "Room No", "Booking Date", "Time Slot", "Status");
            System.out.println("---------------------------------------------------------------------------------");

            for (int i = 0; i < userBookings.size(); i++) {
                Booking b = userBookings.get(i);
                
                Facility f = FileManager.getFacilityById(b.getFacilityID());
                String roomNo = (f != null) ? f.getRoomNo() : b.getFacilityID();

                String dateDisplay = java.time.LocalDate.parse(b.getBookingDate(), 
                        java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"))
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"));

                String timeDisplay = Constants.TIME_SLOTS[b.getTimeSlot()];

                System.out.printf("%-4d | %-10s | %-20s | %-20s | %-10s\n", 
                        (i + 1), roomNo, dateDisplay, timeDisplay, b.getStatus());
            }

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

    private void showBookingDetail(Booking selected) {
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

    private void reportIssue() {
        System.out.println("\n========== REPORT FACILITY ISSUE ==========");
        System.out.println("(Type 'B' to go back, 'C' to cancel completely)");

        String block = "", type = "", floor = "";
        Facility selectedFacility = null;
        int step = 1;

        List<Facility> allFacilities = FileManager.loadAllFacilities();
        if (allFacilities.isEmpty()) {
            System.out.println(">> No facilities found in system.");
            return;
        }

        while (step <= 5) {
            switch (step) {
                case 1: 
                    List<String> blocks = allFacilities.stream()
                            .map(Facility::getBlock).distinct().sorted().collect(Collectors.toList());
                    block = selectWithBack(blocks, "Block");
                    if (block == null) return; 
                    if (block.equals("BACK")) return;
                    step++;
                    break;

                case 2: 
                    final String b2 = block;
                    List<String> types = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b2))
                            .map(Facility::getType).distinct().sorted().collect(Collectors.toList());
                    type = selectWithBack(types, "Facility Type");
                    if (type == null) return;
                    if (type.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 3: 
                    final String b3 = block, t3 = type;
                    List<String> floors = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b3) && f.getType().equalsIgnoreCase(t3))
                            .map(Facility::getFloor).distinct().sorted().collect(Collectors.toList());
                    floor = selectWithBack(floors, "Floor");
                    if (floor == null) return;
                    if (floor.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 4: 
                    final String b4 = block, t4 = type, f4 = floor;
                    List<Facility> filtered = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b4) && 
                                         f.getType().equalsIgnoreCase(t4) && 
                                         f.getFloor().equalsIgnoreCase(f4))
                            .collect(Collectors.toList());

                    if (filtered.isEmpty()) {
                        System.out.println(">> No rooms found for this criteria. Going back...");
                        step--; break;
                    }

                    System.out.println("\n--- Select Facility to Report ---");
                    System.out.printf("%-4s | %-10s | %-30s\n", "No.", "Room No", "Facility Name");
                    System.out.println("-".repeat(50));
                    for (int i = 0; i < filtered.size(); i++) {
                        System.out.printf("[%-2d] | %-10s | %-30s\n", (i + 1), filtered.get(i).getRoomNo(), filtered.get(i).getName());
                    }
                    System.out.print("Select No. [B: Back, C: Cancel]: ");
                    String choice = sc.nextLine().trim().toUpperCase();

                    if (choice.equals("C")) return;
                    if (choice.equals("B")) { step--; break; }

                    if (my.edu.utar.util.Validator.isValidMenuChoice(choice, 1, filtered.size())) {
                        selectedFacility = filtered.get(Integer.parseInt(choice) - 1);
                        step++;
                    } else {
                        System.out.println(">> Invalid selection.");
                    }
                    break;

                case 5:
                    System.out.println("\nSelected: " + selectedFacility.getRoomNo() + " (" + selectedFacility.getName() + ")");
                    
                    System.out.println("\n--- Select Issue Type ---");
                    for (int i = 0; i < Constants.ISSUE_TYPES.length; i++) {
                        System.out.println("[" + (i + 1) + "] " + Constants.ISSUE_TYPES[i]);
                    }
                    System.out.print("Choice: ");
                    String typeIn = sc.nextLine().trim();
                    if (!my.edu.utar.util.Validator.isValidMenuChoice(typeIn, 1, Constants.ISSUE_TYPES.length)) {
                        System.out.println(">> Invalid type. Start over this step.");
                        break;
                    }
                    String issueType = Constants.ISSUE_TYPES[Integer.parseInt(typeIn) - 1];

                    System.out.print("Enter detailed description of the issue: ");
                    String description = sc.nextLine().trim();
                    if (description.isEmpty()) {
                        System.out.println(">> Description cannot be empty.");
                        break;
                    }

                    System.out.print("\nConfirm submission? (Y/N): ");
                    if (sc.nextLine().trim().equalsIgnoreCase("Y")) {
                        String issueID = FileManager.generateMaintenanceID();
                        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                        
                        MaintenanceReport report = new MaintenanceReport(
                            issueID, selectedFacility.getFacilityID(), currentUser.getId(),
                            issueType, description, date
                        );
                        
                        FileManager.appendMaintenanceReport(report);
                        System.out.println("\n>> SUCCESS! Issue reported. ID: " + issueID);
                        System.out.println("Press Enter to return to menu...");
                        sc.nextLine();
                        return;
                    } else {
                        System.out.println(">> Submission cancelled.");
                        return;
                    }
            }
        }
    }
    
    private void viewMyReportedIssues() {
        System.out.println("\n  ====================== MY REPORTED ISSUES ======================");
        
        List<MaintenanceReport> allReports = FileManager.loadAllMaintenanceReports();
        List<Facility> allFacilities = FileManager.loadAllFacilities(); // Needed for Room Numbers

        List<MaintenanceReport> myReports = allReports.stream()
            .filter(r -> r.getReporterID().equals(currentUser.getId()))
            .collect(Collectors.toList());

        if (myReports.isEmpty()) {
            System.out.println("  >> You have not reported any issues yet.");
            System.out.println("  Press Enter to continue...");
            sc.nextLine();
            return;
        }
        System.out.printf("  %-10s | %-12s | %-25s | %-15s\n", "Room", "Date", "Issue Type", "Status");
        System.out.println("  " + "-".repeat(68));

        for (MaintenanceReport report : myReports) {
            
            String roomNo = allFacilities.stream()
                .filter(fac -> fac.getFacilityID().equals(report.getFacilityID()))
                .map(Facility::getRoomNo)
                .findFirst()
                .orElse(report.getFacilityID());

            String rawDate = report.getReportDate();
            String formattedDate = rawDate.substring(0,2) + "-" + rawDate.substring(2,4) + "-" + rawDate.substring(4);

            System.out.printf("  %-10s | %-12s | %-25s | %-15s\n", 
                roomNo, 
                formattedDate, 
                report.getIssueType(), 
                report.getStatus());
        }

        System.out.println("  " + "-".repeat(68));
        System.out.println("  (Note: You cannot edit reports once submitted.)");
        System.out.println("  Press Enter to return to menu...");
        sc.nextLine();
    }
    
    private void showAdminContact() {
        System.out.println("\n[!] Online booking limit is 1 month.");
        System.out.println("Contact " + Constants.ADMIN_NAME + " (" + Constants.ADMIN_PHONE + ") for advanced bookings.");
    }
}
