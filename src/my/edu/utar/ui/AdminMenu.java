
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
import java.time.LocalDate;

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
        }
    }

    // --- HELPER: CANCELLATION MESSAGE ---
    private void handleCancellation() {
        System.out.println("\n>> Action Cancelled. Returning to Main Menu...");
        System.out.println("Press Enter to continue...");
        new Scanner(System.in).nextLine();
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
    	Scanner input = new Scanner (System.in);
    	while (true) {
    		System.out.println("Facilities Management Page");
            System.out.println("[1] View all current facilities");
            System.out.println("[2] Add new facilities");
            System.out.println("[3] Remove unused facilities");
            System.out.println("[4] Edit current facilities details");
            System.out.println("[0] Return to Main Menu");
            System.out.println("Please enter your choice: ");
            String choice = sc.nextLine().trim().toUpperCase();
            
            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }
            
            if (choice.equals("0")) {
            	return;
            }
            
            switch (choice) {
            case "1" :
            	List<Facility> all = facilityService.getAllFacilities();
                System.out.println("Current facilities List");
                for (Facility f : all) {
                	System.out.println(f.toFileString());
                }
                break;

            case "2":
                System.out.println("\n--- Add New Facility ---");

                List<Facility> allCurrent = facilityService.getAllFacilities();
                int nextIdNum = allCurrent.size() + 1;
                String ID = String.format("F%03d", nextIdNum);
                
                while (FileManager.isFacilityIdExists(ID)) {
                    nextIdNum++;
                    ID = String.format("F%03d", nextIdNum);
                }
                System.out.println("Generated Facility ID: " + ID);

                String block;
                while (true) {
                    System.out.print("Enter block name (KA / KB): ");
                    block = sc.nextLine().trim().toUpperCase();
                    if (block.equals("KA") || block.equals("KB")) break;
                    System.out.println("Invalid Block. Only KA or KB allowed.");
                }

                String floor;
                while (true) {
                    System.out.print("Enter floor (KA: 1-8, M, G, SB | KB: 1-10, G, SB): ");
                    floor = sc.nextLine().trim().toUpperCase();
                    boolean isValid = false;

                    if (block.equals("KA")) {
                        if (floor.equals("M") || floor.equals("G") || floor.equals("SB")) isValid = true;
                        else if (Validator.isNumeric(floor)) {
                            int fNum = Integer.parseInt(floor);
                            if (fNum >= 1 && fNum <= 8) isValid = true;
                        }
                    } else {
                        if (floor.equals("G") || floor.equals("SB")) isValid = true;
                        else if (Validator.isNumeric(floor)) {
                            int fNum = Integer.parseInt(floor);
                            if (fNum >= 1 && fNum <= 10) isValid = true;
                        }
                    }

                    if (isValid) break;
                    System.out.println("Invalid floor for Block " + block);
                }

                String roomNo;
                while (true) {
                    System.out.print("Enter Room Number (Must start with " + block + "): ");
                    roomNo = sc.nextLine().trim().toUpperCase();
                    
                    if (!roomNo.startsWith(block)) {
                        System.out.println("Error: Room number must start with the block name " + block);
                        continue;
                    }

                    boolean exists = false;
                    for (Facility existing : allCurrent) {
                        if (existing.getRoomNo().equalsIgnoreCase(roomNo)) {
                            exists = true;
                            break;
                        }
                    }
                    
                    if (exists) {
                        System.out.println("Error: Room number " + roomNo + " already exists in the system.");
                    } else {
                        break;
                    }
                }

                System.out.print("Enter Facility Name: ");
                String description = sc.nextLine().trim();

                String type = "";
                while (true) {
                    System.out.println("Select Facility Type:");
                    for (int i = 0; i < Constants.FACILITY_TYPES.length; i++) {
                        System.out.println("[" + (i + 1) + "] " + Constants.FACILITY_TYPES[i]);
                    }
                    System.out.print("Choice: ");
                    String tChoice = sc.nextLine().trim();
                    if (Validator.isValidMenuChoice(tChoice, 1, Constants.FACILITY_TYPES.length)) {
                        type = Constants.FACILITY_TYPES[Integer.parseInt(tChoice) - 1];
                        break;
                    }
                    System.out.println("Invalid choice.");
                }

                int capacity = 0;
                while (true) {
                    System.out.print("Enter capacity (0-300): ");
                    String capInput = sc.nextLine().trim();
                    if (Validator.isNumeric(capInput)) {
                        capacity = Integer.parseInt(capInput);
                        if (capacity >= 0 && capacity <= 300) break;
                    }
                    System.out.println("Invalid capacity. Please enter a number between 0 and 300.");
                }

                // 8. FINAL CREATION
                String status = Constants.FACILITY_AVAILABLE;
                Facility f = new Facility(ID, block, floor, roomNo, description, type, capacity, status);
                
                String error = facilityService.addFacility(f);
                if (error == null) {
                    System.out.println("\nSUCCESS: Facility " + roomNo + " added with ID " + ID);
                } else {
                    System.out.println("Error: " + error);
                }
                break;
            	
            case "3" :
            	System.out.println("------Facilities Removal Page------");
            	System.out.println("Enter the facility ID that you would like to remove:");
            	String theremovingID = input.nextLine().trim();
            	facilityService.removeFacility(theremovingID);
                System.out.println("The Facility list has been updated.");
                break;
            	
            case "4" :
            	System.out.println("------Edit Facilities Details Page------");
            	System.out.println("Enter the facility ID that you would like to remove:");
            	String theeditingID = input.nextLine().trim();
            	
            	List<Facility> allF = facilityService.getAllFacilities();
            	Facility target = null;
            	
            	for(Facility f1 : allF) {
            		if(f1.getFacilityID().equalsIgnoreCase(theeditingID)) {
            			target = f1;
            			break;
            		}
            	}
            	
            	if(target == null) {
            		System.out.println("The facility ID is not existed.");
            		return;
            	} else {
            		System.out.println("------Current Details of the Facility ID------");
                	target.display();
                  	System.out.println("What would you like to edit?");
                	System.out.println("[1] Block name");
                	System.out.println("[2] Floor number");
                	System.out.println("[3] Room number");
                	System.out.println("[4] Facility type");
                	System.out.println("[5] Faciltiy capacity");
                	System.out.println("[6] Facility status");
                	System.out.print("Choice: ");
                	int Choice = Integer.parseInt(input.nextLine());
                    
                    switch(Choice) {
                    case 1:
                    	System.out.println("Enter new block name:(e.g. KB) ");
                    	target.setBlock(input.nextLine());
                    	break;
                    case 2:
                    	System.out.println("Enter new floor number:(e.g. 1) ");
                    	target.setFloor(input.nextLine());
                    	break;
                    case 3:
                    	System.out.println("Enter new room number:(KB104) ");
                    	target.setRoomNo(input.nextLine());
                    	break;
                    case 4:
                    	System.out.println("Enter new facility type:(lecture hall) ");
                    	target.setType(input.nextLine());
                    	break;
                    case 5:
                    	System.out.println("Enter new facility capacity: (e.g. 80) ");
                    	target.setCapacity(Integer.parseInt(input.nextLine()));
                    	break;
                    case 6:
                    	System.out.println("Enter new facility status: (e.g. active) ");
                    	target.setStatus(input.nextLine());
                    	break;
                    default:
                    	System.out.println("Only choice 1 to 6 make changes. Please try again.");
                    	break;
                    }
                    
                    if (facilityService.updateFacilities(allF)) {
                        System.out.println("Successfully Edited!");
                    } else {
                        System.out.println("Error on saving edited file.");
                    }
                    	
            	}
                break;
                
            default:
                System.out.println("Invalid selection.");
                break;
            }
    	}
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
