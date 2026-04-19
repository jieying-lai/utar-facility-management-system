
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
        System.out.println("\n========== GENERATE SUMMARY REPORT ==========");
        System.out.println("Select Report Period: [1] Monthly [2] Semester [3] Yearly");
        System.out.print("Choice: ");
        String periodType = sc.nextLine().trim();
        
        System.out.print("Enter specific period (e.g., 2026-03, 2026-S1, or 2026): ");
        String period = sc.nextLine().trim();

        
        List<Booking> allBookings = bookingManager.getBookingList();
        List<Facility> allFacilities = facilityService.getAllFacilities();
        List<String[]> maintenanceData = loadMaintenanceData(period); 

        List<Booking> filteredBookings = new ArrayList<>();
        for (Booking b : allBookings) {
            if (b.getBookingDate().contains(period) && b.getStatus().equalsIgnoreCase("Approved")) {
                filteredBookings.add(b);
            }
        }

        if (filteredBookings.isEmpty() && maintenanceData.isEmpty()) {
            System.out.println("No data found for the selected period: " + period);
            return;
        }
        calculateAndDisplayAnalytics(filteredBookings, allFacilities, maintenanceData);
    }
    
    private List<String[]> loadMaintenanceData(String period) {
        List<String[]> filteredMaint = new ArrayList<>();
        File file = new File("data/maintenance.txt");
        
        if (!file.exists()) return filteredMaint;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue;
                
                String[] data = line.split("\\|");
                if (data.length > 4 && data[2].contains(period)) {
                    filteredMaint.add(data);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Maintenance record file not found.");
        }
        return filteredMaint;
    }
    
    private void calculateAndDisplayAnalytics(List<Booking> filtered, List<Facility> facilities, List<String[]> maintenance) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("FACILITY ANALYTICS REPORT");
        System.out.println("=".repeat(60));

        Map<String, Integer> usageMap = new HashMap<>();
        Map<String, Integer> maintMap = new HashMap<>();
        double totalRepairHours = 0;

        for (Booking b : filtered) {
            usageMap.put(b.getFacilityID(), usageMap.getOrDefault(b.getFacilityID(), 0) + 1);
        }

        for (String[] m : maintenance) {
            String fID = m[1]; 
            maintMap.put(fID, maintMap.getOrDefault(fID, 0) + 1);
            totalRepairHours += Double.parseDouble(m[4]);
        }

        double avgRepairTime = maintenance.isEmpty() ? 0 : totalRepairHours / maintenance.size();

        String mostUsed = "N/A";
        if (!usageMap.isEmpty()) {
            mostUsed = Collections.max(usageMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        }

        System.out.printf("%-10s | %-15s | %-12s | %-12s | %-10s\n", 
                          "Fac ID", "Type", "Bookings", "Maint Cases", "Util %");
        System.out.println("-".repeat(60));

        for (Facility f : facilities) {
            int count = usageMap.getOrDefault(f.getFacilityID(), 0);
            int mCount = maintMap.getOrDefault(f.getFacilityID(), 0);
            
            double utilRate = (count / 20.0) * 100;

            System.out.printf("%-10s | %-15s | %-12d | %-12d | %-9.1f%%\n", 
                              f.getFacilityID(), f.getType(), count, mCount, utilRate);
        }

        System.out.println("-".repeat(60));
        System.out.println("Most Frequently Used: " + mostUsed);
        System.out.printf("Average Repair Time: %.2f hours\n", avgRepairTime);
        System.out.println("=".repeat(60));
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
