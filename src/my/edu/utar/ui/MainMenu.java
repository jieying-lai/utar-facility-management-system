package my.edu.utar.ui;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.*;
import my.edu.utar.service.BookingManager;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;
import java.util.Map;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MainMenu {

    private Scanner sc;
    private BookingManager bookingManager;

    public MainMenu(Scanner sc) {
        this.sc = sc;
        
        bookingManager = new BookingManager();
        bookingManager.loadFromFile();
    }

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
        LocalDateTime now = LocalDateTime.now();
        String currentDate = now.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        String currentTime = now.format(DateTimeFormatter.ofPattern("hh:mm a"));

        System.out.println("============================================");
        System.out.println("    UTAR Smart Campus Management System     ");
        System.out.println("============================================");
        System.out.println("  Date : " + currentDate);
        System.out.println("  Time : " + currentTime);
        System.out.println("--------------------------------------------");
        System.out.println("  [1] Register New User");
        System.out.println("  [2] Login");
        System.out.println("  [E] xit");
        System.out.println("--------------------------------------------");
        System.out.print("  Enter your choice: ");
    }

    private void registerUser() {
        System.out.println("============================================");
        System.out.println("           REGISTER NEW USER                ");
        System.out.println("============================================");
        System.out.println("  [C] to cancel registration at any time    ");
        System.out.println("--------------------------------------------");

        String role = chooseRole();
        if (role == null) return;

        String id = enterStudentId(role);
        if (id == null) return;

        String name = enterName();
        if (name == null) return;

        String email = enterEmail(role);
        if (email == null) return;

        String phone = enterPhone();
        if (phone == null) return;

        int facultyIndex = chooseFaculty();
        if (facultyIndex == -1) return;
        
        String facultyFullName = Constants.FACULTIES[facultyIndex];
        String facultyCode = Constants.FACULTY_CODE_MAP.get(facultyFullName);
        String programme = enterProgrammeOrDept(role, facultyFullName);
        
        if (programme == null) return;

        String password = enterPassword();
        if (password == null) return;

        System.out.println("============================================");
        System.out.println("         REGISTRATION SUMMARY               ");
        System.out.println("============================================");
        System.out.println("  Role        : " + role);
        System.out.println("  ID          : " + id);
        System.out.println("  Name        : " + name);
        System.out.println("  Email       : " + email);
        System.out.println("  Phone       : " + phone);
        System.out.println("  Faculty     : " + facultyFullName);
        String progDisplay = Constants.FACULTY_PROGRAMME_MAP.get(facultyFullName) != null ? 
                Constants.FACULTY_PROGRAMME_MAP.get(facultyFullName).getOrDefault(programme, programme) : 
                programme;
        System.out.println("  Programme   : " + progDisplay);
        System.out.println("--------------------------------------------");
        System.out.print("  Confirm registration? [Y] Yes / [N] No: ");

        String confirm = sc.nextLine().trim().toUpperCase();
        if (!"Y".equals(confirm)) {
            System.out.println("\n  Registration cancelled.");
            return;
        }

        User newUser;
        if (Constants.ROLE_STUDENT.equals(role)) {
            newUser = new Student(id, name, email, phone, password, facultyCode, programme);
        } else {
            newUser = new Staff(id, name, email, phone, password, facultyCode, programme);
        }
        
        FileManager.saveUser(newUser);
        System.out.println("\n  Registration successful!");
    }

    private String chooseRole() {
        while (true) {
            System.out.println("\n  Select your role:");
            System.out.println("  [1] Student");
            System.out.println("  [2] Staff");
            System.out.println("  [C] ancel");
            System.out.print("  Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) {
                System.out.println("  Cannot be empty. Please try again.");
                continue;
            }
            if ("C".equals(input)) {
                System.out.println("\n  Registration cancelled.");
                return null;
            }
            if ("1".equals(input)) return Constants.ROLE_STUDENT;
            if ("2".equals(input)) return Constants.ROLE_STAFF;
            System.out.println("  Invalid selection, please try again.");
        }
    }

    private String enterStudentId(String role) {
        while (true) {
            System.out.print("\n  Enter " + role + " ID (7 digits) or [C]ancel: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) {
                System.out.println("  Cannot be empty. Please try again.");
                continue;
            }
            if ("C".equals(input)) {
                System.out.println("\n  Registration cancelled.");
                return null;
            }
            if (!Validator.isValidStudentID(input)) {
                System.out.println("  Invalid ID format. Must be exactly 7 digits (e.g., 2301888).");
                continue;
            }
            if (FileManager.isUserIdExists(input)) {
                System.out.println("  This ID is already registered. Please login instead.");
                return null;
            }
            return input;
        }
    }

    private String enterName() {
        while (true) {
            System.out.print("\n  Enter Full Name or [C] ancel: ");
            String input = sc.nextLine().trim();

            if (Validator.isEmpty(input)) {
                System.out.println("  Name cannot be empty. Please try again.");
                continue;
            }
            if ("C".equalsIgnoreCase(input)) {
                System.out.println("\n  Registration cancelled.");
                return null;
            }
            return input.toUpperCase();
        }
    }

    private String enterEmail(String role) {
        while (true) {
            if (Constants.ROLE_STUDENT.equals(role)) {
                System.out.print("\n  Enter Student Email (e.g., 2301234@1utar.my) or [C] ancel: ");
            } else {
                System.out.print("\n  Enter Staff Email (e.g., name@utar.edu.my) or [C] ancel: ");
            }
            String input = sc.nextLine().trim().toLowerCase();

            if (Validator.isEmpty(input)) {
                System.out.println("  Cannot be empty. Please try again.");
                continue;
            }
            if ("c".equals(input)) {
                System.out.println("\n  Registration cancelled.");
                return null;
            }
            if (!Validator.isValidEmail(input, role)) {
                if (Constants.ROLE_STUDENT.equals(role)) {
                    System.out.println("  Invalid student email. Must end with @1utar.my");
                } else {
                    System.out.println("  Invalid staff email. Must end with @utar.edu.my");
                }
                continue;
            }
            return input;
        }
    }

    private String enterPhone() {
        while (true) {
            System.out.print("\n  Enter Phone Number (e.g., 0112345678) or [C] ancel: ");
            String input = sc.nextLine().trim();

            if (Validator.isEmpty(input)) {
                System.out.println("  Cannot be empty. Please try again.");
                continue;
            }
            if ("C".equalsIgnoreCase(input)) {
                System.out.println("\n  Registration cancelled.");
                return null;
            }
            if (!Validator.isValidPhone(input)) {
                System.out.println("  Invalid phone number. Must start with 01 and have 10-11 digits.");
                continue;
            }
            return input;
        }
    }

    private int chooseFaculty() {
        while (true) {
            System.out.println("\n  Select Faculty:");
            for (int i = 0; i < Constants.FACULTIES.length; i++) {
                System.out.println("  [" + (i + 1) + "] " + Constants.FACULTIES[i]);
            }
            System.out.println("  [C] ancel");
            System.out.print("  Enter choice: ");
            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) {
                System.out.println("  Cannot be empty. Please try again.");
                continue;
            }
            if ("C".equals(input)) {
                System.out.println("\n  Registration cancelled.");
                return -1;
            }
            if (Validator.isValidMenuChoice(input, 1, Constants.FACULTIES.length)) {
                return Integer.parseInt(input) - 1;
            }
            System.out.println("  Invalid selection, please try again.");
        }
    }

    private String enterProgrammeOrDept(String role, String selectedFaculty) {
        while (true) {
            System.out.println("\n--------------------------------------------");
            if (Constants.ROLE_STUDENT.equals(role)) {
                System.out.println("  Enter your Programme Code (e.g., SE, MH, AS):");
            } else {
                System.out.println("  Enter your Department Code (e.g., HR, LIB):");
            }
            System.out.print("  Choice: ");

            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) {
                System.out.println("  Cannot be empty.");
                continue;
            }
            if ("C".equals(input)) return null;

            Map<String, String> programmes = Constants.FACULTY_PROGRAMME_MAP.get(selectedFaculty);
            String fullName = (programmes != null) ? programmes.get(input) : null;

            if (fullName != null) {
                System.out.println("  Verified Programme: " + fullName);
                System.out.print("  Confirm? [Y] Yes / [N] Re-enter: ");
            } else {
                System.out.println("  [WARNING] \"" + input + "\" is not registered under " + selectedFaculty + ".");
                System.out.print("  Are you sure you want to persist with this code? [Y] Yes / [N] Re-enter: ");
            }

            String confirm = sc.nextLine().trim().toUpperCase();
            if ("Y".equals(confirm)) {
                return input; 
            }
        }
    }

    private String enterPassword() {
        while (true) {
            System.out.print("\n  Enter Password (min 8 characters) or [C] Cancel: ");
            String pass1 = sc.nextLine().trim();

            if (Validator.isEmpty(pass1)) {
                System.out.println("  Cannot be empty. Please try again.");
                continue;
            }
            if ("C".equalsIgnoreCase(pass1)) {
                System.out.println("\n  Registration cancelled.");
                return null;
            }
            if (!Validator.isValidPassword(pass1)) {
                System.out.println("  Password must be at least 8 characters.");
                continue;
            }

            System.out.print("  Confirm Password: ");
            String pass2 = sc.nextLine().trim();

            if (!Validator.passwordsMatch(pass1, pass2)) {
                System.out.println("  Passwords do not match. Please re-enter.");
                continue;
            }
            return pass1;
        }
    }

    private void login() {
        while (true) {
            System.out.println("============================================");
            System.out.println("                  LOGIN                     ");
            System.out.println("============================================");

            System.out.print("  Enter ID (or 'B' to back): ");
            String id = sc.nextLine().trim();

            if (id.equalsIgnoreCase("B")) {
                return; 
            }

            if (Validator.isEmpty(id)) {
                System.out.println("  ID cannot be empty.");
                continue;
            }

            System.out.print("  Enter Password (or 'B' to back): ");
            String password = sc.nextLine().trim();
            
            if (password.equalsIgnoreCase("B")) {
                return;
            }

            if (Validator.isEmpty(password)) {
                System.out.println("  Password cannot be empty.");
                continue; 
            }

            Admin admin = FileManager.loginAdmin(id, password);
            if (admin != null) {
                new AdminMenu(sc, admin).show();
                return; 
            }

            User user = FileManager.loginUser(id, password);
            if (user != null) {
                System.out.println("\nWelcome, " + user.getName() + "! (" + user.getRole() + ")");
                new UserMenu(sc, user, bookingManager).show();
                return;
            }

            
            if (FileManager.findUserById(id) != null) {
                System.out.println("\n  >> Incorrect password. Please try again.");
            } else {
                System.out.println("\n  >> Account not found. Please register first.");
            }
            System.out.println("--------------------------------------------");
        }
    }

    private void exitProgram() {
        System.out.println("============================================");
        System.out.println("  Thank you for using UTAR Smart Campus!");
        System.out.println("  Goodbye!");
        System.out.println("============================================");
    }
}