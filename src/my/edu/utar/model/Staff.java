package my.edu.utar.model;

import my.edu.utar.util.Constants;

public class Staff extends User {

    public Staff(String staffId, String name, String email, String phone,
                 String password, String faculty, String department) {
        super(staffId, name, email, phone, password,
              Constants.ROLE_STAFF, faculty, department);
    }

    public Staff() {}

    @Override
    public boolean validateCredentials(String inputId, String inputPassword) {
        return getId().equals(inputId) && getPassword().equals(inputPassword);
    }

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
