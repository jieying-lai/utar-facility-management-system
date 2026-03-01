package my.edu.utar.ui;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.User;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;

import java.util.Scanner;

/**
 * UserMenu.java
 * The main menu page for logged-in Students and Staff.
 *
 * Member 1 owns: updateProfile(), reminderBooking() display
 * Member 2 owns: searchAvailableFacility(), newBooking(),
 *                modifyBooking(), viewBookingRequestStatus()
 * Member 3 owns: reportIssue()
 * Member 4 owns: viewBookingHistory() summary portion
 */
public class UserMenu {

    private Scanner sc;
    private User currentUser;

    public UserMenu(Scanner sc, User currentUser) {
        this.sc = sc;
        this.currentUser = currentUser;
    }

    // ===================== MAIN USER MENU =====================
    public void show() {
        while (true) {
            showReminders();     // Always show reminders at top
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
                case "6": reportIssue();                break;
                case "7": viewBookingHistory();         break;
                case "L":
                    System.out.println("Logged out successfully. Goodbye, " + currentUser.getName() + "!");
                    return;
                default:
                    System.out.println("Invalid selection, please try again.");
            }
        }
    }

    private void printUserMenu() {
        System.out.println("\n============================================");
        System.out.println("   Welcome, " + currentUser.getName());
        System.out.println("   Role: " + currentUser.getRole());
        System.out.println("============================================");
        System.out.println("[1] Update Profile");
        System.out.println("[2] Search Available Facility");
        System.out.println("[3] New Booking");
        System.out.println("[4] Modify / Cancel Booking");
        System.out.println("[5] View Booking Request Status");
        System.out.println("[6] Report Issue");
        System.out.println("[7] View Booking History & Summary");
        System.out.println("[L] Logout");
        System.out.println("--------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    // ===================== MEMBER 1: SHOW REMINDERS =====================
    /**
     * Displays upcoming approved bookings as reminders.
     * TODO Member 1: Implement this method.
     * Logic:
     *   - Read bookings.txt
     *   - Filter by currentUser.getId() AND status = Approved
     *   - Show bookings where booking date is today or within next 3 days
     *   - If none: show "No upcoming reminders."
     */
    private void showReminders() {
        System.out.println("\n--- REMINDERS ---");
        // TODO: implement reminder logic here
        System.out.println("No upcoming reminders.");
        System.out.println("-----------------");
    }

    // ===================== MEMBER 1: UPDATE PROFILE =====================
    /**
     * Allows user to update editable fields.
     * Role, ID, and Email are NOT editable.
     * TODO Member 1: Implement this method.
     */
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
                case "1":
                    // TODO: update name (validate non-empty, UPPERCASE)
                    System.out.println("[TODO] Update name");
                    break;
                case "2":
                    // TODO: update phone (validate phone format)
                    System.out.println("[TODO] Update phone");
                    break;
                case "3":
                    // TODO: update faculty (show list, choose)
                    System.out.println("[TODO] Update faculty");
                    break;
                case "4":
                    // TODO: update programme/department (based on faculty)
                    System.out.println("[TODO] Update programme/department");
                    break;
                case "5":
                    // TODO: update password (validate min 8, confirm twice)
                    System.out.println("[TODO] Update password");
                    break;
                default:
                    System.out.println("Invalid selection, please try again.");
            }

            // After each update: call FileManager.updateUser(currentUser)
        }
    }

    // ===================== MEMBER 2: SEARCH FACILITY =====================
    /**
     * Multi-step facility search.
     * TODO Member 2: Implement this method.
     * Steps: Block > Facility Type > Floor > Room > Date > Time Slot
     */
    private void searchAvailableFacility() {
        System.out.println("[TODO - Member 2] Search Available Facility");
    }

    // ===================== MEMBER 2: NEW BOOKING =====================
    /**
     * Creates a new booking after facility search.
     * TODO Member 2: Implement this method.
     */
    private void newBooking() {
        System.out.println("[TODO - Member 2] New Booking");
    }

    // ===================== MEMBER 2: MODIFY/CANCEL BOOKING =====================
    /**
     * Shows pending bookings, allows modify or cancel.
     * TODO Member 2: Implement this method.
     */
    private void modifyBooking() {
        System.out.println("[TODO - Member 2] Modify / Cancel Booking");
    }

    // ===================== MEMBER 2: VIEW BOOKING STATUS =====================
    /**
     * Shows all upcoming booking requests and their status.
     * TODO Member 2: Implement this method.
     */
    private void viewBookingRequestStatus() {
        System.out.println("[TODO - Member 2] View Booking Request Status");
    }

    // ===================== MEMBER 3: REPORT ISSUE =====================
    /**
     * User selects a facility and reports a maintenance issue.
     * TODO Member 3: Implement this method.
     */
    private void reportIssue() {
        System.out.println("[TODO - Member 3] Report Issue");
    }

    // ===================== MEMBER 4: VIEW BOOKING HISTORY =====================
    /**
     * Shows past (approved/rejected) bookings and personal summary.
     * TODO Member 4: Implement summary analytics portion.
     * TODO Member 2: Implement the booking list display portion.
     */
    private void viewBookingHistory() {
        System.out.println("[TODO - Member 2 & 4] View Booking History");
    }
}
