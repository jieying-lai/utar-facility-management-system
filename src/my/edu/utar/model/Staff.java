package my.edu.utar.model;

import my.edu.utar.util.Constants;

/**
 * Staff.java
 * Extends User. Represents a staff member.
 * Applies OOP concept: INHERITANCE, POLYMORPHISM
 */
public class Staff extends User {

    // ===================== CONSTRUCTOR =====================
    public Staff(String staffId, String name, String email, String phone,
                 String password, String faculty, String department) {
        super(staffId, name, email, phone, password,
              Constants.ROLE_STAFF, faculty, department);
    }

    public Staff() {}

    // ===================== IMPLEMENTED METHODS =====================
    /**
     * Validates login credentials for staff.
     */
    @Override
    public boolean validateCredentials(String inputId, String inputPassword) {
        return getId().equals(inputId) && getPassword().equals(inputPassword);
    }

    /**
     * Overrides displayProfile to show staff-specific labels.
     */
    @Override
    public void displayProfile() {
        System.out.println("========== STAFF PROFILE ==========");
        System.out.println("Staff ID    : " + getId());
        System.out.println("Name        : " + getName());
        System.out.println("Email       : " + getEmail());
        System.out.println("Phone       : " + getPhone());
        System.out.println("Faculty     : " + getFaculty());
        System.out.println("Department  : " + getProgramme()); // reused as department
        System.out.println("===================================");
    }
}
