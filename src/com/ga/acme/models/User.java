package com.ga.acme.models;

import com.ga.acme.enums.Roles;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.services.AccountService;
import com.ga.acme.services.AuthService;

import java.time.LocalTime;
import java.util.Map;

public abstract class User implements IUser {
    SavingsAccount savingsAccount;
    CheckingAccount checkingAccount;
    private String id;
    private String name;
    private String hashedPassword;
    private Roles role;
    private int loginAttempts = 0;
    private LocalTime lockedUntil;
    private boolean isLoggedIn = false;

    public User() {
    }

    public User(String id, String name, String password, Roles role) {
        this.id = id;
        this.name = name;
        this.hashedPassword = AuthService.encryptPassword(password);
        this.role = role;
    }

    public static IUser mapToUserObject(Map<String, String> values) {
        IUser user = null;

        if (values.get("role").equalsIgnoreCase("customer")) {
            user = new Customer();
        } else if (values.get("role").equalsIgnoreCase("banker")) {
            user = new Banker();
        }
        user.setId(values.get("id"));
        user.setName(values.get("name"));
        user.setHashedPassword(values.get("hashedPassword"));
        user.setRole(Roles.valueOf(values.get("role")));
        user.setLoginAttempts(Integer.parseInt(values.get("loginAttempts")));
        user.setLockedUntil(!values.get("lockedUntil").equals("null") ? LocalTime.parse(values.get("lockedUntil")) : null);
        user.setIsLoggedIn(Boolean.parseBoolean(values.get("isLoggedIn")));
        if (!values.get("checkingId").equals("null")) {
            user.setCheckingAccount((CheckingAccount) AccountService.getAccountById(values.get("checkingId")));
        }
        if (!values.get("savingId").equals("null")) {
            user.setSavingsAccount((SavingsAccount) AccountService.getAccountById(values.get("savingId")));
        }
        return user;
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

        return "id=" + id + ";name=" + name + ";hashedPassword=" + hashedPassword + ";role=" + role + ";loginAttempts=" + loginAttempts + ";lockedUntil=" + lockedUntil + ";isLoggedIn=" + isLoggedIn + ";checkingId=" + checkingId + ";savingId=" + savingId;
    }
}