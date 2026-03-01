package my.edu.utar.data;

import my.edu.utar.model.*;
import my.edu.utar.util.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager.java
 * Handles ALL file read/write operations for the system.
 * All members use this class to access .txt data files.
 * Never read/write files directly outside this class.
 */
public class FileManager {

    // ===================== INIT FILES =====================
    /**
     * Creates all required .txt files if they don't exist yet.
     * Call this once at program startup in Main.java
     */
    public static void initFiles() {
        String[] files = {
            Constants.FILE_USERS,
            Constants.FILE_ADMIN,
            Constants.FILE_FACILITIES,
            Constants.FILE_BOOKINGS,
            Constants.FILE_MAINTENANCE
        };
        // Make sure data/ folder exists
        new File("data").mkdirs();

        for (String filePath : files) {
            File f = new File(filePath);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                    System.out.println("[INFO] Created missing file: " + filePath);
                } catch (IOException e) {
                    System.out.println("[ERROR] Could not create file: " + filePath);
                }
            }
        }
    }

    // ===================== GENERIC HELPERS =====================
    /**
     * Reads all lines from a file. Returns empty list if file not found.
     */
    public static List<String> readAllLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("[ERROR] File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("[ERROR] Could not read file: " + filePath);
        }
        return lines;
    }

    /**
     * Writes all lines to a file (overwrites existing content).
     */
    public static void writeAllLines(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Could not write to file: " + filePath);
        }
    }

    /**
     * Appends a single line to a file.
     */
    public static void appendLine(String filePath, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("[ERROR] Could not append to file: " + filePath);
        }
    }

    // ===================== USER FILE OPERATIONS =====================
    /**
     * Loads all users from users.txt
     * Returns list of User objects (Student or Staff based on role field).
     */
    public static List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();
        List<String> lines = readAllLines(Constants.FILE_USERS);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            try {
                String[] parts = line.split(Constants.DELIMITER_REGEX, -1);
                // Format: id|name|email|phone|role|faculty|programme|password
                if (parts.length < 8) {
                    System.out.println("[WARNING] Skipping malformed user data on line " + (i + 1));
                    continue;
                }
                String id         = parts[0].trim();
                String name       = parts[1].trim();
                String email      = parts[2].trim();
                String phone      = parts[3].trim();
                String role       = parts[4].trim();
                String faculty    = parts[5].trim();
                String programme  = parts[6].trim();
                String password   = parts[7].trim();

                if (Constants.ROLE_STUDENT.equals(role)) {
                    users.add(new Student(id, name, email, phone, password, faculty, programme));
                } else if (Constants.ROLE_STAFF.equals(role)) {
                    users.add(new Staff(id, name, email, phone, password, faculty, programme));
                }
            } catch (Exception e) {
                System.out.println("[WARNING] Error reading user on line " + (i + 1) + ", skipping.");
            }
        }
        return users;
    }

    /**
     * Saves a new user to users.txt (appends one line).
     */
    public static void saveUser(User user) {
        appendLine(Constants.FILE_USERS, user.toFileString());
    }

    /**
     * Updates an existing user's record in users.txt.
     * Finds the line matching the user's ID and replaces it.
     */
    public static void updateUser(User updatedUser) {
        List<String> lines = readAllLines(Constants.FILE_USERS);
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(Constants.DELIMITER_REGEX, -1);
            if (parts.length > 0 && parts[0].trim().equals(updatedUser.getId())) {
                lines.set(i, updatedUser.toFileString());
                break;
            }
        }
        writeAllLines(Constants.FILE_USERS, lines);
    }

    /**
     * Checks if a student/staff ID already exists in users.txt.
     */
    public static boolean isUserIdExists(String id) {
        List<String> lines = readAllLines(Constants.FILE_USERS);
        for (String line : lines) {
            String[] parts = line.split(Constants.DELIMITER_REGEX, -1);
            if (parts.length > 0 && parts[0].trim().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds and returns a User by ID and password (for login).
     * Returns null if not found or wrong password.
     */
    public static User loginUser(String id, String password) {
        List<User> users = loadAllUsers();
        for (User user : users) {
            if (user.validateCredentials(id, password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Finds a user by ID only (to check if registered).
     * Returns null if not found.
     */
    public static User findUserById(String id) {
        List<User> users = loadAllUsers();
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    // ===================== ADMIN FILE OPERATIONS =====================
    /**
     * Loads all admins from admin.txt
     */
    public static List<Admin> loadAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        List<String> lines = readAllLines(Constants.FILE_ADMIN);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            try {
                String[] parts = line.split(Constants.DELIMITER_REGEX, -1);
                // Format: adminID|name|email|phone|department|password
                if (parts.length < 6) {
                    System.out.println("[WARNING] Skipping malformed admin data on line " + (i + 1));
                    continue;
                }
                admins.add(new Admin(
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    parts[3].trim(), parts[5].trim(), parts[4].trim()
                ));
            } catch (Exception e) {
                System.out.println("[WARNING] Error reading admin on line " + (i + 1) + ", skipping.");
            }
        }
        return admins;
    }

    /**
     * Finds an admin by ID and password for login.
     * Returns null if not found.
     */
    public static Admin loginAdmin(String id, String password) {
        List<Admin> admins = loadAllAdmins();
        for (Admin admin : admins) {
            if (admin.validateCredentials(id, password)) {
                return admin;
            }
        }
        return null;
    }

    // ===================== PLACEHOLDER METHODS (for other members) =====================
    // Members 2, 3, 4 will add their own methods below this line.
    // Do not delete this comment.

    // Member 2 - Facility & Booking methods go here
    // Member 3 - Maintenance methods go here
    // Member 4 - Report/analytics read methods go here
}
