
package my.edu.utar.ui;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.util.Scanner;
import java.util.List;
import my.edu.utar.service.BookingManager;
import my.edu.utar.service.ReportGenerator;
import my.edu.utar.model.Booking;
import my.edu.utar.data.FileManager;
import my.edu.utar.model.Admin;
import my.edu.utar.model.User; 
import my.edu.utar.model.Facility;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;
import my.edu.utar.service.FacilitiesService;
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
        System.out.println("\n--- Search The Facility Status ---");
        System.out.println("Example Keyword:facilityID,block,floor,roomNo,type,capacity,status");
        System.out.println("Enter Search Keyword: ");
        String input = sc.nextLine().trim().toLowerCase();

        if(input.isEmpty()) {
        	return;
        }
        
        List<Facility> results = facilityService.searchFacilities(input);

        if (results.isEmpty()) {
            System.out.println("No matching facility found. Please Try Again.");
        } else {
        	System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n", 
                    "ID", "Block", "Floor", "Room", "Type", "Capacity", "Status");
            for (Facility f : results) {
                System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n", f.getFacilityID(), f.getBlock(), f.getFloor(), f.getRoomNo(),
                        f.getType(), f.getCapacity(), f.getStatus());
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
        System.out.print("Enter Booking ID to process: ");
        String id = sc.nextLine().trim();

        Booking b = bookingManager.findBooking(id); 
        
        if (b == null) {
            System.out.println("Error: Booking ID not found.");
            return;
        }
 
        if (!b.getStatus().equalsIgnoreCase("Pending")) {
            System.out.println("Error: This booking has already been processed (Status: " + b.getStatus() + ").");
            return;
        }
 
        System.out.println("\n--- Booking Details ---");
        b.display();
        System.out.println("-----------------------");

        System.out.println("Action: [A] Approve | [R] Reject | [S] Skip/Back");
        System.out.print("Enter choice: ");
        String action = sc.nextLine().trim().toUpperCase();

        if (action.equals("A")) {
            if (bookingManager.hasConflict(b)) {
                System.out.println("\nWARNING: Conflicting booking exists for [" + b.getFacilityID() + 
                                   "] on [" + b.getBookingDate() + "] at [Slot " + b.getTimeSlot() + "].");
                System.out.print("Do you still want to approve? [Y/N]: ");
                
                String confirm = sc.nextLine().trim().toUpperCase();
                if (!confirm.equals("Y")) {
                    System.out.println("Approval cancelled. Request remains Pending.");
                    return;
                }
            }

            if (bookingManager.approveBooking(id)) {
                System.out.println("Success: Booking " + id + " has been approved!");
            } else {
                System.out.println("Error: Critical failure during approval.");
            }

        } else if (action.equals("R")) {
            String reason = "";
            while (reason.isEmpty()) {
                System.out.print("Enter rejection reason (cannot be empty): ");
                reason = sc.nextLine().trim();
                if (reason.isEmpty()) {
                    System.out.println("Error: You must provide a reason for rejection.");
                }
            }
 
            if (bookingManager.rejectBooking(id, reason)) {
                System.out.println("Success: Booking " + id + " has been rejected.");
            } else {
                System.out.println("Error: Critical failure during rejection.");
            }

        } else if (action.equals("S")) {
            System.out.println("Skipped. Returning to Admin Menu.");
        } else {
            System.out.println("Invalid selection. Operation aborted.");
        }
    }
    
    private void facilityUsageTracking() {
        System.out.println("\n--- Facility Usage Tracking ---");
        
        List<Facility> allFacilities = facilityService.getAllFacilities();
        List<Booking> allBookings = bookingManager.getBookingList();

        for (Facility f : allFacilities) {
            int approvedCount = 0;
            int[] slotUsage = new int[5]; 

            for (Booking b : allBookings) { 
                if (b.getFacilityID().equals(f.getFacilityID()) && b.getStatus().equalsIgnoreCase("Approved")) {
                    approvedCount++;
                    
                    if (b.getTimeSlot() >= 0 && b.getTimeSlot() < 5) {
                        slotUsage[b.getTimeSlot()]++;
                    }
                }
            }

            int totalHours = approvedCount * 2;

            int peakSlotIndex = 0;
            for (int i = 1; i < slotUsage.length; i++) {
                if (slotUsage[i] > slotUsage[peakSlotIndex]) {
                    peakSlotIndex = i;
                }
            }
            
            String peakTime = (approvedCount > 0) ? my.edu.utar.util.Constants.TIME_SLOTS[peakSlotIndex] : "N/A";

            System.out.println("--------------------------------------------");
            System.out.println("Facility ID    : " + f.getFacilityID());
            System.out.println("Room Name      : " + f.getRoomNo());
            System.out.println("Total Bookings : " + approvedCount);
            System.out.println("Total Hours    : " + totalHours + " hours");
            System.out.println("Peak Time Slot : " + peakTime);
        }
    }
    
    private void viewSummaryReport() {
        System.out.println("\n--- GENERATE SUMMARY REPORT ---");
        
        System.out.print("Enter specific period to analyze (e.g., 2026-04): ");
        String period = sc.nextLine().trim();

        if (my.edu.utar.util.Validator.isEmpty(period)) {
            System.out.println("Period cannot be empty.");
            return;
        }

        List<Booking> bookingList = bookingManager.getBookingList();
        List<Facility> facilityList = facilityService.getAllFacilities();

        Booking[] bookingArray = bookingList.toArray(new Booking[0]);
        Facility[] facilityArray = facilityList.toArray(new Facility[0]);

        ReportGenerator rg = new ReportGenerator();

        System.out.println("\n============================================");
        System.out.println("   OFFICIAL ANALYTICS REPORT: " + period);
        System.out.println("============================================");

        rg.generateAdminSummaryReport(bookingArray, facilityArray, period); 

        System.out.println("============================================");
        System.out.println("Report Generation Complete.");
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
