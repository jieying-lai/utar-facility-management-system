package my.edu.utar.model;

import my.edu.utar.util.Constants;

//Represents a staff user. Email must end with @utar.edu.my.
public class Staff extends User {

    public Staff(String staffId, String name, String email, String phone,
                 String password, String faculty, String department) {
        super(staffId, name, email, phone, password,
              Constants.ROLE_STAFF, faculty, department);
    }

    public Staff() {}
    
 // Staff reuses the 'programme' field from User to store department
    public String getDepartment() {
        return getProgramme(); 
    }

    public void setDepartment(String d) {
        setProgramme(d);
    }

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
        System.out.println("Department  : " + getDepartment()); // reused as department
        System.out.println("===================================");
    }
}
