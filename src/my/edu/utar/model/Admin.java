package my.edu.utar.model;

import my.edu.utar.util.Constants;

/**
 * Admin.java
 * Extends Person directly (not User — admins don't book facilities).
 * Admin accounts are pre-loaded in admin.txt, not self-registerable.
 * Applies OOP concept: INHERITANCE
 */
public class Admin extends Person {

    // ===================== ATTRIBUTES =====================
    private String department;

    // ===================== CONSTRUCTOR =====================
    public Admin(String adminId, String name, String email,
                 String phone, String password, String department) {
        super(adminId, name, email, phone, password);
        this.department = department;
    }

    public Admin() {}

    // ===================== IMPLEMENTED METHODS =====================
    /**
     * Validates admin login credentials.
     */
    @Override
    public boolean validateCredentials(String inputId, String inputPassword) {
        return getId().equals(inputId) && getPassword().equals(inputPassword);
    }

    /**
     * Displays admin profile.
     */
    @Override
    public void displayProfile() {
        System.out.println("========== ADMIN PROFILE ==========");
        System.out.println("Admin ID    : " + getId());
        System.out.println("Name        : " + getName());
        System.out.println("Email       : " + getEmail());
        System.out.println("Phone       : " + getPhone());
        System.out.println("Department  : " + department);
        System.out.println("===================================");
    }

    /**
     * Converts admin to file string for admin.txt
     * Format: adminID|name|email|phone|department|password
     */
    public String toFileString() {
        return getId() + Constants.DELIMITER +
               getName() + Constants.DELIMITER +
               getEmail() + Constants.DELIMITER +
               getPhone() + Constants.DELIMITER +
               department + Constants.DELIMITER +
               getPassword();
    }

    // ===================== GETTERS / SETTERS =====================
    public String getDepartment()              { return department; }
    public void setDepartment(String dept)     { this.department = dept; }
}
