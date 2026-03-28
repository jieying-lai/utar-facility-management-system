
package my.edu.utar.ui;

import java.util.List;
import java.util.Scanner;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.Admin;
import my.edu.utar.model.Facility;
import my.edu.utar.util.Validator;

/**
 * AdminMenu.java
 * The main menu page for Admin users.
 * Member 4 owns the full implementation of this class.
 * Member 1 sets up this skeleton so login can route here correctly.
 */
public class AdminMenu {

    private Scanner sc;
    private Admin currentAdmin;

    public AdminMenu(Scanner sc, Admin currentAdmin) {
        this.sc = sc;
        this.currentAdmin = currentAdmin;
    }

    // ===================== ADMIN MENU =====================
    public void show() {
        while (true) {
            showIssueAlerts();   // Always show alerts at top
            printAdminMenu();

            String choice = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }

            switch (choice) {
                case "1": searchFacility();         break;
                case "2": manageFacility();         break;
                case "3": approvalBooking();        break;
                case "4": facilityUsageTracking();  break;
                case "5": viewSummaryReport();      break;
                case "6": maintenanceManagement();  break;
                case "7": issueAlert();             break;
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

    // ===================== MEMBER 3: ISSUE ALERTS =====================
    /**
     * Auto-shown on every admin page load.
     * TODO Member 3: Implement this.
     * Check maintenance.txt for:
     *   - Facilities with 3+ unresolved issues
     *   - Issues pending more than 7 days
     */
    private void showIssueAlerts() {
        // TODO Member 3: implement alert check here
    }

    // ===================== MEMBER 4: ALL ADMIN FEATURES =====================
    /** TODO Member 4 */
    private void searchFacility() {
    	System.out.println("\n--- Search Facility Status ---");
    	System.out.print("Enter search keyword (ID, Block, or Type): ");
        String keyword = sc.nextLine().trim().toLowerCase();
        
        if (keyword.isEmpty()) {
            System.out.println("Error: Keyword cannot be empty.");
            return;
        }
        List<Facility> allFacilities = FileManager.loadAllFacilities();
        boolean found = false;
        
        System.out.println("\n---------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n", 
                          "ID", "Block", "Floor", "Room", "Type", "Capacity", "Status");
        System.out.println("---------------------------------------------------------------------------------------");
        
        for (Facility f : allFacilities) {
            if (f.getFacilityID().toLowerCase().contains(keyword) ||
                f.getBlock().toLowerCase().contains(keyword) ||
                f.getType().toLowerCase().contains(keyword)) {
                
                System.out.printf("%-10s %-8s %-8s %-10s %-15s %-10s %-15s\n",
                    f.getFacilityID(), f.getBlock(), f.getFloor(), f.getRoomNo(),
                    f.getType(), f.getCapacity(), f.getStatus());
                
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("No matching facilities found for: " + keyword);
        }
        System.out.println("---------------------------------------------------------------------------------------");
    }

    /** TODO Member 4 */
    private void manageFacility() {
    	Scanner input = new Scanner (System.in);
    	while (true) {
    		System.out.println("Facilities Management Page");
            System.out.println("Please enter your choice: ");
            System.out.println("[1] View all current facilities");
            System.out.println("[2] Add new facilities");
            System.out.println("[3] Remove unused facilities");
            System.out.println("[4] Edit current facilities details");
            String choice = sc.nextLine().trim().toUpperCase();
            
            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }
 
            switch (choice) {
            case "1" :
            	System.out.println("---Current facilities List---");
            	System.out.println("facilityID|block|floor|roomNo|type|capacity|status");
            	List <Facility> all =FileManager.loadAllFacilities();
            	for(Facility f : all) {
            		System.out.println(f.toFileString());
            	}
            	System.out.println("Press enter button to return to the admin menu...");
            	input.nextLine();
            break;
            case "2" :
            	System.out.println("Please add a new facility by using format below.");
            	System.out.println("facilityID|block|floor|roomNo|type|capacity|status");
            	System.out.print("Enter facility ID (e.g. F001): ");
            	String ID = input.nextLine();
            	
            	if(FileManager.isFacilityIdExists(ID)) {
            		System.out.println("ID" + ID + "exists in the system. Try another ID.");
            	} else {
            		System.out.print("Enter block name (e.g. KB): ");
            		String block = input.nextLine();
                	System.out.print("Enter floor number (e.g. 1): ");
                	String num = input.nextLine();
                	System.out.print("Enter roomNo (e.g. KB104): ");
                	String roomNo = input.nextLine();
                	System.out.print("Enter type of the facility (e.g. Lecture hall): ");
                	String type = input.nextLine();
                	System.out.print("Enter capacity of the facility (e.g. 80): ");
                	String cap = input.nextLine();
                	int capacity = Integer.parseInt(cap);
                	String status = "Available";
                	
                	Facility f = new Facility(ID,block,num,roomNo,type,capacity,status);
                	
                	if(FileManager.isExactFacilityDuplicate(f)) {
                		System.out.println("Facility added failed. It is already exists in the system.");
                	} else {
                		FileManager.saveFacility(f);
                		System.out.println("The facility is successfully added!");
                	}
            	}
            	break;
            case "3" :
            	
            }

            
    	}
    }

    /** TODO Member 4 */
    private void approvalBooking() {
        System.out.println("[TODO - Member 4] Process Booking Requests");
    }

    /** TODO Member 4 */
    private void facilityUsageTracking() {
        System.out.println("[TODO - Member 4] Facility Usage Tracking");
    }

    /** TODO Member 4 */
    private void viewSummaryReport() {
        System.out.println("[TODO - Member 4] View Summary Report");
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
