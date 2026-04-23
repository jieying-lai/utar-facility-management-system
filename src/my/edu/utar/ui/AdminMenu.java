
package my.edu.utar.ui;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
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

public class AdminMenu{

    private Scanner sc;
    private Admin currentAdmin;
    
    private FacilitiesService facilityService = new FacilitiesService();
    private BookingManager bookingManager = new BookingManager();

    public AdminMenu(Scanner sc, Admin currentAdmin) {
        this.sc = sc;
        this.currentAdmin = currentAdmin;
    }

    public void show() {
        while (true) {
            showIssueAlerts();  
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
                case "3": approval();    
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
        String currentDate = now.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
        
        System.out.println("============================================");
        System.out.println("    UTAR Smart Campus Management System     ");
        System.out.println("               Admin Dashboard               ");
        System.out.println("============================================");

        System.out.println("  Date : " + currentDate);
        System.out.println("  Time : " + currentTime);
        
        System.out.println("\nWelcome, " + currentAdmin.getName());
        System.out.println("  Role: Admin" );
        System.out.println("============================================");
        System.out.println("[1] Search Facility Status");
        System.out.println("[2] Manage Facilities");
        System.out.println("[3] Process Booking Requests");
        System.out.println("[4] Facility Usage Tracking");
        System.out.println("[5] View Summary Report");
        System.out.println("[6] Maintenance Management");
        System.out.println("[7] View Issue Alerts");
        System.out.println("[8] Manage Users");
        System.out.println("[L] Logout");
        System.out.println("--------------------------------------------");
        System.out.print("Enter your choice: ");

    }

    private void showIssueAlerts() {
        // TODO Member 3: implement alert check here
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
            sc.close();
            return;
        }

        while (step <= 4) {
            switch (step) {
                case 1: // 1. Select Block
                    List<String> blocks = allFacilities.stream()
                            .map(Facility::getBlock).distinct().collect(Collectors.toList());
                    block = selectWithBack(blocks, "Block");
                    if (block == null) { handleCancellation(); return; }
                    if (block.equals("BACK")) return;
                    step++;
                    break;

                case 2: // 2. Select Facility Type
                    final String b2 = block;
                    List<String> types = allFacilities.stream()
                            .filter(f -> f.getBlock().equalsIgnoreCase(b2))
                            .map(Facility::getType).distinct().collect(Collectors.toList());
                    type = selectWithBack(types, "Facility Type");
                    if (type == null) { handleCancellation(); return; }
                    if (type.equals("BACK")) { step--; break; }
                    step++;
                    break;

                case 3: // 3. Select Date
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

                case 4: // 4. Display Matrix Results
                    displayScheduleTable(block, type, formattedDate, allFacilities);
                    
                    // --- LOOP FOR VALID INPUT HANDLING ---
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
                            // Stays in this while loop to ask again
                        }
                    }
                    break;
            }
            sc.close();
        }
    }

    // --- HELPER: CANCELLATION MESSAGE ---
    private void handleCancellation() {
        System.out.println("\n>> Action Cancelled. Returning to Main Menu...");
        System.out.println("Press Enter to continue...");
        
        this.sc.nextLine(); 
    }
    // --- HELPER: TABLE DRAWING (To keep code clean) ---
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
        // DECLARE VARIABLES HERE for scope
        String block = "";
        String type = "";
        String floor = "";
        int step = 1;
        Facility selectedFacility = null;

        while (true) {
            // CRITICAL FIX: Refresh data from the file at the start of every loop
            // This ensures the program "sees" new additions or deletions immediately
            List<Facility> allFacilities = facilityService.getAllFacilities();

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
                case "1": { // PAGINATION VIEW
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

                case "2": { // ADD NEW FACILITY
                    System.out.println("\n============================================");
                    System.out.println("          ADD NEW CAMPUS FACILITY           ");
                    System.out.println("============================================");

                    // Auto-ID Generation based on current list
                    int nextIdNum = 1;
                    for (Facility existing : allFacilities) {
                        try {
                            int currentIdNum = Integer.parseInt(existing.getFacilityID().substring(1));
                            if (currentIdNum >= nextIdNum) nextIdNum = currentIdNum + 1;
                        } catch (Exception e) { }
                    }
                    String ID = String.format("F%03d", nextIdNum);
                    System.out.println("Generated Facility ID: " + ID);

                    // Select Block
                    while (true) {
                        System.out.print("Select Block [1] KA [2] KB: ");
                        String bChoice = sc.nextLine().trim();
                        if (bChoice.equals("1")) { block = "KA"; break; }
                        if (bChoice.equals("2")) { block = "KB"; break; }
                        System.out.println(">> Invalid choice.");
                    }

                    // Select Floor
                    String[] kaFloors = {"SB", "G", "M", "1", "2", "3", "4", "5", "6", "7", "8"};
                    String[] kbFloors = {"SB", "G", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                    String[] targetFloors = block.equals("KA") ? kaFloors : kbFloors;

                    while (true) {
                        System.out.println("Available Floors: " + Arrays.toString(targetFloors));
                        System.out.print("Select Floor: ");
                        floor = sc.nextLine().trim().toUpperCase();
                        final String fSearch = floor;
                        if (Arrays.stream(targetFloors).anyMatch(f -> f.equals(fSearch))) break;
                        System.out.println(">> Invalid Floor.");
                    }

                    // Room Number & Duplicate Check
                    String roomNo;
                    while (true) {
                        System.out.print("Enter Facility Number (Start with " + block + "): ");
                        roomNo = sc.nextLine().trim().toUpperCase();
                        final String rSearch = roomNo;
                        if (roomNo.startsWith(block) && allFacilities.stream().noneMatch(f -> f.getRoomNo().equals(rSearch))) break;
                        System.out.println(">> Invalid or Duplicate Room Number.");
                    }

                    System.out.print("Enter Facility Name: ");
                    String description = sc.nextLine().trim();

                    // Select Type
                    while (true) {
                        System.out.println("\nSelect Facility Type:");
                        for (int i = 0; i < Constants.FACILITY_TYPES.length; i++) {
                            // %-2d  : Two digits, left-aligned
                            // %-20s : String, 20 characters wide, left-aligned
                            System.out.printf("[%d] %-20s ", (i + 1), Constants.FACILITY_TYPES[i]);
                            
                            // Print a newline every 2 items to create 2 columns
                            if ((i + 1) % 2 == 0) {
                                System.out.println();
                            }
                        }
                        
                        // Add a newline if the total number of items was odd
                        if (Constants.FACILITY_TYPES.length % 2 != 0) {
                            System.out.println();
                        }

                        System.out.print("Choice: ");
                        String tChoice = sc.nextLine().trim();
                        if (Validator.isValidMenuChoice(tChoice, 1, Constants.FACILITY_TYPES.length)) {
                            type = Constants.FACILITY_TYPES[Integer.parseInt(tChoice) - 1];
                            break;
                        }
                        System.out.println(">> Invalid choice. Please try again.");
                    }

                    System.out.print("Enter Capacity (1-300): ");
                    int capacity = Integer.parseInt(sc.nextLine().trim());

                    Facility newFacility = new Facility(ID, block, floor, roomNo, description, type, capacity, Constants.FACILITY_AVAILABLE);
                    
                    if (facilityService.addFacility(newFacility) == null) {
                        System.out.println("\n============================================");
                        System.out.println("        FACILITY SUCCESSFULLY ADDED         ");
                        System.out.println("============================================");
                        System.out.printf("  %-15s : %s%n", "Facility ID", newFacility.getFacilityID());
                        System.out.printf("  %-15s : %s%n", "Room Number", newFacility.getRoomNo());
                        System.out.printf("  %-15s : %s%n", "Name", newFacility.getName());
                        System.out.printf("  %-15s : %s%n", "Type", newFacility.getType());
                        System.out.printf("  %-15s : %s%n", "Location", newFacility.getBlock() + " - Level " + newFacility.getFloor());
                        System.out.printf("  %-15s : %d%n", "Capacity", newFacility.getCapacity());
                        System.out.printf("  %-15s : %s%n", "Status", newFacility.getStatus());
                        System.out.println("============================================");
                        
                        System.out.println("\nPress Enter to continue...");
                        sc.nextLine();
                    } else {
                        System.out.println("\n[!] Error: Could not save the facility. Please check the data logs.");
                    }
                    break;
                }

                case "3": { // MODIFY / DELETE
                    step = 1;
                    boolean inModifyMode = true;
                    while (inModifyMode) {
                        switch (step) {
                            case 1: // Select Block
                                List<String> blocks = allFacilities.stream().map(Facility::getBlock).distinct().sorted().collect(Collectors.toList());
                                block = selectWithBack(blocks, "Block");
                                if (block == null || block.equals("BACK")) { inModifyMode = false; break; }
                                step++; break;

                            case 2: // Select Floor
                                final String b2 = block;
                                List<String> floors = allFacilities.stream().filter(f -> f.getBlock().equals(b2)).map(Facility::getFloor).distinct().sorted().collect(Collectors.toList());
                                floor = selectWithBack(floors, "Floor");
                                if (floor == null) { inModifyMode = false; break; }
                                if (floor.equals("BACK")) { step--; break; }
                                step++; break;

                            case 3: // Select Type
                                final String b3 = block, f3 = floor;
                                List<String> types = allFacilities.stream().filter(f -> f.getBlock().equals(b3) && f.getFloor().equals(f3)).map(Facility::getType).distinct().sorted().collect(Collectors.toList());
                                type = selectWithBack(types, "Facility Type");
                                if (type == null) { inModifyMode = false; break; }
                                if (type.equals("BACK")) { step--; break; }
                                step++; break;

                            case 4: // Select Specific Facility
                                final String b4 = block, f4 = floor, t4 = type;
                                // Filter the list based on previous selections
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
                                // Table Header for alignment
                                System.out.printf("%-5s | %-12s | %-20s\n", "No.", "Room No", "Facility Name");
                                System.out.println("----------------------------------------------");

                                for (int i = 0; i < filtered.size(); i++) {
                                    Facility f = filtered.get(i);
                                    // Using %-12s and %-20s ensures columns stay perfectly straight
                                    System.out.printf("[%d]   | %-12s | %-20s\n", (i + 1), f.getRoomNo(), f.getName());
                                }
                                System.out.println("----------------------------------------------");
                                System.out.print("Select Number [B: Back, C: Cancel]: ");
                                String sel = sc.nextLine().trim().toUpperCase();

                                // 1. Check for Navigation
                                if (sel.equals("C")) { inModifyMode = false; break; }
                                if (sel.equals("B")) { step--; break; }

                                // 2. Validate Numeric Input
                                if (Validator.isValidMenuChoice(sel, 1, filtered.size())) {
                                    selectedFacility = filtered.get(Integer.parseInt(sel) - 1);
                                    step++; // Move to Case 5 (Action)
                                } else {
                                    System.out.println(">> Error: Invalid selection. Please choose a number from the list.");
                                    // We don't increment step, so it stays on Case 4 and shows the list again
                                }
                                break;

                            case 5: // Action: Modify or Delete
                                System.out.println("\n==============================================");
                                System.out.println("           FACILITY ACTION MENU               ");
                                System.out.println("==============================================");
                                // Show current details before action
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
                                    // performModification usually returns boolean or the updated object
                                    performModification(selectedFacility, allFacilities);
                                    
                                    // Show updated details as a receipt
                                    System.out.println("\n>> FACILITY UPDATED SUCCESSFULLY!");
                                    System.out.println("==============================================");
                                    selectedFacility.display(); 
                                    System.out.println("==============================================");
                                    System.out.println("Press Enter to continue...");
                                    sc.nextLine();
                                    inModifyMode = false; 
                                } 
                                else if (action.equals("2")) {
                                    // Store ID before it gets deleted for the final message
                                    String deletedID = selectedFacility.getFacilityID();
                                    
                                    performDeletion(selectedFacility, allFacilities);
                                    
                                    System.out.println("\n>> SUCCESS: Facility [" + deletedID + "] has been removed.");
                                    System.out.println("Press Enter to continue...");
                                    sc.nextLine();
                                    inModifyMode = false;
                                } 
                                else {
                                    // The "Try Again" logic for invalid input
                                    System.out.println("\n>> [!] Invalid action. Please enter '1', '2', 'B', or 'C'.");
                                    System.out.println(">> Please try again.");
                                    // We do NOT change step or inModifyMode, so the loop repeats this case
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
        // 1. Check Booking Dependency
        boolean isBooked = FileManager.isFacilityInBookings(target.getFacilityID());
        if (isBooked) {
            System.out.println("\n[!] WARNING: This facility (ID: " + target.getFacilityID() + ") has existing bookings.");
            System.out.print("Modifying details may cause data inconsistency. Modify anyway? (Y/N): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("Y")) return;
        }

        // Keep a backup of the original state in case they select "Cancel"
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
                // Restore original values
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
                case "1": // Block Modification with Logic Check
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

                case "2": // Floor Selection Menu
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

                case "3": // Room Number with Block Logic
                    System.out.print("Enter New Room No: ");
                    String newRoom = sc.nextLine().trim().toUpperCase();
                    if (!newRoom.startsWith(target.getBlock())) {
                        System.out.println("\n[!] WARNING: New Room Number " + newRoom + " does not start with current Block " + target.getBlock() + ".");
                        System.out.print("Proceed anyway? (Y/N): ");
                        if (!sc.nextLine().trim().equalsIgnoreCase("Y")) break;
                    }
                    target.setRoomNo(newRoom);
                    break;

                case "4": // Facility Name Validation
                    System.out.print("Enter New Facility Name: ");
                    String newName = sc.nextLine().trim();
                    if (!Validator.isEmpty(newName)) {
                        target.setName(newName);
                    } else {
                        System.out.println(">> [!] Name cannot be empty.");
                    }
                    break;

                case "5": // Facility Type Selection
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

                case "6": // Capacity Validation
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

        // FINAL SAVE to text file
        if (facilityService.updateFacilities(allF)) {
            System.out.println("\n==============================================");
            System.out.println(">> UPDATE SUCCESSFUL: File data has been saved.");
            System.out.println("==============================================");
        } else {
            System.out.println("\n[!] SYSTEM ERROR: Failed to update the database file.");
        }
    }
        
    private void performDeletion(Facility target, List<Facility> allF) {
        // 1. Check for ACTIVE bookings only
        boolean hasActive = FileManager.hasActiveBookings(target.getFacilityID());
        
        if (hasActive) {
            System.out.println("\n[!] ERROR: Cannot delete facility " + target.getFacilityID());
            System.out.println(">> This facility has Active/Confirmed/Pending bookings.");
            System.out.println(">> Please resolve or cancel those bookings before deleting.");
            System.out.println("Press Enter to return...");
            sc.nextLine();
            return;
        }

        // 2. Show details of what is about to be deleted
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
            // Remove from the current list
            allF.remove(target);
            
            // Overwrite the file with the updated list
            if (facilityService.updateFacilities(allF)) {
                System.out.println("\n>> SUCCESS: Facility [" + target.getRoomNo() + "] removed permanently.");
            } else {
                System.out.println("\n>> [!] SYSTEM ERROR: Failed to update database file.");
            }
        } else {
            System.out.println("\n>> Deletion cancelled. Facility is safe.");
        }
        
        System.out.println("Press Enter to continue...");
        sc.nextLine();
    }
    
    private void approval() {
    	Scanner input = new Scanner(System.in);
        System.out.print("Enter Booking ID to process: ");
        String id = input.nextLine();

        System.out.println("(1) Approve or (2) Reject this booking?");
        System.out.print("Selection: ");
        String choice = input.nextLine();

        if (choice.equals("1")) {
            if (bookingManager.approveBooking(id)) {
                System.out.println("Booking approved successfully!");
            } else {
                System.out.println("Error: Booking ID not found or already processed.");
            }
        } else if (choice.equals("2")) {
            System.out.print("Enter reason for rejection: ");
            String reason = input.nextLine();
            
            if (bookingManager.rejectBooking(id, reason)) {
                System.out.println("Booking rejected. Reason recorded.");
            } else {
                System.out.println("Error: Failed to reject the booking.");
            }
        } else {
            System.out.println("Invalid selection.");
        }
        input.close();
    }
    
    private void facilityUsageTracking() {
        System.out.println("\n--- Facility Usage Tracking ---");
  
        List<Facility> allFacilities = facilityService.getAllFacilities();
        List<Booking> allBookings = bookingManager.getBookingList();

        for (Facility f : allFacilities) {
            int count = 0; 
                for (Booking booking : allBookings) {
                    if (booking.getFacilityID().equals(f.getFacilityID())) {
                        count++;
                    }
                }
                System.out.println("Facility: " + f.getFacilityID() + " | Total Usage: " + count);
        }
    }

    private void viewSummaryReport() {
        try {
            File facilityFile = new File("data/facilities.txt");
            File bookingFile = new File("data/bookings.txt");

            if (!facilityFile.exists() || !bookingFile.exists()) {
                System.out.println("Error: Required data files are missing.");
                return;
            }

            System.out.println("========== FACILITIES BOOKING SUMMARY REPORT ==========\" ");
            System.out.printf("%-6s | %-6s | %-6s | %-10s | %-15s | %-5s\n", 
                    "ID", "Block", "Floor", "Room", "Name", "Total Booking Number");
            System.out.println("---------------------------------------------------------------------------");

            Scanner facScanner = new Scanner(facilityFile);
            while (facScanner.hasNextLine()) {
                String facLine = facScanner.nextLine();
                String[] fParts = facLine.split("\\|");
                
                if (fParts.length >= 5) {
                    String ID = fParts[0];
                    String Block = fParts[1];
                    String floor = fParts[2];
                    String RoomNum = fParts[3];
                    String name = fParts[4];
                    
                    int count = 0;

                    Scanner bookScanner = new Scanner(bookingFile);
                    while (bookScanner.hasNextLine()) {
                        String bookLine = bookScanner.nextLine();
                        String[] bParts = bookLine.split("\\|");
                        
                        if (bParts.length > 2 && bParts[2].equals(ID)) {
                            count++;
                        }
                    }
                    bookScanner.close();

                    System.out.printf("%-6s | %-6s | %-6s | %-10s | %-15s | %-5d\n", 
                            ID, Block, floor, RoomNum, name, count);
                }
            }
            facScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
        
        System.out.println("======================================================\n");

    }

    private void manageUsers() {
        System.out.println("\n========== MANAGE USERS ==========");
        System.out.println("[1] Student");
        System.out.println("[2] Staff");
        System.out.println("[B] Back");
        System.out.print("Select role: ");
        String roleChoice = sc.nextLine().trim().toUpperCase();

        String filterRole = "";
        if (roleChoice.equals("1")) filterRole = Constants.ROLE_STUDENT;
        else if (roleChoice.equals("2")) filterRole = Constants.ROLE_STAFF;
        else return;

        System.out.print("Enter " + filterRole + " ID to search: ");
        String targetId = sc.nextLine().trim();
        
        User targetUser = FileManager.findUserById(targetId);
        if (targetUser == null || !targetUser.getRole().equalsIgnoreCase(filterRole)) {
            System.out.println("Error: User not found in " + filterRole + " category.");
            return;
        }

        System.out.println("\n--- User Details ---");
        System.out.println("ID        : " + targetUser.getId());
        System.out.println("Name      : " + targetUser.getName());
        System.out.println("Role      : " + targetUser.getRole());
        System.out.println("Phone     : " + targetUser.getPhone());
        System.out.println("Faculty   : " + targetUser.getFaculty());
        System.out.println("Password  : ******** (Hidden)");
        
        System.out.println("\n[1] Update User Info");
        System.out.println("[2] Delete User");
        System.out.println("[B] Back");
        System.out.print("Action: ");
        String action = sc.nextLine().trim().toUpperCase();

        if (action.equals("1")) {
            adminUpdateUser(targetUser);
        } else if (action.equals("2")) {
            adminDeleteUser(targetUser);
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

        System.out.print("Are you SURE you want to delete " + user.getName() + "? (YES/NO): ");
        if (sc.nextLine().trim().equalsIgnoreCase("YES")) {
            FileManager.deleteUser(user.getId()); 
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    /** TODO Member 3 */
    private void maintenanceManagement() {
        System.out.println("[TODO - Member 3] Maintenance Management");
    }

    /** TODO Member 3 */
    private void issueAlert() {
        System.out.println("[TODO - Member 3] Issue Alerts");
    }
}
