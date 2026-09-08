package com.ga.acme;

import com.ga.acme.enums.Roles;
import java.util.Date;

abstract class User implements IUser {
    private String id;
    private String name;
    private String hashedPassword;
    private Roles role;

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
        this.hashedPassword = Auth.encryptPassword(hashedPassword);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return id + "," + name + "," + hashedPassword + "," + role;
    }
}
