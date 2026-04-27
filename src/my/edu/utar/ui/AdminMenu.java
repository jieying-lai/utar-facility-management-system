
package my.edu.utar.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.List;
import my.edu.utar.service.BookingManager;
import my.edu.utar.model.Booking;
import my.edu.utar.data.FileManager;
import my.edu.utar.model.Admin;
import my.edu.utar.model.User; 
import my.edu.utar.model.Facility;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;
import my.edu.utar.service.FacilitiesService;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import my.edu.utar.model.MaintenanceReport;
import my.edu.utar.service.ReportGenerator;

public class AdminMenu {

    private Scanner sc;
    private Admin currentAdmin;
    private BookingManager bookingManager;
    private FacilitiesService facilitiesService = new FacilitiesService();

    public AdminMenu(Scanner sc, Admin currentAdmin) {
        this.sc = sc;
        this.currentAdmin = currentAdmin;
        
        this.bookingManager = new BookingManager();
        this.facilitiesService = new FacilitiesService();
    }

    public void show() {
        while (true) {
            printAdminMenu();

            String choice = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }
          
            switch (choice) {
                case "1": searchFacility();        
                		  break;
                case "2": manageFacility();       
                		  break;
                case "3": approval(sc);    
                		  break;
                case "4": facilityUsageTracking();
                          break;
                case "5": viewSummaryReport();
                		  break;
                case "6": maintenanceManagement();  
                		  break;
                case "7": issueAlert();             
                		  break;
                case "8": manageUsers(); 
                		  break;
                
                case "L":
                    System.out.println("Logged out. Goodbye, " + currentAdmin.getName() + "!");
                    return;
                default:
                    System.out.println("Invalid selection, please try again.");
            }
           }
        }
    
    private void printAdminMenu() {
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
        
        System.out.println("============================================================");
        System.out.println("            UTAR Smart Campus Management System             ");
        System.out.println("                      Admin Dashboard                       ");
        System.out.println("============================================================");

        System.out.printf("  Date : %-15s | Time : %-10s\n", currentDate, currentTime);
        System.out.println("  Welcome, " + currentAdmin.getName() + " (Admin)");
        System.out.println("------------------------------------------------------------");

        showIssueAlerts();  

        System.out.println("------------------------------------------------------------");
        System.out.printf("  %-28s  %-28s\n", "[1] Search Facility Status", "[2] Manage Facilities");
        System.out.printf("  %-28s  %-28s\n", "[3] Process Bookings",       "[4] Usage Tracking");
        System.out.printf("  %-28s  %-28s\n", "[5] View Summary Report",    "[6] Maintenance Management");
        System.out.printf("  %-28s  %-28s\n", "[7] View Issue Alerts",      "[8] Manage Users");
        System.out.println("  [L] Logout");
        System.out.println("------------------------------------------------------------");
        System.out.print("  Enter your choice: ");
    }

    private void showIssueAlerts() {
        List<MaintenanceReport> allReports = FileManager.loadAllMaintenanceReports();
        
        long pendingCount = allReports.stream()
            .filter(r -> r.getStatus().equalsIgnoreCase(Constants.STATUS_PENDING))
            .count();
            
        long inProgressCount = allReports.stream()
            .filter(r -> r.getStatus().equalsIgnoreCase(Constants.MAINT_IN_PROGRESS))
            .count();

        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║                SYSTEM ISSUE ALERTS                ║");
        System.out.println("╠═══════════════════════════════════════════════════╣");

        if (pendingCount == 0 && inProgressCount == 0) {
            System.out.println("║  [/] Status: All clear!                           ║");
            System.out.println("║      No active maintenance issues found.          ║");
        } else {
            System.out.println("║  [!] STATUS: ATTENTION REQUIRED                   ║");
            System.out.println("║                                                   ║");
            
            if (pendingCount > 0) {
                System.out.printf("║  - %-2d NEW requests are currently PENDING          ║\n", pendingCount);
            }
            
            if (inProgressCount > 0) {
                System.out.printf("║  - %-2d tasks are currently IN-PROGRESS             ║\n", inProgressCount);
            }

            System.out.println("║                                                   ║");
            System.out.println("║  Suggestion: Go to [6] Maintenance Management     ║");
            System.out.println("║  to assign or resolve these issues.               ║");
        }
        
        System.out.println("╚═══════════════════════════════════════════════════╝");
    }

    private void searchFacility() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n========== ADMIN: FACILITY SCHEDULE VIEW ==========");
        System.out.println("(Type 'B' to go back, 'C' to cancel)");

        String block = "", type = "", formattedDate = "";
        int step = 1;

        List<Facility> allFacilities = FileManager.loadAllFacilities();
        if (allFacilities.isEmpty()) {
            System.out.println(">> No facilities found.");
            return;
        }

        while (step <= 4) {
            switch (step) {
                case 1:
                    List<String> blocks = allFacilities.stream()
                            .map(Facility::getBlock).distinct().collect(Collectors.toList());
                    block = selectWithBack(blocks, "Block");
                    if (block == null) { handleCancellation(); return; }
                    if (block.equals("BACK")) return;
                    step++;
                    break;

                case 2: 
                    final String b2 = block;
                    List<String> types = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b2))
                            .map(Facility::getType).distinct().collect(Collectors.toList());
                    type = selectWithBack(types, "Facility Type");
                    if (type == null) { handleCancellation(); return; }
                    if (type.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 3: 
                    while (true) {
                        System.out.print("\nEnter Date (YYYY-MM-DD) [B: Back, C: Cancel]: ");
                        String dInput = sc.nextLine().trim().toUpperCase();
                        if (dInput.equals("C")) { handleCancellation(); return; }
                        if (dInput.equals("B")) { step--; break; }

                        try {
                            java.time.LocalDate d = java.time.LocalDate.parse(dInput);
                            formattedDate = d.format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
                            step++; 
                            break;
                        } catch (Exception e) {
                            System.out.println(">> Invalid date format. Please use YYYY-MM-DD.");
                        }
                    }
                    break;

                case 4:
                    displayScheduleTable(block, type, formattedDate, allFacilities);
                    
                    boolean validChoice = false;
                    while (!validChoice) {
                        System.out.print("\n[Enter: New Search, B: Change Date, C: Back to Main Menu]: ");
                        String endInput = sc.nextLine().trim().toUpperCase();
                        
                        if (endInput.equals("C")) {
                            handleCancellation();
                            return; 
                        } else if (endInput.equals("B")) {
                            step = 3; 
                            validChoice = true;
                        } else if (endInput.equals("") || endInput.equals("ENTER")) { // Empty string is "Enter"
                            step = 1; 
                            validChoice = true;
                        } else {
                            System.out.println(">> Invalid input '" + endInput + "'. Please choose Enter, B, or C.");
                        }
                    }
                    break;
            }
        }
    }

    private void handleCancellation() {
        System.out.println("\n>> Action Cancelled. Returning to Main Menu...");
        System.out.println("Press Enter to continue...");
        
        this.sc.nextLine(); 
    }
    
    private void displayScheduleTable(String block, String type, String fDate, List<Facility> allFacilities) {
        List<Facility> filtered = allFacilities.stream()
                .filter(f -> f.getBlock().equalsIgnoreCase(block) && f.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());

        java.time.LocalDate d = java.time.LocalDate.parse(fDate, java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy"));
        
        System.out.println("\n" + "=".repeat(110));
        System.out.println("SCHEDULE FOR : " + d.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        System.out.println("BLOCK        : " + block + " | TYPE: " + type);
        System.out.println("LEGEND       : [/] Available   [X] Occupied/Pending");
        System.out.println("=".repeat(110));
        
        System.out.printf("%-10s", "Room ID");
        for (int slot = 1; slot <= Constants.TOTAL_SLOTS; slot++) {
            System.out.printf(" | %-17s", Constants.TIME_SLOTS[slot].trim());
        }
        System.out.println("\n" + "-".repeat(110));

        for (Facility f : filtered) {
            System.out.printf("%-10s", f.getRoomNo());
            for (int slot = 1; slot <= Constants.TOTAL_SLOTS; slot++) {
                boolean free = bookingManager.isAvailable(f.getFacilityID(), fDate, slot);
                System.out.printf(" | %-17s", "        " + (free ? "/" : "X"));
            }
            System.out.println();
        }
        System.out.println("=".repeat(110));
    }

    private String selectWithBack(List<String> options, String label) {
        while (true) {
            System.out.println("\n--- Select " + label + " ---");
            for (int i = 0; i < options.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + options.get(i));
            }
            System.out.print("Selection [B: Back, C: Cancel]: ");
            String input = sc.nextLine().trim().toUpperCase();
            
            if (input.equals("C")) return null;
            if (input.equals("B")) return "BACK";
            
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= options.size()) {
                    return options.get(choice - 1);
                } else {
                    System.out.println(">> Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Please enter a valid number, 'B', or 'C'.");
            }
        }
    }
    
    private void manageFacility() {
        String block = "";
        String type = "";
        String floor = "";
        int step = 1;
        Facility selectedFacility = null;

        while (true) {
            List<Facility> allFacilities = facilitiesService.getAllFacilities();

            System.out.println("\n============================================");
            System.out.println("         FACILITIES MANAGEMENT PAGE         ");
            System.out.println("============================================");
            System.out.println("  [1] View all current facilities");
            System.out.println("  [2] Add new facilities");
            System.out.println("  [3] Modify / Delete facilities");
            System.out.println("  [0] Return to Main Menu");
            System.out.println("--------------------------------------------");
            System.out.print("Please enter your choice: ");
            
            String menuChoice = sc.nextLine().trim();

            if (Validator.isEmpty(menuChoice)) {
                System.out.println(">> Error: Input cannot be empty!");
                continue;
            }
            
            if (menuChoice.equals("0")) return;

            switch (menuChoice) {
                case "1": {
                    if (allFacilities.isEmpty()) {
                        System.out.println("No facilities found.");
                        break;
                    }

                    int pageSize = 10, currentPage = 0;
                    int totalFacilities = allFacilities.size();
                    int totalPages = (int) Math.ceil((double) totalFacilities / pageSize);

                    boolean viewing = true;
                    while (viewing) {
                        System.out.println("\n================ CURRENT FACILITIES ================");
                        System.out.printf("%-5s | %-12s | %-15s%n", "No.", "Room No", "Type");
                        System.out.println("----------------------------------------------------");
                        
                        int start = currentPage * pageSize;
                        int end = Math.min(start + pageSize, totalFacilities);

                        for (int i = start; i < end; i++) {
                            Facility f = allFacilities.get(i);
                            System.out.printf("%-5d | %-12s | %-15s%n", (i + 1), f.getRoomNo(), f.getType());
                        }

                        System.out.println("----------------------------------------------------");
                        System.out.printf("Page %d of %d | [N] Next | [P] Previous | [X] Exit%n", (currentPage + 1), totalPages);
                        System.out.print("Action: ");
                        String nav = sc.nextLine().toUpperCase();

                        if (nav.equals("N") && (currentPage + 1) < totalPages) currentPage++;
                        else if (nav.equals("P") && currentPage > 0) currentPage--;
                        else if (nav.equals("X")) viewing = false;
                        else System.out.println(">> Invalid move or end of list.");
                    }
                    break;
                }

                case "2": { 
                	block = ""; 
                    floor = ""; 
                    String roomNo = "";
                    String name = "";  
                    type = ""; 
                    int capacity = 0;
                    step = 1; 
                    boolean addingProcess = true;

                    while (addingProcess) {
                        switch (step) {
                            case 1:
                                System.out.println("\n============================================");
                                System.out.println("          ADD NEW CAMPUS FACILITY           ");
                                System.out.println("============================================");
                                System.out.print("Select Block [1] KA [2] KB [C: Cancel]: ");
                                String bChoice = sc.nextLine().trim().toUpperCase();

                                if (bChoice.equals("C")) { 
                                    System.out.println(">> Addition Cancelled.");
                                    addingProcess = false; 
                                } else if (bChoice.equals("1")) { 
                                    block = "KA"; 
                                    step++; 
                                } else if (bChoice.equals("2")) { 
                                    block = "KB"; 
                                    step++; 
                                } else { 
                                    System.out.println(">> [!] Invalid choice. Please enter 1, 2, or C."); 
                                }
                                break;

                            case 2: 
                                String[] kaFloors = {"SB", "G", "M", "1", "2", "3", "4", "5", "6", "7", "8"};
                                String[] kbFloors = {"SB", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                                String[] targetFloors = block.equals("KA") ? kaFloors : kbFloors;

                                System.out.println("\nLocation: Block " + block);
                                System.out.println("Available Floors: " + Arrays.toString(targetFloors));
                                System.out.print("Enter Floor [B: Back, C: Cancel]: ");
                                String fInput = sc.nextLine().trim().toUpperCase();

                                if (fInput.equals("C")) { addingProcess = false; break; }
                                if (fInput.equals("B")) { step--; break; }

                                if (Arrays.asList(targetFloors).contains(fInput)) {
                                    floor = fInput;
                                    step++;
                                } else {
                                    System.out.println(">> [!] Invalid Floor for Block " + block);
                                }
                                break;

                            case 3: 
                                System.out.println("\nLocation: " + block + " Floor " + floor);
                                System.out.print("Enter Room Number (e.g., " + block + "101) [B: Back, C: Cancel]: ");
                                String rInput = sc.nextLine().trim().toUpperCase();

                                if (rInput.equals("C")) { addingProcess = false; break; }
                                if (rInput.equals("B")) { step--; break; }

                                final String rSearch = rInput;
                                if (rInput.startsWith(block) && allFacilities.stream().noneMatch(f -> f.getRoomNo().equals(rSearch))) {
                                    roomNo = rInput;
                                    step++;
                                } else {
                                    System.out.println(">> [!] Invalid Format (must start with " + block + ") or Room Number already exists.");
                                }
                                break;

                            case 4: 
                                System.out.print("\nEnter Facility Name (e.g. Tutorial Room 1) [B: Back, C: Cancel]: ");
                                String nInput = sc.nextLine().trim();

                                if (nInput.equalsIgnoreCase("C")) { addingProcess = false; break; }
                                if (nInput.equalsIgnoreCase("B")) { step--; break; }

                                if (!nInput.isEmpty()) {
                                    name = nInput;
                                    step++;
                                } else {
                                    System.out.println(">> [!] Name cannot be empty.");
                                }
                                break;

                            case 5:
                                System.out.println("\nSelect Facility Type:");
                                for (int i = 0; i < Constants.FACILITY_TYPES.length; i++) {
                                    System.out.printf("[%d] %-20s ", (i + 1), Constants.FACILITY_TYPES[i]);
                                    if ((i + 1) % 2 == 0) System.out.println();
                                }
                                System.out.print("\nChoice [B: Back, C: Cancel]: ");
                                String tChoice = sc.nextLine().trim().toUpperCase();

                                if (tChoice.equals("C")) { addingProcess = false; break; }
                                if (tChoice.equals("B")) { step--; break; }

                                try {
                                    int idx = Integer.parseInt(tChoice) - 1;
                                    if (idx >= 0 && idx < Constants.FACILITY_TYPES.length) {
                                        type = Constants.FACILITY_TYPES[idx];
                                        step++;
                                    } else {
                                        System.out.println(">> [!] Invalid selection.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println(">> [!] Please enter a number.");
                                }
                                break;

                            case 6: 
                                System.out.print("\nEnter Capacity (1-300) [B: Back, C: Cancel]: ");
                                String capInput = sc.nextLine().trim().toUpperCase();

                                if (capInput.equals("C")) { addingProcess = false; break; }
                                if (capInput.equals("B")) { step--; break; }

                                try {
                                    int c = Integer.parseInt(capInput);
                                    if (c > 0 && c <= 300) {
                                        capacity = c;
                                        step++;
                                    } else {
                                        System.out.println(">> [!] Capacity must be between 1 and 300.");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println(">> [!] Please enter a valid number.");
                                }
                                break;

                            case 7:
                                int nextIdNum = 1;
                                for (Facility existing : allFacilities) {
                                    try {
                                        int currentIdNum = Integer.parseInt(existing.getFacilityID().substring(1));
                                        if (currentIdNum >= nextIdNum) nextIdNum = currentIdNum + 1;
                                    } catch (Exception e) { }
                                }
                                String finalID = String.format("F%03d", nextIdNum);

                                System.out.println("\n============================================");
                                System.out.println("        CONFIRM NEW FACILITY DETAILS        ");
                                System.out.println("============================================");
                                System.out.println("ID:       " + finalID);
                                System.out.println("Room:     " + roomNo);
                                System.out.println("Name:     " + name);
                                System.out.println("Type:     " + type);
                                System.out.println("Location: Block " + block + ", Floor " + floor);
                                System.out.println("Capacity: " + capacity);
                                System.out.println("--------------------------------------------");
                                System.out.print("Save this facility? (Y/N) [B: Back]: ");
                                String confirm = sc.nextLine().trim().toUpperCase();

                                if (confirm.equals("B")) { 
                                    step--; 
                                } else if (confirm.equals("Y")) {
                                    Facility newFacility = new Facility(finalID, block, floor, roomNo, name, type, capacity, "Available");
                                    if (facilitiesService.addFacility(newFacility) == null) {
                                        System.out.println("\n>> SUCCESS: Facility [" + roomNo + "] added permanently.");
                                    } else {
                                        System.out.println("\n[!] ERROR: Failed to write to data file.");
                                    }
                                    addingProcess = false; // Finish
                                } else {
                                    System.out.println(">> Save cancelled. Returning to step 6.");
                                }
                                break;
                        }
                    }
                    break; 
                }

                case "3": { 
                    step = 1;
                    boolean inModifyMode = true;
                    while (inModifyMode) {
                        switch (step) {
                            case 1: 
                                List<String> blocks = allFacilities.stream().map(Facility::getBlock).distinct().sorted().collect(Collectors.toList());
                                block = selectWithBack(blocks, "Block");
                                if (block == null || block.equals("BACK")) { inModifyMode = false; break; }
                                step++; break;

                            case 2: 
                                final String b2 = block;
                                List<String> floors = allFacilities.stream().filter(f -> f.getBlock().equals(b2)).map(Facility::getFloor).distinct().sorted().collect(Collectors.toList());
                                floor = selectWithBack(floors, "Floor");
                                if (floor == null) { inModifyMode = false; break; }
                                if (floor.equals("BACK")) { step--; break; }
                                step++; break;

                            case 3:
                                final String b3 = block, f3 = floor;
                                List<String> types = allFacilities.stream().filter(f -> f.getBlock().equals(b3) && f.getFloor().equals(f3)).map(Facility::getType).distinct().sorted().collect(Collectors.toList());
                                type = selectWithBack(types, "Facility Type");
                                if (type == null) { inModifyMode = false; break; }
                                if (type.equals("BACK")) { step--; break; }
                                step++; break;

                            case 4:
                                final String b4 = block, f4 = floor, t4 = type;
                                List<Facility> filtered = allFacilities.stream()
                                        .filter(f -> f.getBlock().equalsIgnoreCase(b4) 
                                                  && f.getFloor().equalsIgnoreCase(f4) 
                                                  && f.getType().equalsIgnoreCase(t4))
                                        .collect(Collectors.toList());

                                if (filtered.isEmpty()) { 
                                    System.out.println("\n>> No facilities found for this selection. Returning to start..."); 
                                    step = 1; 
                                    break; 
                                }

                                System.out.println("\n==============================================");
                                System.out.println("            SELECT SPECIFIC FACILITY          ");
                                System.out.println("==============================================");
                                System.out.printf("%-5s | %-12s | %-20s\n", "No.", "Room No", "Facility Name");
                                System.out.println("----------------------------------------------");

                                for (int i = 0; i < filtered.size(); i++) {
                                    Facility f = filtered.get(i);
                                    System.out.printf("[%d]   | %-12s | %-20s\n", (i + 1), f.getRoomNo(), f.getName());
                                }
                                System.out.println("----------------------------------------------");
                                System.out.print("Select Number [B: Back, C: Cancel]: ");
                                String sel = sc.nextLine().trim().toUpperCase();

                                if (sel.equals("C")) { inModifyMode = false; break; }
                                if (sel.equals("B")) { step--; break; }

                                if (Validator.isValidMenuChoice(sel, 1, filtered.size())) {
                                    selectedFacility = filtered.get(Integer.parseInt(sel) - 1);
                                    step++; // Move to Case 5 (Action)
                                } else {
                                    System.out.println(">> Error: Invalid selection. Please choose a number from the list.");
                                }
                                break;

                            case 5: 
                                System.out.println("\n==============================================");
                                System.out.println("           FACILITY ACTION MENU               ");
                                System.out.println("==============================================");
                                selectedFacility.display(); 
                                System.out.println("----------------------------------------------");
                                System.out.println("[1] Modify Details");
                                System.out.println("[2] Delete Facility");
                                System.out.print("Action [B: Back, C: Cancel]: ");
                                String action = sc.nextLine().trim().toUpperCase();

                                if (action.equals("C")) { 
                                    inModifyMode = false; 
                                    break; 
                                }
                                if (action.equals("B")) { 
                                    step--; 
                                    break; 
                                }

                                if (action.equals("1")) {
                                    performModification(selectedFacility, allFacilities);
                                    
                                    System.out.println("\n>> FACILITY UPDATED SUCCESSFULLY!");
                                    System.out.println("==============================================");
                                    selectedFacility.display(); 
                                    System.out.println("==============================================");
                                    System.out.println("Press Enter to continue...");
                                    sc.nextLine();
                                    inModifyMode = false; 
                                } 
                                else if (action.equals("2")) {
                                    String deletedID = selectedFacility.getFacilityID();
                                    boolean success = performDeletion(selectedFacility, allFacilities);
                                    
                                    if (success) {
                                        System.out.println("\n>> SUCCESS: Facility [" + deletedID + "] has been removed.");
                                        System.out.println("Press Enter to continue...");
                                        sc.nextLine();
                                        inModifyMode = false; 
                                    } 
                                } 
                                else {
                                    System.out.println("\n>> [!] Invalid action. Please enter '1', '2', 'B', or 'C'.");
                                    System.out.println(">> Please try again.");
                                }
                                break;
                        }
                    }
                    break;
                }
                default:
                    System.out.println(">> Invalid selection.");
            }
        }
    }
       
    private void performModification(Facility target, List<Facility> allF) {
        boolean isBooked = FileManager.isFacilityInBookings(target.getFacilityID());
        if (isBooked) {
            System.out.println("\n[!] WARNING: This facility (ID: " + target.getFacilityID() + ") has existing bookings.");
            System.out.print("Modifying details may cause data inconsistency. Modify anyway? (Y/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("Y")) return;
        }

        Facility original = new Facility(target.getFacilityID(), target.getBlock(), target.getFloor(), 
                                        target.getRoomNo(), target.getName(), target.getType(), 
                                        target.getCapacity(), target.getStatus());

        while (true) {
            System.out.println("\n==============================================");
            System.out.println("            MODIFICATION MENU                 ");
            System.out.println("==============================================");
            System.out.printf("[1] Block        : %s\n", target.getBlock());
            System.out.printf("[2] Floor        : %s\n", target.getFloor());
            System.out.printf("[3] Room Number  : %s\n", target.getRoomNo());
            System.out.printf("[4] Facility Name: %s\n", target.getName());
            System.out.printf("[5] Facility Type: %s\n", target.getType());
            System.out.printf("[6] Capacity     : %d\n", target.getCapacity());
            System.out.println("----------------------------------------------");
            System.out.println("[S] Save and Exit");
            System.out.println("[C] Cancel All Changes");
            System.out.print("Select field to edit: ");
            
            String mChoice = sc.nextLine().trim().toUpperCase();

            if (mChoice.equals("C")) {
                target.setBlock(original.getBlock());
                target.setFloor(original.getFloor());
                target.setRoomNo(original.getRoomNo());
                target.setName(original.getName());
                target.setType(original.getType());
                target.setCapacity(original.getCapacity());
                System.out.println(">> Modifications discarded.");
                return;
            }
            if (mChoice.equals("S")) break;

            switch (mChoice) {
                case "1": 
                    System.out.print("New Block [1] KA [2] KB: ");
                    String bInput = sc.nextLine().trim();
                    String newBlock = bInput.equals("1") ? "KA" : "KB";
                    
                    if (!target.getRoomNo().startsWith(newBlock)) {
                        System.out.println("\n[!] CONFLICT: The Room Number (" + target.getRoomNo() + ") belongs to " + target.getBlock() + ".");
                        System.out.print("Are you sure you want to change the Block to " + newBlock + "? (Y/N): ");
                        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) {
                            System.out.println(">> Change cancelled.");
                            break;
                        }
                    }
                    target.setBlock(newBlock);
                    break;

                case "2": 
                    String[] kaFloors = {"SB", "G", "M", "1", "2", "3", "4", "5", "6", "7", "8"};
                    String[] kbFloors = {"SB", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                    String[] targetFloors = target.getBlock().equals("KA") ? kaFloors : kbFloors;
                    
                    System.out.println("\nAvailable Floors for Block " + target.getBlock() + ": " + Arrays.toString(targetFloors));
                    System.out.print("Enter New Floor: ");
                    String fInput = sc.nextLine().trim().toUpperCase();
                    if (Arrays.asList(targetFloors).contains(fInput)) {
                        target.setFloor(fInput);
                    } else {
                        System.out.println(">> [!] Invalid Floor for this block. Try again.");
                    }
                    break;

                case "3": 
                    System.out.print("Enter New Room No: ");
                    String newRoom = sc.nextLine().trim().toUpperCase();
                    if (!newRoom.startsWith(target.getBlock())) {
                        System.out.println("\n[!] WARNING: New Room Number " + newRoom + " does not start with current Block " + target.getBlock() + ".");
                        System.out.print("Proceed anyway? (Y/N): ");
                        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) break;
                    }
                    target.setRoomNo(newRoom);
                    break;

                case "4":
                    System.out.print("Enter New Facility Name: ");
                    String newName = sc.nextLine().trim();
                    if (!Validator.isEmpty(newName)) {
                        target.setName(newName);
                    } else {
                        System.out.println(">> [!] Name cannot be empty.");
                    }
                    break;

                case "5":
                    System.out.println("\nSelect New Type:");
                    for (int i = 0; i < Constants.FACILITY_TYPES.length; i++) {
                        System.out.printf("[%d] %-20s ", (i + 1), Constants.FACILITY_TYPES[i]);
                        if ((i + 1) % 2 == 0) System.out.println();
                    }
                    System.out.print("\nChoice: ");
                    String tChoice = sc.nextLine().trim();
                    if (Validator.isValidMenuChoice(tChoice, 1, Constants.FACILITY_TYPES.length)) {
                        target.setType(Constants.FACILITY_TYPES[Integer.parseInt(tChoice) - 1]);
                    }
                    break;

                case "6": 
                    System.out.print("Enter New Capacity (1-300): ");
                    String capStr = sc.nextLine().trim();
                    if (Validator.isNumeric(capStr)) {
                        int c = Integer.parseInt(capStr);
                        if (c >= 1 && c <= 300) target.setCapacity(c);
                        else System.out.println(">> [!] Capacity must be 1-300.");
                    } else {
                        System.out.println(">> [!] Invalid numeric input.");
                    }
                    break;
                
                default:
                    System.out.println(">> [!] Invalid selection. Please try again.");
            }
        }

        if (facilitiesService.updateFacilities(allF)) {
            System.out.println("\n==============================================");
            System.out.println(">> UPDATE SUCCESSFUL: File data has been saved.");
            System.out.println("==============================================");
        } else {
            System.out.println("\n[!] SYSTEM ERROR: Failed to update the database file.");
        }
    }
        
    private boolean performDeletion(Facility target, List<Facility> allF) {
        boolean hasActive = FileManager.hasActiveBookings(target.getFacilityID());
        
        if (hasActive) {
            System.out.println("\nERROR: Cannot delete facility " + target.getFacilityID());
            System.out.println(">> This facility has Active/Confirmed/Pending bookings.");
            System.out.println(">> Please resolve or cancel those bookings before deleting.");
            System.out.println("Press Enter to return...");
            sc.nextLine();
            return false;
        }

        System.out.println("\n==============================================");
        System.out.println("           CONFIRM DELETION                   ");
        System.out.println("==============================================");
        System.out.printf("%-15s : %s\n", "ID", target.getFacilityID());
        System.out.printf("%-15s : %s\n", "Room No", target.getRoomNo());
        System.out.printf("%-15s : %s\n", "Name", target.getName());
        System.out.println("----------------------------------------------");
        System.out.print("Are you absolutely sure? (Y/N): ");
        
        String confirm = sc.nextLine().trim().toUpperCase();

        if (confirm.equals("Y")) {
            allF.remove(target);
            if (facilitiesService.updateFacilities(allF)) {
                return true; // <--- SUCCESS
            } else {
                System.out.println("\n>> [!] SYSTEM ERROR: Failed to update database file.");
                return false;
            }
        } else {
            System.out.println("\n>> Deletion cancelled. Facility is safe.");
            return false; 
        }
    }
    
    private void approval(Scanner sc) {
        List<Booking> pendingList = bookingManager.getPendingBookingsSorted();

        if (pendingList.isEmpty()) {
            System.out.println("\n[!] No pending booking requests found.");
            return;
        }

        List<String> userLines = FileManager.readAllLines(Constants.FILE_USERS);

        System.out.println("\n" + "=".repeat(100));
        System.out.println("                                PENDING BOOKING REQUESTS                                  ");
        System.out.println("=".repeat(100));
        
        String format = "%-4s | %-10s | %-15s | %-10s | %-25s | %-10s%n";
        
        System.out.printf(format, "No", "User ID", "User Name", "Room ID", "Facility", "Status");
        System.out.println("-".repeat(100));

        for (int i = 0; i < pendingList.size(); i++) {
            Booking b = pendingList.get(i);
            String userId = b.getUserID();
            
            String uName = "Unknown";
            for (String uLine : userLines) {
                String[] uData = uLine.split("\\|");
                if (uData.length >= 2 && uData[0].equals(userId)) {
                    uName = uData[1];
                    break;
                }
            }

            String fName = facilitiesService.getFacilityNameById(b.getFacilityID());

            System.out.printf(format, 
                              (i + 1), 
                              userId, 
                              (uName.length() > 15 ? uName.substring(0, 12) + "..." : uName), 
                              b.getFacilityID(), 
                              (fName.length() > 25 ? fName.substring(0, 22) + "..." : fName), 
                              b.getStatus());
        }
        System.out.println("=".repeat(100));

        System.out.print("\nSelect No. to process (B to back): ");
        String inputStr = sc.nextLine();
        
        if (inputStr.equalsIgnoreCase("B")) return;

        try {
            int index = Integer.parseInt(inputStr) - 1;
            if (index >= 0 && index < pendingList.size()) {
                processDecision(pendingList.get(index).getBookingID(), sc);
            } else {
                System.out.println("Invalid selection number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number or 'B'.");
        }
    }

    private void processDecision(String bookingID, Scanner sc) {
        Booking b = bookingManager.findBooking(bookingID);
        
        if (b == null) {
            System.out.println("Error: Booking details not found.");
            return;
        }

        String uName = getUserName(b.getUserID());
        String fName = facilitiesService.getFacilityNameById(b.getFacilityID());
        
        String slotTime = "Unknown Slot";
        if (b.getTimeSlot() >= 1 && b.getTimeSlot() < Constants.TIME_SLOTS.length) {
            slotTime = Constants.TIME_SLOTS[b.getTimeSlot()];
        }

        System.out.println("\n==================================================");
        System.out.println("                BOOKING DETAILS                   ");
        System.out.println("==================================================");
        System.out.printf("Booking ID:          %s%n", b.getBookingID());
        System.out.printf("Apply Date:          %s%n", formatDate(b.getApplyDate())); 
        System.out.println("--------------------------------------------------");
        System.out.printf("User ID:             %s%n", b.getUserID());
        System.out.printf("User Name:           %s%n", uName);
        System.out.println("--------------------------------------------------");
        System.out.printf("Room ID:             %s%n", b.getFacilityID());
        System.out.printf("Facility Name:       %s%n", fName);
        System.out.printf("Booked Date:         %s%n", formatDate(b.getBookingDate())); 
        System.out.printf("Booked Time Slot:    %s%n", slotTime);
        System.out.println("--------------------------------------------------");

        System.out.println("\nHow would you like to proceed?");
        System.out.println("[1] Approve");
        System.out.println("[2] Reject");
        System.out.println("[B] Back to list (No changes)");
        System.out.print("Selection: ");
        
        String choice = sc.nextLine().toUpperCase();

        switch (choice) {
        case "1":
            if (bookingManager.approveBooking(bookingID)) {
                System.out.println("Booking Approved successfully!");
            }
            System.out.println("\nPress Enter to continue...");
            sc.nextLine(); 
            break;

        case "2":
            String reason = "";
            while (true) {
                System.out.print("Enter reason for rejection (or 'B' to cancel): ");
                reason = sc.nextLine().trim();

                if (reason.equalsIgnoreCase("B")) {
                    processDecision(bookingID, sc); 
                    return; 
                }

                if (reason.isEmpty()) {
                    System.out.println("Error: Rejection reason cannot be empty. Please provide a reason.");
                } else {
                    break; 
                }
            }

            if (bookingManager.rejectBooking(bookingID, reason)) {
                System.out.println("Booking Rejected. Reason recorded.");
            }
            System.out.println("\nPress Enter to continue...");
            sc.nextLine(); 
            break;

        case "B":
            break;

        default:
            System.out.println("Invalid choice.");
            System.out.println("Press Enter to continue...");
            sc.nextLine();
            break;
        }
    }

    private void facilityUsageTracking() {
        bookingManager.loadFromFile(); 
        List<Booking> allBookings = bookingManager.getBookingList();
        List<Facility> allFacilities = facilitiesService.getAllFacilities();

        System.out.println("\nSelect Period: \n[1] Current Month \n[2] Current Trimester \n[3] Current Year \n[4] All-Time");
        System.out.print("Choice: ");
        String choice = sc.nextLine().trim();

        LocalDate now = LocalDate.now();
        List<Booking> filtered = new ArrayList<>();
        String periodLabel = "";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("ddMMyyyy");
        
        for (Booking b : allBookings) {
            try {
                String rawDate = b.getBookingDate().trim();
                LocalDate d = LocalDate.parse(rawDate, dtf);

                boolean include = false;
                switch(choice) {
                    case "1":
                        periodLabel = now.getMonth().toString();
                        if (d.getMonth() == now.getMonth() && d.getYear() == now.getYear()) include = true;
                        break;
                    case "2":
                        int m = d.getMonthValue();
                        if (m <= 5) periodLabel = "Trimester 1 (Jan-May)";
                        else if (m <= 9) periodLabel = "Trimester 2 (Jun-Sep)";
                        else periodLabel = "Trimester 3 (Oct-Dec)";
                        include = true; 
                        break;
                    case "3":
                        periodLabel = "Year " + now.getYear();
                        if (d.getYear() == now.getYear()) include = true;
                        break;
                    default:
                        periodLabel = "All-Time Records";
                        include = true;
                }
                if (include) filtered.add(b);
            } catch (Exception e) {
                try {
                    LocalDate d = LocalDate.parse(b.getBookingDate(), DateTimeFormatter.ofPattern("yyyyMMdd"));
                    filtered.add(b);
                } catch (Exception e2) { continue; }
            }
        }

        Map<String, Integer> typeMap = new HashMap<>();
        Map<Integer, Integer> slotFreq = new HashMap<>();
        int pnd = 0, app = 0, rej = 0, can = 0;

        for (Booking b : filtered) {
            String s = b.getStatus().trim();
            if (s.equalsIgnoreCase("Approved")) app++;
            else if (s.equalsIgnoreCase("Pending")) pnd++;
            else if (s.equalsIgnoreCase("Rejected")) rej++;
            else if (s.equalsIgnoreCase("Cancelled")) can++;

            slotFreq.put(b.getTimeSlot(), slotFreq.getOrDefault(b.getTimeSlot(), 0) + 1);

            allFacilities.stream()
                .filter(f -> f.getFacilityID().equals(b.getFacilityID()))
                .findFirst()
                .ifPresent(f -> typeMap.put(f.getType(), typeMap.getOrDefault(f.getType(), 0) + 1));
        }

        int peakIdx = slotFreq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(0);
        String peakTimeStr = (peakIdx > 0 && peakIdx < Constants.TIME_SLOTS.length) 
                             ? Constants.TIME_SLOTS[peakIdx] : "N/A";

        String hr = "═".repeat(60);
        System.out.println("\n╔" + hr + "╗");
        System.out.printf("║ %-58s ║%n", "REPORT PERIOD: " + periodLabel);
        System.out.println("╠" + hr + "╣");
        System.out.printf("║ %-22s : %-33d ║%n", "Total Records Found", filtered.size());
        System.out.printf("║ %-22s : %-33d ║%n", "Approved Bookings", app);
        System.out.printf("║ %-22s : %-33s ║%n", "Peak Time Slot", peakTimeStr.trim());
        System.out.println("╠" + hr + "╣");
        
        String statusLine = String.format("PENDING: %d | APPROVED: %d | REJECTED: %d | CANCEL: %d", 
                                           pnd, app, rej, can);
        System.out.printf("║ %-58s ║%n", statusLine);
        System.out.println("╚" + hr + "╝");

        System.out.println("\n[ DEMAND BY FACILITY TYPE ]");
        System.out.println("┌───────────────────────────────────┬──────────────┐");
        System.out.printf("│ %-33s │ %-12s │%n", "Facility Category", "Bookings");
        System.out.println("├───────────────────────────────────┼──────────────┤");
        for (String type : Constants.FACILITY_TYPES) {
            System.out.printf("│ %-33s │ %-12d │%n", type, typeMap.getOrDefault(type, 0));
        }
        System.out.println("└───────────────────────────────────┴──────────────┘");

        System.out.println("\nPress Enter to return...");
        sc.nextLine();
    }
    
    private String getUserName(String userId) {
        try {
            List<String> lines = FileManager.readAllLines(Constants.FILE_USERS); 
            for (String line : lines) {
                String[] data = line.split("\\|");
                if (data.length >= 2 && data[0].equals(userId)) {
                    return data[1]; 
                }
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }
    
    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "N/A";
        
        java.time.format.DateTimeFormatter inputFormat1 = java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy");
        java.time.format.DateTimeFormatter inputFormat2 = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");
        java.time.format.DateTimeFormatter outputFormat = java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy");

        try {
            java.time.LocalDate date;
            if (dateStr.length() == 8 && !dateStr.startsWith("20")) {
                date = java.time.LocalDate.parse(dateStr, inputFormat1);
            } else {
                date = java.time.LocalDate.parse(dateStr, inputFormat2);
            }
            return date.format(outputFormat);
        } catch (Exception e) {
            return dateStr;
        }
    }
    
    private void viewSummaryReport() {
        bookingManager.loadFromFile(); 
        List<Facility> allFacs = facilitiesService.getAllFacilities();
        List<Booking> allBooks = bookingManager.getBookingList();
        List<MaintenanceReport> maintLogs = FileManager.loadAllMaintenanceReports();

        Map<String, Integer> typeBookingCount = new HashMap<>();
        Map<String, Integer> typeMaintCount = new HashMap<>();
        Map<String, Integer> facilityTypeCount = new HashMap<>();
        
        for (String type : Constants.FACILITY_TYPES) {
            typeBookingCount.put(type, 0);
            typeMaintCount.put(type, 0);
            facilityTypeCount.put(type, 0);
        }

        for (Facility f : allFacs) {
            String type = f.getType();
            facilityTypeCount.put(type, facilityTypeCount.get(type) + 1);
            
            long issues = maintLogs.stream()
                .filter(r -> r.getFacilityID().equals(f.getFacilityID()))
                .count();
            typeMaintCount.put(type, typeMaintCount.get(type) + (int)issues);
        }

        for (Booking b : allBooks) {
            if (b.getStatus().equalsIgnoreCase("Approved")) {
                allFacs.stream()
                    .filter(f -> f.getFacilityID().equals(b.getFacilityID()))
                    .findFirst()
                    .ifPresent(f -> typeBookingCount.put(f.getType(), typeBookingCount.get(f.getType()) + 1));
            }
        }

        System.out.println("\n" + "=".repeat(95));
        System.out.println("                 UNIVERSITY FACILITY STRATEGIC ANALYTICS DASHBOARD");
        System.out.println("=".repeat(95));

        System.out.printf("| %-20s | %-10s | %-10s | %-10s | %-10s | %-10s |\n", 
                          "Facility Type", "Units", "Approved", "Util %", "Issues", "Health");
        System.out.println("-".repeat(95));

        for (String type : Constants.FACILITY_TYPES) {
            int units = facilityTypeCount.get(type);
            if (units == 0) continue;

            int books = typeBookingCount.get(type);
            int issues = typeMaintCount.get(type);
            double utilRate = (books / (units * 50.0)) * 100; 
            
            String health = (issues > (units * 2)) ? "CRITICAL" : (issues > 0 ? "STABLE" : "EXCELLENT");

            System.out.printf("| %-20s | %-10d | %-10d | %-9.1f%% | %-10d | %-10s |\n", 
                              type, units, books, utilRate, issues, health);
        }
        System.out.println("=".repeat(95));

        double campusUtil = typeBookingCount.values().stream().mapToInt(Integer::intValue).sum() / (allFacs.size() * 50.0) * 100;
        int roomsUnderMaint = (int)allFacs.stream().filter(f -> f.getStatus().equals(Constants.FACILITY_MAINTENANCE)).count();

        System.out.println("\n┌─────────────────────────── DASHBOARD INSIGHTS ───────────────────────────┐");
        System.out.printf("│  %-35s : %-32.1f%% │\n", "Overall Campus Utilization", campusUtil);
        System.out.printf("│  %-35s : %-33d │\n", "Facilities Currently Under Main.", roomsUnderMaint);
        System.out.printf("│  %-35s : %-33s │\n", "Maintenance Productivity", "2.5 Day Avg Repair Time");
        System.out.println("└──────────────────────────────────────────────────────────────────────────┘");

        System.out.println("\n[!] CRITICAL ALERTS: FACILITIES WITH FREQUENT FAILURES");
        System.out.println("-------------------------------------------------------------------------------");
        boolean hasSeriousAlerts = false;
        
        for (Facility f : allFacs) {
            long issueCount = maintLogs.stream().filter(r -> r.getFacilityID().equals(f.getFacilityID())).count();
            if (issueCount >= 3) {
                System.out.printf(" >> ROOM %-8s (%-15s) : %-2d Total Issues | Action: Replace Resource\n", 
                                  f.getRoomNo(), f.getType(), issueCount);
                hasSeriousAlerts = true;
            }
        }
        
        if (!hasSeriousAlerts) {
            System.out.println("   No high-frequency failure patterns detected.");
        }

        System.out.println("\n" + "=".repeat(95));
        System.out.println("End of Report. Press Enter to return...");
        sc.nextLine();
    }
    
    private void maintenanceManagement() {
        while (true) {
            List<MaintenanceReport> allReports = FileManager.loadAllMaintenanceReports();

            List<MaintenanceReport> pending = allReports.stream()
                .filter(r -> !r.getStatus().equalsIgnoreCase(Constants.MAINT_RESOLVED))
                .collect(Collectors.toList());

            System.out.println("\n=============== MAINTENANCE MANAGEMENT ===============");
            System.out.println("Pending Issues: " + pending.size());
            System.out.println("------------------------------------------------------");
            System.out.println("[1] View & Update Pending Tasks");
            System.out.println("[2] View Full Maintenance History");
            System.out.println("[B] Back to Admin Menu");
            System.out.print("Selection: ");
            
            String mainChoice = sc.nextLine().trim().toUpperCase();
            
            if (mainChoice.equals("B")) break;

            switch (mainChoice) {
                case "1":
                    updateIssueWorkflow(allReports, pending);
                    break;
                case "2":
                    viewMaintenanceHistoryPaged(allReports);
                    break;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        }
    }

    private void updateIssueWorkflow(List<MaintenanceReport> allReports, List<MaintenanceReport> pending) {
        if (pending.isEmpty()) {
            System.out.println("\n>> No pending tasks found.");
            return;
        }

        List<Facility> facilities = FileManager.loadAllFacilities();
        List<User> allUsers = FileManager.loadAllUsers();
        List<Admin> admins = FileManager.loadAllAdmins();
        System.out.println("\n--- PENDING MAINTENANCE LIST ---");
        System.out.printf("%-4s | %-14s | %-10s | %-27s | %-12s\n", "No.", "Issue ID", "Room", "Type", "Status");
        System.out.println("-".repeat(77));
        
        for (int i = 0; i < pending.size(); i++) {
            MaintenanceReport r = pending.get(i);
            Facility f = facilities.stream()
                .filter(fac -> fac.getFacilityID().equalsIgnoreCase(r.getFacilityID()))
                .findFirst().orElse(null);
            
            System.out.printf("%-4d | %-14s | %-10s | %-27s | %-12s\n", 
                (i + 1), r.getIssueID(), (f != null ? f.getRoomNo() : r.getFacilityID()), r.getIssueType(), r.getStatus());
        }

        System.out.print("\nEnter No. to update [B: Back]: ");
        String input = sc.nextLine().trim().toUpperCase();
        if (input.equals("B")) return;

        try {
            int choice = Integer.parseInt(input);
            if (choice < 1 || choice > pending.size()) {
                System.out.println(">> Invalid selection.");
                return;
            }

            MaintenanceReport target = pending.get(choice - 1);
            
            Facility f = facilities.stream()
                .filter(fac -> fac.getFacilityID().equalsIgnoreCase(target.getFacilityID()))
                .findFirst().orElse(null);

            User reporter = allUsers.stream()
                .filter(u -> u.getId().equals(target.getReporterID()))
                .findFirst().orElse(null);
            Admin adminObj = admins.stream()
                .filter(a -> a.getId().equalsIgnoreCase(target.getAssignedTo()))
                .findFirst().orElse(null);

            System.out.println("\n-----------------------------------------------------");
            System.out.println("            MAINTENANCE REPORT DETAILS               ");
            System.out.println("-----------------------------------------------------");
            System.out.println("Maintenance ID     : " + target.getIssueID());
            System.out.println("Room ID            : " + (f != null ? f.getRoomNo() : target.getFacilityID()));
            System.out.println("Room Name          : " + (f != null ? f.getName() : "N/A"));
            System.out.println("Facility Type      : " + (f != null ? f.getType() : "N/A"));
            System.out.println("-----------------------------------------------------");
            System.out.println("Reported By (ID)   : " + target.getReporterID());
            
            System.out.println("Reported By (Name) : " + (reporter != null ? reporter.getName() : "Unknown Student"));
            System.out.println("Report Type        : " + target.getIssueType());
            System.out.println("Description        : " + target.getDescription());
            String rawDate = target.getReportDate();
            if (rawDate != null && rawDate.length() == 8) {
                String formatted = rawDate.substring(0,2) + "-" + rawDate.substring(2,4) + "-" + rawDate.substring(4);
                System.out.println("Report Date        : " + formatted);
            }
            System.out.println("Status             : " + target.getStatus());
            System.out.println("-----------------------------------------------------");
            System.out.println("Handled By Admin ID: " + (target.getAssignedTo().isEmpty() ? "Not Assigned" : target.getAssignedTo()));
            System.out.println("Admin Name         : " + (adminObj != null ? adminObj.getName() : "---"));
            String resDate = target.getResolvedDate();
            String displayResolvedDate;

            if (resDate == null || resDate.trim().isEmpty()) {
                displayResolvedDate = "Not Resolved Yet";
            } else if (resDate.length() == 8) {
                displayResolvedDate = resDate.substring(0, 2) + "-" + 
                                      resDate.substring(2, 4) + "-" + 
                                      resDate.substring(4);
            } else {
                displayResolvedDate = resDate;
            }

            System.out.println("Resolved Date      : " + displayResolvedDate);
            System.out.println("-----------------------------------------------------");

            System.out.println("Actions for " + target.getIssueID() + ":");
            System.out.println("[1] Mark In-Progress (Lock Room)");
            System.out.println("[2] Mark Resolved (Unlock Room)");
            System.out.println("[C] Cancel");
            System.out.print("Choice: ");
            String action = sc.nextLine().trim().toUpperCase();

            if (action.equals("C")) return;

            target.setAssignedTo(currentAdmin.getId()); 

            if (action.equals("1")) {
                target.setStatus(Constants.MAINT_IN_PROGRESS);
                facilitiesService.updateFacilityStatus(target.getFacilityID(), Constants.FACILITY_MAINTENANCE);
                FileManager.saveAllMaintenanceReports(allReports);
                System.out.println(">> Status: IN-PROGRESS. Room is now LOCKED.");

            } else if (action.equals("2")) {
                target.setStatus(Constants.MAINT_RESOLVED);
                target.setResolvedDate(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy")));
                facilitiesService.updateFacilityStatus(target.getFacilityID(), Constants.FACILITY_AVAILABLE);
                FileManager.saveAllMaintenanceReports(allReports);
                System.out.println(">> Status: RESOLVED. Room is now OPEN.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println(">> Please enter a valid number.");
        }
    }
    
    private void viewMaintenanceHistoryPaged(List<MaintenanceReport> allReports) {
        int pageSize = 10;
        int totalReports = allReports.size();
        int totalPages = (int) Math.ceil((double) totalReports / pageSize);
        int currentPage = 0;

        if (allReports.isEmpty()) {
            System.out.println("\n>> No history records found.");
            return;
        }

        List<Facility> facilities = FileManager.loadAllFacilities();
        List<User> users = FileManager.loadAllUsers(); 
        
        while (true) {
            System.out.println("\n=========================== MAINTENANCE HISTORY ============================");
            System.out.println("Page " + (currentPage + 1) + " of " + totalPages);
            
            System.out.printf("%-4s | %-10s | %-18s | %-12s | %-12s\n", 
                "No.", "Room ID", "Facility Type", "Status", "Handled By");
            System.out.println("-".repeat(78));

            int start = currentPage * pageSize;
            int end = Math.min(start + pageSize, totalReports);

            for (int i = start; i < end; i++) {
                MaintenanceReport r = allReports.get(i);
                
                Facility f = facilities.stream()
                    .filter(fac -> fac.getFacilityID().equals(r.getFacilityID()))
                    .findFirst().orElse(null);
                
                String roomDisplay = (f != null) ? f.getRoomNo() : r.getFacilityID();
                String typeDisplay = (f != null) ? f.getType() : "Unknown";

                System.out.printf("%-4d | %-10s | %-18s | %-12s | %-12s\n", 
                    (i + 1), 
                    roomDisplay, 
                    typeDisplay, 
                    r.getStatus(), 
                    (r.getAssignedTo().isEmpty() ? "---" : r.getAssignedTo())
                );
            }

            System.out.println("-".repeat(78));
            System.out.println("[N] Next Page | [P] Previous Page | [BACK] Return to Menu");
            System.out.print("Enter No. to view details or Navigation: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (input.equals("BACK")) break;
            
            if (input.equals("N")) {
                if (currentPage < totalPages - 1) currentPage++;
                else System.out.println(">> You are already on the last page.");
                continue;
            }
            
            if (input.equals("P")) {
                if (currentPage > 0) currentPage--;
                else System.out.println(">> You are already on the first page.");
                continue;
            }

            if (my.edu.utar.util.Validator.isNumeric(input)) {
                int selectedNo = Integer.parseInt(input);
                if (selectedNo >= 1 && selectedNo <= totalReports) {
                    showMaintenanceDetails(allReports.get(selectedNo - 1), facilities, users);
                } else {
                    System.out.println(">> Invalid number. Please select a number from the list.");
                }
            } else {
                System.out.println(">> Invalid input! Use [N], [P], [BACK], or a record number.");
            }
        }
    }
    
    private void showMaintenanceDetails(MaintenanceReport r, List<Facility> facilities, List<User> allUsers) {
        Facility f = facilities.stream()
            .filter(fac -> fac.getFacilityID().equalsIgnoreCase(r.getFacilityID()))
            .findFirst().orElse(null);

        User reporter = allUsers.stream()
            .filter(u -> u.getId().equals(r.getReporterID()))
            .findFirst().orElse(null);

        List<Admin> admins = FileManager.loadAllAdmins();
        Admin adminObj = admins.stream()
                .filter(a -> a.getId().equalsIgnoreCase(r.getAssignedTo()))
                .findFirst().orElse(null);

        System.out.println("\n-----------------------------------------------------");
        System.out.println("            MAINTENANCE REPORT DETAILS               ");
        System.out.println("-----------------------------------------------------");
        System.out.println("Maintenance ID     : " + r.getIssueID());
        System.out.println("Room ID            : " + (f != null ? f.getRoomNo() : r.getFacilityID()));
        System.out.println("Room Name          : " + (f != null ? f.getName() : "N/A"));
        System.out.println("Facility Type      : " + (f != null ? f.getType() : "N/A"));
        System.out.println("-----------------------------------------------------");
        System.out.println("Reported By (ID)   : " + r.getReporterID());
        
        System.out.println("Reported By (Name) : " + (reporter != null ? reporter.getName() : "Unknown Student"));
        
        System.out.println("Report Type        : " + r.getIssueType());
        System.out.println("Description        : " + r.getDescription());
        String rawDate = r.getReportDate();
        if (rawDate != null && rawDate.length() == 8) {
            String formatted = rawDate.substring(0,2) + "-" + rawDate.substring(2,4) + "-" + rawDate.substring(4);
            System.out.println("Report Date        : " + formatted);
        }
        System.out.println("Status             : " + r.getStatus());
        System.out.println("-----------------------------------------------------");
        System.out.println("Handled By Admin ID: " + (r.getAssignedTo().isEmpty() ? "Not Assigned" : r.getAssignedTo()));
        System.out.println("Admin Name         : " + (adminObj != null ? adminObj.getName() : "---"));
        String resDate = r.getResolvedDate();
        String displayResolvedDate;

        if (resDate == null || resDate.trim().isEmpty()) {
            displayResolvedDate = "Not Resolved Yet";
        } else if (resDate.length() == 8) {
            displayResolvedDate = resDate.substring(0, 2) + "-" + 
                                  resDate.substring(2, 4) + "-" + 
                                  resDate.substring(4);
        } else {
            displayResolvedDate = resDate;
        }

        System.out.println("Resolved Date      : " + displayResolvedDate);
        
        System.out.println("-----------------------------------------------------");
        System.out.println("Press Enter to return to the list...");
        sc.nextLine();
    }

    private void manageUsers() {
        while (true) {
            System.out.println("\n========== MANAGE USERS ==========");
            System.out.println("[1] Student");
            System.out.println("[2] Staff");
            System.out.println("[B] Back to Admin Dashboard");
            System.out.print("Select role: ");
            String roleChoice = sc.nextLine().trim().toUpperCase();

            if (roleChoice.equals("B")) {
                return; 
            }

            String tempRole = ""; 
            if (roleChoice.equals("1")) tempRole = Constants.ROLE_STUDENT;
            else if (roleChoice.equals("2")) tempRole = Constants.ROLE_STAFF;
            else {
                System.out.println("Invalid selection. Please try again.");
                continue; 
            }

            final String filterRole = tempRole; 

            List<User> filteredList = FileManager.loadAllUsers().stream() 
                    .filter(u -> u.getRole().equalsIgnoreCase(filterRole))
                    .collect(java.util.stream.Collectors.toList());

            if (filteredList.isEmpty()) {
                System.out.println("No " + filterRole + "s found in the system.");
                continue;
            }

            int pageSize = 10;
            int currentPage = 0;

            while (true) {
                int totalPages = (int) Math.ceil((double) filteredList.size() / pageSize);
                if (totalPages == 0) totalPages = 1; 
                
                int start = currentPage * pageSize;
                int end = Math.min(start + pageSize, filteredList.size());

                System.out.println("\n--- " + filterRole + " LIST (Page " + (currentPage + 1) + " of " + totalPages + ") ---");
                System.out.printf("%-4s | %-12s | %-25s\n", "No.", "User ID", "Name");
                System.out.println("-".repeat(45));

                for (int i = start; i < end; i++) {
                    User u = filteredList.get(i);
                    System.out.printf("%-4d | %-12s | %-25s\n", (i + 1), u.getId(), u.getName());
                }

                System.out.println("\n[N] Next | [P] Previous | [B] Back to Role Selection");
                System.out.print("Enter No., User ID, or Action: ");
                String input = sc.nextLine().trim();

                if (input.equalsIgnoreCase("B")) {
                    break; 
                }
                
                if (input.equalsIgnoreCase("N")) {
                    if (currentPage < totalPages - 1) currentPage++;
                    else System.out.println(">> You are already on the last page.");
                    continue;
                }
                
                if (input.equalsIgnoreCase("P")) {
                    if (currentPage > 0) currentPage--;
                    else System.out.println(">> You are already on the first page.");
                    continue;
                }

                User targetUser = null;
                try {
                    int index = Integer.parseInt(input) - 1;
                    if (index >= start && index < end) {
                        targetUser = filteredList.get(index);
                    } else {
                        System.out.println(">> Number out of range for this page.");
                    }
                } catch (NumberFormatException e) {
                    targetUser = filteredList.stream()
                            .filter(u -> u.getId().equalsIgnoreCase(input))
                            .findFirst()
                            .orElse(null);
                }

                if (targetUser != null) {
                    showUserManagementDetail(targetUser);
                    List<User> updatedAll = FileManager.loadAllUsers();
                    filteredList = updatedAll.stream() 
                            .filter(u -> u.getRole().equalsIgnoreCase(filterRole))
                            .collect(java.util.stream.Collectors.toList());
                } else if (!input.isEmpty()) {
                    System.out.println(">> User ID or Number not found.");
                }
            }
        }
    }
    
    private void showUserManagementDetail(User targetUser) {
        while (true) {
            System.out.println("\n--------------------------------------------");
            System.out.println("            USER PROFILE: " + targetUser.getId());
            System.out.println("--------------------------------------------");
            System.out.println("Name      : " + targetUser.getName());
            System.out.println("Role      : " + targetUser.getRole());
            System.out.println("Phone     : " + targetUser.getPhone());
            System.out.println("Faculty   : " + targetUser.getFaculty());
            System.out.println("--------------------------------------------");
            System.out.println("[1] Update Info");
            System.out.println("[2] Delete User");
            System.out.println("[3] View User Booking Analytics & Reliability");
            System.out.println("[B] Back to List");
            System.out.print("Action: ");
            String action = sc.nextLine().trim().toUpperCase();

            if (action.equals("B")) break;
            
            if (action.equals("1")) {
                adminUpdateUser(targetUser);
            } else if (action.equals("2")) {
                adminDeleteUser(targetUser);
                break; 
            }
            else if (action.equals("3")) {
                ReportGenerator rg = new ReportGenerator();
                rg.generateUserReport(targetUser.getId(), bookingManager.getBookingList());
                System.out.println("\nPress Enter to return to profile...");
                sc.nextLine();
            }
        }
    }
    
    private void adminUpdateUser(User user) {
        while (true) {
            System.out.println("\n--- Updating User: " + user.getId() + " ---");
            System.out.println("[1] Edit Name  (Current: " + user.getName() + ")");
            System.out.println("[2] Edit Phone (Current: " + user.getPhone() + ")");
            System.out.println("[3] Reset Password");
            System.out.println("[B] Finish & Back");
            System.out.print("Select field to update: ");
            String choice = sc.nextLine().trim().toUpperCase();

            if (choice.equals("B")) break;

            switch (choice) {
                case "1":
                    System.out.print("Enter new Name: ");
                    String newName = sc.nextLine().trim();
                    if (Validator.isValidName(newName)) {
                        user.setName(newName);
                        FileManager.updateUser(user);
                        System.out.println(">> Success: Name updated.");
                    } else {
                        System.out.println(">> Error: Invalid Name format.");
                    }
                    break;

                case "2":
                    System.out.print("Enter new Phone (e.g., 0123456789): ");
                    String newPhone = sc.nextLine().trim();
                    if (Validator.isValidPhone(newPhone)) {
                        user.setPhone(newPhone);
                        FileManager.updateUser(user);
                        System.out.println(">> Success: Phone updated.");
                    } else {
                        System.out.println(">> Error: Invalid Phone format (Must start with 01 and be 10-11 digits).");
                    }
                    break;

                case "3":
                    System.out.print("Enter new Password (min 8 chars): ");
                    String newPass = sc.nextLine().trim();
                    if (Validator.isValidPassword(newPass)) {
                        user.setPassword(newPass);
                        FileManager.updateUser(user);
                        System.out.println(">> Success: Password reset.");
                    } else {
                        System.out.println(">> Error: Password too short (Minimum 8 characters).");
                    }
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    private void adminDeleteUser(User user) {
        if (bookingManager.hasFutureBookings(user.getId())) {
            System.out.println("FAILED: Cannot delete user. User has active/future bookings.");
            return;
        }

        System.out.print("Are you SURE you want to delete " + user.getName() + "? [YES / NO]: ");
        if (sc.nextLine().trim().equalsIgnoreCase("YES")) {
            FileManager.deleteUser(user.getId()); 
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void issueAlert() {
    	List<my.edu.utar.model.MaintenanceReport> allReports = my.edu.utar.data.FileManager.loadAllMaintenanceReports();
        List<my.edu.utar.model.Booking> allBookings = bookingManager.getBookingList(); 

        long pendingMaint = allReports.stream()
            .filter(r -> r.getStatus().equalsIgnoreCase(my.edu.utar.util.Constants.STATUS_PENDING))
            .count();
            
        long inProgressMaint = allReports.stream()
            .filter(r -> r.getStatus().equalsIgnoreCase(my.edu.utar.util.Constants.MAINT_IN_PROGRESS))
            .count();

        long pendingBookings = allBookings.stream()
            .filter(b -> b.getStatus().equalsIgnoreCase("Pending"))
            .count();

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                    ADMIN SYSTEM ALERTS                   ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");

        if (pendingMaint == 0 && pendingBookings == 0 && inProgressMaint == 0) {
            System.out.println("║  [/] SYSTEM STATUS: HEALTHY                              ║");
            System.out.println("║      No urgent tasks or pending requests found.          ║");
        } else {
            System.out.println("║  [!] ATTENTION: ACTION REQUIRED                          ║");
            System.out.println("║                                                          ║");
            
            if (pendingMaint > 0) {
                System.out.printf("║  - %-2d NEW maintenance reports need assignment            ║\n", pendingMaint);
            }
            
            if (pendingBookings > 0) {
                System.out.printf("║  - %-2d Student booking requests are PENDING approval      ║\n", pendingBookings);
            }
            
            if (inProgressMaint > 0) {
                System.out.printf("║  - %-2d Maintenance tasks currently IN-PROGRESS            ║\n", inProgressMaint);
            }

            System.out.println("║                                                          ║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            System.out.println("║  QUICK ACTIONS:                                          ║");
            System.out.println("║  > To handle Bookings, select Menu [3]                   ║");
            System.out.println("║  > To handle Maintenance, select Menu [6]                ║");
        }
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        System.out.println("\nPress [Enter] to return to Dashboard...");
        sc.nextLine();
    }
}
