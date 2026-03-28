
package my.edu.utar.ui;

import java.util.List;
import java.util.Scanner;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.Admin;
import my.edu.utar.model.Facility;
import my.edu.utar.util.Validator;

public class AdminMenu {

    private Scanner sc;
    private Admin currentAdmin;

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

    private void showIssueAlerts() {
        // TODO Member 3: implement alert check here
    }

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
        System.out.println("[TODO - Member 4] Manage Facilities");
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
