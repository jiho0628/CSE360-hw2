package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    // private final String _userName = "";
    private final SimpleStringProperty userName;
    private SimpleStringProperty password;
    private SimpleStringProperty roles;
    private SimpleStringProperty name;
    private SimpleStringProperty email;

    // Constructor to initialize a new User object with userName, password, and role.
    public User( String userName, String password, String role, String name, String email) {
        this.userName = new SimpleStringProperty(userName);
        this.password =new SimpleStringProperty(password);
        this.roles = new SimpleStringProperty(role);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
    }

    public String getUserName() { return this.userName.get(); }
    public void setUserName(String newUserName) { this.userName.set(newUserName); }
    public final StringProperty userNameProperty() { return userName; }
    
    public String getPassword() { return this.password.get(); }
    public void setPassword(String newPassword) { this.password.set(newPassword); }
    public StringProperty passwordProperty() {return password;}
    
    public String getName() { return this.name.get(); }
    public void setName(String newName) { this.name.set(newName); }
    public StringProperty nameProperty() {return name;}
    
    public String getEmail() { return this.email.get(); }
    public void setEmail(String newEmail) {  this.email.set(newEmail);}
    public StringProperty emailProperty() { return email;}
    
    public String getRoles() { return this.roles.get(); }
    public void setRole(String newRoles) {this.roles.set(newRoles);}
    public StringProperty rolesProperty() {return roles;}
    
    
}
