package com.ga.acme.models;

import com.ga.acme.controllers.Auth;
import com.ga.acme.enums.Roles;
import com.ga.acme.interfaces.IUser;

import java.time.LocalTime;

abstract class User implements IUser {
    SavingsAccount savingsAccount;
    CheckingAccount checkingAccount;
    private String id;
    private String name;
    private String hashedPassword;
    private Roles role;
    private int loginAttempts = 0;
    private LocalTime lockedUntil;
    private boolean isLoggedIn = false;
    private boolean isActive;

    public User() {
    }

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

    public LocalTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public SavingsAccount getSavingsAccount() {
        return savingsAccount;
    }

    public void setSavingsAccount(SavingsAccount savingsAccount) {
        this.savingsAccount = savingsAccount;
    }

    public CheckingAccount getCheckingAccount() {
        return checkingAccount;
    }

    public void setCheckingAccount(CheckingAccount checkingAccount) {
        this.checkingAccount = checkingAccount;
    }

    @Override
    public String toString() {
        String savingId = savingsAccount != null ? savingsAccount.getId() : null;
        String checkingId = checkingAccount != null ? checkingAccount.getId() : null;

        return id + "," + name + "," + hashedPassword + "," + role + "," + loginAttempts + "," + lockedUntil + "," + isLoggedIn + "," + checkingId + "," + savingId + "," + isActive;
    }
}
