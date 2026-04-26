package my.edu.utar.model;

import my.edu.utar.util.Constants;

public abstract class User extends Person {

    private String role;        
    private String faculty;
    private String programme;  

    public User(String id, String name, String email, String phone,
                String password, String role, String faculty, String programme) {
        super(id, name, email, phone, password);
        this.role       = role;
        this.faculty    = faculty;
        this.programme  = programme;
    }

    public User() {}

    @Override
    public abstract boolean validateCredentials(String inputId, String inputPassword);

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

    public String getRole()      { return role; }
    public String getFaculty()   { return faculty; }
    public String getProgramme() { return programme; }

    public void setRole(String role)           { this.role = role; }
    public void setFaculty(String faculty)     { this.faculty = faculty; }
    public void setProgramme(String programme) { this.programme = programme; }
}
