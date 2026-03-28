package my.edu.utar.model;

public abstract class Person {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;

    public Person(String id, String name, String email, String phone, String password) {
        this.id       = id;
        this.name     = name.toUpperCase();
        this.email    = email.toLowerCase();
        this.phone    = phone;
        this.password = password;
    }

    public Person() {}

    public abstract void displayProfile();

    public abstract boolean validateCredentials(String inputId, String inputPassword);

    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public String getPassword() { return password; }

    public void setId(String id)           { this.id = id; }
    public void setName(String name)       { this.name = name.toUpperCase(); }
    public void setEmail(String email)     { this.email = email.toLowerCase(); }
    public void setPhone(String phone)     { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Email: " + email + " | Phone: " + phone;
    }
}
