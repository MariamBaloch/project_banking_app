package com.ga.acme;
import com.ga.acme.enums.Roles;
import java.time.LocalTime;

abstract class User implements IUser {
    private String id;
    private String name;
    private String hashedPassword;
    private Roles role;

    private int loginAttempts = 0;
    private LocalTime lockedTime;

    public User() {}

    public User(String id, String name, String password, Roles role) {
        this.id = id;
        this.name = name;
        this.hashedPassword = Auth.encryptPassword(password);
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public LocalTime getLockedTime() {
        return lockedTime;
    }

    public void setLockedTime(LocalTime lockedUntil) {
        this.lockedTime = lockedUntil;
    }


    @Override
    public String toString() {
        return id + "," + name + "," + hashedPassword + "," + role + "," + loginAttempts + "," + lockedTime;
    }
}
