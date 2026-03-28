package my.edu.utar.ui;

import my.edu.utar.data.FileManager;
import my.edu.utar.model.*;
import my.edu.utar.util.Constants;
import my.edu.utar.util.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MainMenu {

    private Scanner sc;

    public MainMenu(Scanner sc) {
        this.sc = sc;
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
        System.out.println("  [E] Exit");
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
        String faculty = Constants.FACULTIES[facultyIndex];

        String programme = enterProgrammeOrDept(role);
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
        System.out.println("  Faculty     : " + faculty);
        System.out.println("  Programme   : " + programme);
        System.out.println("--------------------------------------------");
        System.out.print("  Confirm registration? [Y] Yes / [N] No: ");

        String confirm = sc.nextLine().trim().toUpperCase();
        if (!"Y".equals(confirm)) {
            System.out.println("\n  Registration cancelled.");
            return;
        }

        User newUser;
        if (Constants.ROLE_STUDENT.equals(role)) {
            newUser = new Student(id, name, email, phone, password, faculty, programme);
        } else {
            newUser = new Staff(id, name, email, phone, password, faculty, programme);
        }
        FileManager.saveUser(newUser);
        System.out.println("\n  Registration successful! You can now login.");
    }

    private String chooseRole() {
        while (true) {
            System.out.println("\n  Select your role:");
            System.out.println("  [1] Student");
            System.out.println("  [2] Staff");
            System.out.println("  [C] Cancel");
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
            System.out.print("\n  Enter " + role + " ID (7 digits) or [C] Cancel: ");
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
            System.out.print("\n  Enter Full Name or [C] Cancel: ");
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
                System.out.print("\n  Enter Student Email (e.g., 2301234@1utar.my) or [C] Cancel: ");
            } else {
                System.out.print("\n  Enter Staff Email (e.g., name@utar.edu.my) or [C] Cancel: ");
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
            System.out.print("\n  Enter Phone Number (e.g., 0112345678) or [C] Cancel: ");
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
            System.out.println("  [C] Cancel");
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

    private String enterProgrammeOrDept(String role) {
        while (true) {
            System.out.println();
            if (Constants.ROLE_STUDENT.equals(role)) {
                System.out.println("  Enter your Programme using short form.");
                System.out.println("  Examples:");
                System.out.println("    SE  = Bachelor of Software Engineering");
                System.out.println("    CS  = Bachelor of Computer Science");
                System.out.println("    ME  = Bachelor of Mechanical Engineering");
                System.out.println("    BUS = Bachelor of Business Administration");
                System.out.println("    MED = Bachelor of Medicine (MBBS)");
                System.out.println("    NUR = Bachelor of Nursing");
                System.out.print("  Enter short form (2-5 letters) or [C] Cancel: ");
            } else {
                System.out.println("  Enter your Department using short form.");
                System.out.println("  Examples:");
                System.out.println("    HR  = Human Resource");
                System.out.println("    IT  = Information Technology");
                System.out.println("    FIN = Finance and Accounts");
                System.out.println("    LIB = Library");
                System.out.println("    FM  = Facility Management");
                System.out.print("  Enter short form (2-5 letters) or [C] Cancel: ");
            }

            String input = sc.nextLine().trim().toUpperCase();

            if (Validator.isEmpty(input)) {
                System.out.println("  Cannot be empty. Please try again.");
                continue;
            }
            if ("C".equals(input)) {
                System.out.println("\n  Registration cancelled.");
                return null;
            }

            if (!input.matches("[A-Z]{2,5}")) {
                System.out.println("  Invalid format! Short form must be:");
                System.out.println("  - Letters only (no numbers or symbols)");
                System.out.println("  - Between 2 to 5 characters");
                System.out.println("  - Example: SE, MH, CL, AS, MBBS");
                continue;
            }

            System.out.print("\n  You entered: \"" + input + "\". Confirm? [Y] Yes / [N] Re-enter / [C] Cancel: ");
            String confirm = sc.nextLine().trim().toUpperCase();

            if ("Y".equals(confirm)) {
                return input; 
            } else if ("C".equals(confirm)) {
                System.out.println("\n  Registration cancelled.");
                return null;
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
        System.out.println("============================================");
        System.out.println("                  LOGIN                     ");
        System.out.println("============================================");

        System.out.print("  Enter ID: ");
        String id = sc.nextLine().trim();
        if (Validator.isEmpty(id)) {
            System.out.println("  Please fill in completely.");
            return;
        }

        System.out.print("  Enter Password: ");
        String password = sc.nextLine().trim();
        if (Validator.isEmpty(password)) {
            System.out.println("  Please fill in completely.");
            return;
        }

        Admin admin = FileManager.loginAdmin(id, password);
        if (admin != null) {
            new AdminMenu(sc, admin).show();
            return;
        }

        User user = FileManager.loginUser(id, password);
        if (user != null) {
            new UserMenu(sc, user).show();
            return;
        }

        if (FileManager.findUserById(id) != null) {
            System.out.println("\n  Incorrect password. Please try again.");
        } else {
            System.out.println("\n  Account not found. Please register first.");
        }
    }

    private void exitProgram() {
        System.out.println("============================================");
        System.out.println("  Thank you for using UTAR Smart Campus!");
        System.out.println("  Goodbye!");
        System.out.println("============================================");
    }
}