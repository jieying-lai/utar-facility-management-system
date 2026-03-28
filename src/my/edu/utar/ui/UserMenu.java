package my.edu.utar.ui;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.User;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;

import java.util.Scanner;

public class UserMenu {

    private Scanner sc;
    private User currentUser;

    public UserMenu(Scanner sc, User currentUser) {
        this.sc = sc;
        this.currentUser = currentUser;
    }

    public void show() {
        while (true) {
            showReminders();     
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

    private void showReminders() {
        System.out.println("\n--- REMINDERS ---");
        // TODO: implement reminder logic here
        System.out.println("No upcoming reminders.");
        System.out.println("-----------------");
    }

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
                case "1": updateName();             break;
                case "2": updatePhone();            break;
                case "3": updateFaculty();          break;
                case "4": updateProgrammeDept();    break;
                case "5": updatePassword();         break;
                default:
                    System.out.println("Invalid selection, please try again.");
            }
        }
    }

    private void updateName() {
        while (true) {
            System.out.print("Enter new name: ");
            String input = sc.nextLine().trim();
            if (Validator.isEmpty(input)) { 
                System.out.println("Name cannot be empty."); 
                continue; 
            }
            currentUser.setName(input.toUpperCase());
            FileManager.updateUser(currentUser);
            System.out.println("Name updated successfully to: " + currentUser.getName());
            break;
        }
    }

    private void updatePhone() {
        while (true) {
            System.out.print("Enter new phone number (e.g. 0112345678): ");
            String input = sc.nextLine().trim();
            if (Validator.isEmpty(input)) { 
                System.out.println("Cannot be empty."); 
                continue; 
            }
            if (!Validator.isValidPhone(input)) {
                System.out.println("Invalid phone number. Format: 01xxxxxxxxx (10-11 digits)");
                continue;
            }
            currentUser.setPhone(input);
            FileManager.updateUser(currentUser);
            System.out.println("Phone number updated successfully to: " + currentUser.getPhone());
            break;
        }
    }

    private void updateFaculty() {
        while (true) {
            System.out.println("\nSelect new Faculty:");
            for (int i = 0; i < Constants.FACULTIES.length; i++) {
                System.out.println("[" + (i + 1) + "] " + Constants.FACULTIES[i]);
            }
            System.out.println("[B] Back");
            System.out.print("Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
            if ("B".equals(input)) return;
            if (!Validator.isValidMenuChoice(input, 1, Constants.FACULTIES.length)) {
                System.out.println("Invalid selection, please try again.");
                continue;
            }
            int index = Integer.parseInt(input) - 1;
            currentUser.setFaculty(Constants.FACULTIES[index]);
            FileManager.updateUser(currentUser);
            System.out.println("Faculty updated successfully to: " + currentUser.getFaculty());
            
            // Ask if they want to update programme too
            System.out.print("Do you want to update Programme/Department too? [Y/N]: ");
            String confirm = sc.nextLine().trim().toUpperCase();
            if ("Y".equals(confirm)) {
                updateProgrammeDept();
            }
            break;
        }
    }

    private void updateProgrammeDept() {
        String type = Constants.ROLE_STUDENT.equals(currentUser.getRole()) ? "Programme" : "Department";
        String example = Constants.ROLE_STUDENT.equals(currentUser.getRole()) ? "SE" : "IT";

        System.out.println("\nUpdate " + type);
        System.out.println("Enter your " + type + " abbreviation (e.g., " + example + "):");
        System.out.println("[B] Back");
        System.out.print("Enter choice: ");
        
        String input = sc.nextLine().trim();

        // 1. Check if user wants to go back
        if (input.equalsIgnoreCase("B")) return;

        // 2. Validate empty input
        if (Validator.isEmpty(input)) {
            System.out.println("Error: " + type + " cannot be empty.");
            return;
        }

        // 3. Store as Capitalized (Uppercase)
        String formattedInput = input.toUpperCase();
        
        currentUser.setProgramme(formattedInput);
        FileManager.updateUser(currentUser);
        
        System.out.println(type + " updated successfully to: " + formattedInput);
    }

    private void updatePassword() {
        while (true) {
            System.out.print("Enter current password: ");
            String oldPass = sc.nextLine().trim();
            if (Validator.isEmpty(oldPass)) { 
                System.out.println("Cannot be empty."); 
                continue; 
            }
            // Verify current password
            if (!oldPass.equals(currentUser.getPassword())) {
                System.out.println("Incorrect current password. Please try again.");
                continue;
            }
            System.out.print("Enter new password (min 8 characters): ");
            String newPass = sc.nextLine().trim();
            if (!Validator.isValidPassword(newPass)) {
                System.out.println("Password must be at least 8 characters.");
                continue;
            }
            System.out.print("Confirm new password: ");
            String confirmPass = sc.nextLine().trim();
            if (!Validator.passwordsMatch(newPass, confirmPass)) {
                System.out.println("Passwords do not match. Please try again.");
                continue;
            }
            currentUser.setPassword(newPass);
            FileManager.updateUser(currentUser);
            System.out.println("Password updated successfully!");
            break;
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
