package my.edu.utar.ui;

import my.edu.utar.model.Admin;
import my.edu.utar.util.Validator;

import java.util.Scanner;

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
        System.out.println("[TODO - Member 4] Search Facility Status");
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
