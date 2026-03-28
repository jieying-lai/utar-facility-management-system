package my.edu.utar.model;

import my.edu.utar.util.Constants;

public class Student extends User {

    public Student(String studentId, String name, String email, String phone,
                   String password, String faculty, String programme) {
        super(studentId, name, email, phone, password,
              Constants.ROLE_STUDENT, faculty, programme);
    }

    public Student() {}

    @Override
    public boolean validateCredentials(String inputId, String inputPassword) {
        return getId().equals(inputId) && getPassword().equals(inputPassword);
    }

    @Override
    public void displayProfile() {
        System.out.println("========== STUDENT PROFILE ==========");
        System.out.println("Student ID  : " + getId());
        System.out.println("Name        : " + getName());
        System.out.println("Email       : " + getEmail());
        System.out.println("Phone       : " + getPhone());
        System.out.println("Faculty     : " + getFaculty());
        System.out.println("Programme   : " + getProgramme());
        System.out.println("=====================================");
    }
}
