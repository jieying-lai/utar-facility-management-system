package my.edu.utar.model;

import my.edu.utar.util.Constants;

public class Admin extends Person {

    private String department;

    public Admin(String adminId, String name, String email,
                 String phone, String password, String department) {
        super(adminId, name, email, phone, password);
        this.department = department;
    }

    public Admin() {}

    @Override
    public boolean validateCredentials(String inputId, String inputPassword) {
        return getId().equals(inputId) && getPassword().equals(inputPassword);
    }

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

    public String toFileString() {
        return getId() + Constants.DELIMITER +
               getName() + Constants.DELIMITER +
               getEmail() + Constants.DELIMITER +
               getPhone() + Constants.DELIMITER +
               department + Constants.DELIMITER +
               getPassword();
    }

    public String getDepartment()              { return department; }
    public void setDepartment(String dept)     { this.department = dept; }
}
