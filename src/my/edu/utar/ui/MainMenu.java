package my.edu.utar.ui;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.*;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;

import java.util.Scanner;

/**
 * MainMenu.java
 * The entry point UI. Shows Main Page with Register, Login, Exit.
 * Member 1 owns this class.
 */
public class MainMenu {

    private Scanner sc;

    public MainMenu(Scanner sc) {
        this.sc = sc;
    }

    // ===================== MAIN PAGE DISPLAY =====================
    public void show() {
        while (true) {
            printMainMenu();
            String choice = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(choice)) {
                System.out.println("Cannot be empty. Please try again.");
                continue;
            }

            switch (choice) {
                case "1": registerUser(); break;
                case "2": login();        break;
                case "E": exitProgram();  return;
                default:
                    System.out.println("Invalid selection, please try again.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n============================================");
        System.out.println("   UTAR Smart Campus Management System");
        System.out.println("============================================");
        System.out.println("[1] Register New User");
        System.out.println("[2] Login");
        System.out.println("[E] Exit");
        System.out.println("--------------------------------------------");
        System.out.print("Enter your choice: ");
    }

    // ===================== REGISTER USER =====================
    private void registerUser() {
        System.out.println("\n========== REGISTER NEW USER ==========");

        // Step 1: Choose role
        String role = chooseRole();
        if (role == null) return; // user pressed back

        // Step 2: Student ID
        String id = enterStudentId(role);
        if (id == null) return;

        // Step 3: Name
        String name = enterName();
        if (name == null) return;

        // Step 4: Email
        String email = enterEmail(role);
        if (email == null) return;

        // Step 5: Phone
        String phone = enterPhone();
        if (phone == null) return;

        // Step 6: Faculty
        int facultyIndex = chooseFaculty();
        if (facultyIndex == -1) return;
        String faculty = Constants.FACULTIES[facultyIndex];

        // Step 7: Programme (student) or Department (staff)
        String programme = chooseProgrammeOrDept(role, facultyIndex);
        if (programme == null) return;

        // Step 8: Password
        String password = enterPassword();
        if (password == null) return;

        // Step 9: Confirm and save
        System.out.println("\n========== REGISTRATION SUMMARY ==========");
        System.out.println("Role        : " + role);
        System.out.println("ID          : " + id);
        System.out.println("Name        : " + name.toUpperCase());
        System.out.println("Email       : " + email.toLowerCase());
        System.out.println("Phone       : " + phone);
        System.out.println("Faculty     : " + faculty);
        System.out.println("Programme   : " + programme);
        System.out.println("==========================================");
        System.out.print("Confirm registration? [Y] Yes / [N] No: ");

        String confirm = sc.nextLine().trim().toUpperCase();
        if (!"Y".equals(confirm)) {
            System.out.println("Registration cancelled.");
            return;
        }

        // Create user object and save
        User newUser;
        if (Constants.ROLE_STUDENT.equals(role)) {
            newUser = new Student(id, name, email, phone, password, faculty, programme);
        } else {
            newUser = new Staff(id, name, email, phone, password, faculty, programme);
        }
        FileManager.saveUser(newUser);
        System.out.println("\nRegistration successful! You can now login.");
    }

    // ---- Register helper methods ----

    private String chooseRole() {
        while (true) {
            System.out.println("\nSelect your role:");
            System.out.println("[1] Student");
            System.out.println("[2] Staff");
            System.out.println("[B] Back");
            System.out.print("Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
            if ("B".equals(input)) return null;
            if ("1".equals(input)) return Constants.ROLE_STUDENT;
            if ("2".equals(input)) return Constants.ROLE_STAFF;
            System.out.println("Invalid selection, please try again.");
        }
    }

    private String enterStudentId(String role) {
        while (true) {
            System.out.print("\nEnter " + role + " ID (7 digits): ");
            String input = sc.nextLine().trim();

            if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
            if (!Validator.isValidStudentID(input)) {
                System.out.println("Invalid Student ID format. Must be 7 digits (e.g., 2301888).");
                continue;
            }
            if (FileManager.isUserIdExists(input)) {
                System.out.println("This ID is already registered. Please login instead.");
                return null;
            }
            return input;
        }
    }

    private String enterName() {
        while (true) {
            System.out.print("\nEnter Full Name: ");
            String input = sc.nextLine().trim();
            if (Validator.isEmpty(input)) { System.out.println("Name cannot be empty."); continue; }
            return input.toUpperCase();
        }
    }

    private String enterEmail(String role) {
        while (true) {
            System.out.print("\nEnter Email: ");
            String input = sc.nextLine().trim().toLowerCase();
            if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
            if (!Validator.isValidEmail(input, role)) {
                if (Constants.ROLE_STUDENT.equals(role)) {
                    System.out.println("Invalid student email. Must end with @1utar.my");
                } else {
                    System.out.println("Invalid staff email. Must end with @utar.edu.my");
                }
                continue;
            }
            return input;
        }
    }

    private String enterPhone() {
        while (true) {
            System.out.print("\nEnter Phone Number (e.g., 0112345678): ");
            String input = sc.nextLine().trim();
            if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
            if (!Validator.isValidPhone(input)) {
                System.out.println("Invalid phone number. Format: 01xxxxxxxxx (10-11 digits)");
                continue;
            }
            return input;
        }
    }

    private int chooseFaculty() {
        while (true) {
            System.out.println("\nSelect Faculty:");
            for (int i = 0; i < Constants.FACULTIES.length; i++) {
                System.out.println("[" + (i + 1) + "] " + Constants.FACULTIES[i]);
            }
            System.out.println("[B] Back");
            System.out.print("Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
            if ("B".equals(input)) return -1;
            if (Validator.isValidMenuChoice(input, 1, Constants.FACULTIES.length)) {
                return Integer.parseInt(input) - 1;
            }
            System.out.println("Invalid selection, please try again.");
        }
    }

    private String chooseProgrammeOrDept(String role, int facultyIndex) {
        if (Constants.ROLE_STUDENT.equals(role)) {
            String[] programmes = Constants.PROGRAMMES[facultyIndex];
            while (true) {
                System.out.println("\nSelect Programme:");
                for (int i = 0; i < programmes.length; i++) {
                    System.out.println("[" + (i + 1) + "] " + programmes[i]);
                }
                System.out.println("[B] Back");
                System.out.print("Enter choice: ");
                String input = sc.nextLine().trim().toUpperCase();

                if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
                if ("B".equals(input)) return null;
                if (Validator.isValidMenuChoice(input, 1, programmes.length)) {
                    return programmes[Integer.parseInt(input) - 1];
                }
                System.out.println("Invalid selection, please try again.");
            }
        } else {
            while (true) {
                System.out.println("\nSelect Department:");
                for (int i = 0; i < Constants.DEPARTMENTS.length; i++) {
                    System.out.println("[" + (i + 1) + "] " + Constants.DEPARTMENTS[i]);
                }
                System.out.println("[B] Back");
                System.out.print("Enter choice: ");
                String input = sc.nextLine().trim().toUpperCase();

                if (Validator.isEmpty(input)) { System.out.println("Cannot be empty."); continue; }
                if ("B".equals(input)) return null;
                if (Validator.isValidMenuChoice(input, 1, Constants.DEPARTMENTS.length)) {
                    return Constants.DEPARTMENTS[Integer.parseInt(input) - 1];
                }
                System.out.println("Invalid selection, please try again.");
            }
        }
    }

    private String enterPassword() {
        while (true) {
            System.out.print("\nEnter Password (min 8 characters): ");
            String pass1 = sc.nextLine().trim();
            if (Validator.isEmpty(pass1)) { System.out.println("Cannot be empty."); continue; }
            if (!Validator.isValidPassword(pass1)) {
                System.out.println("Password must be at least 8 characters.");
                continue;
            }
            System.out.print("Confirm Password: ");
            String pass2 = sc.nextLine().trim();
            if (!Validator.passwordsMatch(pass1, pass2)) {
                System.out.println("Passwords do not match. Please re-enter.");
                continue;
            }
            return pass1; // TODO: hash this before storing in real implementation
        }
    }

    // ===================== LOGIN =====================
    private void login() {
        System.out.println("\n========== LOGIN ==========");

        // Try admin login first, then user login
        System.out.print("Enter ID: ");
        String id = sc.nextLine().trim();
        if (Validator.isEmpty(id)) { System.out.println("Please fill in completely."); return; }

        System.out.print("Enter Password: ");
        String password = sc.nextLine().trim();
        if (Validator.isEmpty(password)) { System.out.println("Please fill in completely."); return; }

        // Check admin
        Admin admin = FileManager.loginAdmin(id, password);
        if (admin != null) {
            System.out.println("\nWelcome, " + admin.getName() + "! (Admin)");
            new AdminMenu(sc, admin).show();
            return;
        }

        // Check user
        User user = FileManager.loginUser(id, password);
        if (user != null) {
            System.out.println("\nWelcome, " + user.getName() + "! (" + user.getRole() + ")");
            new UserMenu(sc, user).show();
            return;
        }

        // Check if ID exists but wrong password
        if (FileManager.findUserById(id) != null) {
            System.out.println("Incorrect password. Please try again.");
        } else {
            System.out.println("Account not found. Please register first.");
        }
    }

    // ===================== EXIT =====================
    private void exitProgram() {
        System.out.println("\nThank you for using UTAR Smart Campus System. Goodbye!");
    }
}
