
package my.edu.utar.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
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
        System.out.println("\n============================================");
        System.out.println("   ADMIN DASHBOARD");
        System.out.println("   " + currentAdmin.getName());
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
        while (true) {
            System.out.println("\n--- Facilities Management Page ---");
            System.out.println("[A] Add New Facility");
            System.out.println("[U] Update Facility Status");
            System.out.println("[D] Remove Facility");
            System.out.println("[V] View All Facilities");
            System.out.println("[0] Return to Main Menu");
            System.out.print("Please enter your choice: ");
            
            String choice = sc.nextLine().trim().toUpperCase();
            if (choice.equals("0")) return;

            switch (choice) {
                case "A": addFacility();
                		  break;
                case "U": updateStatus();
                		  break;
                case "D": removeFacility();
                		  break;
                case "V": viewAllFacilities();
                		  break;
                default:
                	System.out.println("Invalid selection.");
                	break;
            }
        }
    }
    
    private void addFacility() {
        System.out.println("\n--- Add New Facility ---");
        System.out.print("Enter Facility ID (e.g. F015): ");
        String id = sc.nextLine().trim();
        System.out.print("Enter Block: ");
        String block = sc.nextLine().trim();
        System.out.print("Enter Floor: ");
        String floor = sc.nextLine().trim();
        System.out.print("Enter Room No: ");
        String room = sc.nextLine().trim();
        System.out.print("Enter Type: ");
        String type = sc.nextLine().trim();
        System.out.print("Enter Capacity: ");
        int cap = Integer.parseInt(sc.nextLine().trim());

        Facility f = new Facility(id, block, floor, room, type, cap, "Available");
        String error = facilityService.addFacility(f);
        
        if (error == null) System.out.println("Facility added successfully with 'Available' status!");
        else System.out.println("Error: " + error);
    }
    
    private void updateStatus() {
        System.out.print("Enter Facility ID to update: ");
        String id = sc.nextLine().trim();
        
        List<Facility> allF = facilityService.getAllFacilities();
        Facility target = allF.stream().filter(f -> f.getFacilityID().equalsIgnoreCase(id)).findFirst().orElse(null);
        
        if (target == null) {
            System.out.println("Facility not found.");
            return;
        }

        System.out.println("Current Status: " + target.getStatus());
        System.out.println("Select New Status: [1] Available [2] Unavailable [3] Under Maintenance");
        String sChoice = sc.nextLine().trim();
        String newStatus;
        
        switch (sChoice) {
            case "1": newStatus = "Available"; break;
            case "2": newStatus = "Unavailable"; break;
            case "3": newStatus = "Under Maintenance"; break;
            default: System.out.println("Invalid choice."); return;
        }

        target.setStatus(newStatus);
        if (facilityService.updateFacilities(allF)) {
            System.out.println("Status updated to " + newStatus);
            if (newStatus.equals("Unavailable") || newStatus.equals("Under Maintenance")) {
                int count = bookingManager.rejectAllPendingForFacility(id, "Facility is now " + newStatus);
                System.out.println("System auto-rejected " + count + " pending bookings for this facility.");
            }
        }
    }
    
    private void removeFacility() {
        System.out.print("Enter Facility ID to remove: ");
        String id = sc.nextLine().trim();

        if (bookingManager.hasApprovedBookings(id)) {
            System.out.println("CANNOT REMOVE: This facility has upcoming approved bookings!");
        } else {
            System.out.print("Are you sure you want to delete " + id + "? [Y/N]: ");
            if (sc.nextLine().equalsIgnoreCase("Y")) {
                facilityService.removeFacility(id);
                System.out.println("Facility removed successfully.");
            }
        }
    }
    
    private void viewAllFacilities() {
        List<Facility> all = facilityService.getAllFacilities();

        if (all.isEmpty()) {
            System.out.println("\n[INFO] No facilities found in the system.");
            return;
        }

        System.out.println("\n--- Current Facilities List ---");
        System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n", 
                          "ID", "Block", "Floor", "Room", "Type", "Capacity", "Status");
        System.out.println("-----------------------------------------------------------------------------------");

        for (Facility f : all) {
            System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n", 
                              f.getFacilityID(), 
                              f.getBlock(), 
                              f.getFloor(), 
                              f.getRoomNo(),
                              f.getType(), 
                              f.getCapacity(), 
                              f.getStatus());
        }
        System.out.println("-----------------------------------------------------------------------------------");
    }
    
    private void approval() {
        List<Booking> pendingList = bookingManager.getPendingBookingsSorted();

        if (pendingList.isEmpty()) {
            System.out.println("\nNo pending booking requests at the moment.");
            return;
        }

        System.out.println("\n--- Processing Pending Bookings ---");
        
        for (Booking b : pendingList) {
            if (b.getTimeSlot() < 0 || b.getTimeSlot() >= my.edu.utar.util.Constants.TIME_SLOTS.length) {
                System.out.println("\n[SKIP] Skipping Booking " + b.getBookingID() + " due to invalid time slot data (" + b.getTimeSlot() + ").");
                continue; 
            }
            System.out.println("\n--------------------------------------------");
            b.display(); 
            System.out.println("--------------------------------------------");
            System.out.print("Action - [A] Approve, [R] Reject, [S] Skip: ");
            String action = sc.nextLine().trim().toUpperCase();

            if (action.equals("A")) {
                if (bookingManager.hasConflict(b)) {
                    System.out.print("WARNING: Conflict detected! Proceed anyway? [Y/N]: ");
                    if (!sc.nextLine().trim().equalsIgnoreCase("Y")) continue;
                }
                bookingManager.approveBooking(b.getBookingID());
                System.out.println("Booking approved successfully!");

            } else if (action.equals("R")) {
                String reason = "";
                while (reason.isEmpty()) {
                    System.out.print("Enter rejection reason: ");
                    reason = sc.nextLine().trim();
                }
                bookingManager.rejectBooking(b.getBookingID(), reason);
                System.out.println("Booking rejected.");

            } else if (action.equals("S")) {
                System.out.println("Skipped.");
            }
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
        System.out.println("\n--- GENERATE SUMMARY REPORT ---");
        System.out.println("Select Report Period: [1] Monthly [2] Semester [3] Yearly");
        System.out.print("Choice: ");
        String periodType = sc.nextLine().trim();
        
        System.out.print("Enter specific period (e.g., 2026-03, 2026S1, 2026): ");
        String period = sc.nextLine().trim();

        List<Booking> allBookings = bookingManager.getBookingList();
        List<Facility> allFacilities = facilityService.getAllFacilities();
        List<String[]> maintenanceData = loadMaintenanceData(period); 

        displayReportManual(allBookings, allFacilities, maintenanceData, period);
    }
    
    private List<String[]> loadMaintenanceData(String period) {
        List<String[]> results = new ArrayList<>();
        File f = new File("data/maintenance.txt");
        
        if (!f.exists()) {
            return results;
        }

        try {
            Scanner reader = new Scanner(f);
            while (reader.hasNextLine()) {
                String row = reader.nextLine();
                
                if (row.trim().equals("")) {
                    continue;
                }
                
                String[] parts = row.split("\\|");
                
                if (parts.length >= 5) {
                    String dateInFile = parts[2];
                    if (dateInFile.contains(period)) {
                        results.add(parts);
                    }
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find maintenance file!");
        }
        
        return results;
    }
    
    private void displayReportManual(List<Booking> bookings, List<Facility> facilities, List<String[]> maintenance, String period) {
        System.out.println("\n============================================================");
        System.out.println("                FACILITY ANALYTICS REPORT");
        System.out.println("============================================================");
        System.out.printf("%-10s | %-15s | %-10s | %-10s | %-10s\n", "ID", "Type", "Booked", "Maint", "Util%");
        System.out.println("------------------------------------------------------------");

        String mostUsedID = "N/A";
        int maxBookings = -1;
        double totalHours = 0;

        for (Facility f : facilities) {
            int bookingCount = 0;
            int maintCount = 0;

            for (Booking b : bookings) {
                if (b.getFacilityID().equals(f.getFacilityID()) && 
                    b.getBookingDate().contains(period) && 
                    b.getStatus().equalsIgnoreCase("Approved")) {
                    bookingCount++;
                }
            }

            for (String[] m : maintenance) {
                if (m[1].equals(f.getFacilityID())) {
                    maintCount++;
                    totalHours += Double.parseDouble(m[4]);
                }
            }

            if (bookingCount > maxBookings) {
                maxBookings = bookingCount;
                mostUsedID = f.getFacilityID();
            }

            double util = (bookingCount / 20.0) * 100;

            System.out.printf("%-10s | %-15s | %-10d | %-10d | %-9.1f%%\n", 
                              f.getFacilityID(), f.getType(), bookingCount, maintCount, util);
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("Most Frequently Used Facility: " + mostUsedID);
        
        double avgTime = 0;
        if (maintenance.size() > 0) {
            avgTime = totalHours / maintenance.size();
        }
        System.out.printf("Average Repair Time: %.2f hours\n", avgTime);
        System.out.println("============================================================\n");
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
