package my.edu.utar.model;

import my.edu.utar.util.Constants;

/**
 * User.java
 * Extends Person. Base class for Student and Staff.
 * Applies OOP concept: INHERITANCE, ENCAPSULATION
 *
 * Subclasses: Student, Staff
 */
public abstract class User extends Person {

    // ===================== ATTRIBUTES =====================
    private String role;        // "Student" or "Staff"
    private String faculty;
    private String programme;   // programme (student) or department (staff)

    // ===================== CONSTRUCTOR =====================
    public User(String id, String name, String email, String phone,
                String password, String role, String faculty, String programme) {
        super(id, name, email, phone, password);
        this.role       = role;
        this.faculty    = faculty;
        this.programme  = programme;
    }

    public User() {}

    // ===================== ABSTRACT METHODS =====================
    /**
     * Each user type validates credentials differently.
     * Overrides Person's abstract method.
     */
    @Override
    public abstract boolean validateCredentials(String inputId, String inputPassword);

    // ===================== IMPLEMENTED METHODS =====================
    /**
     * Displays the user's profile info.
     * Can be overridden in subclasses for extra detail.
     * Applies OOP concept: POLYMORPHISM
     */
    @Override
    public void displayProfile() {
        System.out.println("========== MY PROFILE ==========");
        System.out.println("ID         : " + getId());
        System.out.println("Name       : " + getName());
        System.out.println("Email      : " + getEmail());
        System.out.println("Phone      : " + getPhone());
        System.out.println("Role       : " + role);
        System.out.println("Faculty    : " + faculty);
        System.out.println("Programme  : " + programme);
        System.out.println("================================");
    }

    /**
     * Converts this user to a line for saving to users.txt
     * Format: id|name|email|phone|role|faculty|programme|password
     */
    public String toFileString() {
        return getId() + Constants.DELIMITER +
               getName() + Constants.DELIMITER +
               getEmail() + Constants.DELIMITER +
               getPhone() + Constants.DELIMITER +
               role + Constants.DELIMITER +
               faculty + Constants.DELIMITER +
               programme + Constants.DELIMITER +
               getPassword();
    }

    // ===================== GETTERS =====================
    public String getRole()      { return role; }
    public String getFaculty()   { return faculty; }
    public String getProgramme() { return programme; }

    // ===================== SETTERS =====================
    public void setRole(String role)           { this.role = role; }
    public void setFaculty(String faculty)     { this.faculty = faculty; }
    public void setProgramme(String programme) { this.programme = programme; }
}
