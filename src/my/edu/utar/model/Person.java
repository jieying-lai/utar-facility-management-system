package my.edu.utar.model;

/**
 * Person.java
 * Abstract base class for all people in the system.
 * Applies OOP concept: ABSTRACTION and ENCAPSULATION.
 *
 * Subclasses: User (-> Student, Staff), Admin
 */
public abstract class Person {

    // ===================== ATTRIBUTES =====================
    private String id;
    private String name;       // stored in UPPERCASE
    private String email;      // stored in lowercase
    private String phone;
    private String password;   // stored as hashed value

    // ===================== CONSTRUCTOR =====================
    public Person(String id, String name, String email, String phone, String password) {
        this.id       = id;
        this.name     = name.toUpperCase();
        this.email    = email.toLowerCase();
        this.phone    = phone;
        this.password = password;
    }

    // Empty constructor for subclass use
    public Person() {}

    // ===================== ABSTRACT METHODS =====================
    /**
     * Each subclass must implement its own display profile format.
     * Applies OOP concept: ABSTRACTION + POLYMORPHISM
     */
    public abstract void displayProfile();

    /**
     * Each subclass must implement credential validation.
     */
    public abstract boolean validateCredentials(String inputId, String inputPassword);

    // ===================== GETTERS =====================
    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public String getPassword() { return password; }

    // ===================== SETTERS =====================
    public void setId(String id)           { this.id = id; }
    public void setName(String name)       { this.name = name.toUpperCase(); }
    public void setEmail(String email)     { this.email = email.toLowerCase(); }
    public void setPhone(String phone)     { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }

    // ===================== UTILITY =====================
    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Email: " + email + " | Phone: " + phone;
    }
}
