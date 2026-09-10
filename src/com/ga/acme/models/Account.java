package com.ga.acme.models;

import com.ga.acme.interfaces.IAccount;

abstract class Account implements IAccount {
    private String id;
    private double balance;
    private String userId;
    private boolean mastercard;
    private boolean mastercardPlatinum;
    private boolean mastercardTitanium;
    private int overdrafts = 0;
    private double overdraftAmount;
    private boolean isLocked;

    public Account() {
    }

    public Account(String id, String userId, boolean mastercard, boolean mastercardPlatinum, boolean mastercardTitanium) {
        this.id = id;
        this.userId = userId;
        this.mastercard = mastercard;
        this.mastercardPlatinum = mastercardPlatinum;
        this.mastercardTitanium = mastercardTitanium;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) {
        balance -= amount;
    }

    public void transferFunds(double amount, IAccount account) {
        this.withdraw(amount);
        account.deposit(amount);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isMastercard() {
        return mastercard;
    }

    public void setMastercard(boolean mastercard) {
        this.mastercard = mastercard;
    }

    public boolean isMastercardTitanium() {
        return mastercardTitanium;
    }

    public void setMastercardTitanium(boolean mastercardTitanium) {
        this.mastercardTitanium = mastercardTitanium;
    }

    public boolean isMastercardPlatinum() {
        return mastercardPlatinum;
    }

    public void setMastercardPlatinum(boolean mastercardPlatinum) {
        this.mastercardPlatinum = mastercardPlatinum;
    }

    public int getOverdrafts() {
        return overdrafts;
    }

    public void setOverdrafts(int overdrafts) {
        this.overdrafts = overdrafts;
    }

    public double getOverdraftAmount() {
        return overdraftAmount;
    }

    public void setOverdraftAmount(double overdraftAmount) {
        this.overdraftAmount = overdraftAmount;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    @Override
    public String toString() {
        return id + "," + balance + "," + userId + "," + getClass().getSimpleName() + "," + mastercard + "," + mastercardPlatinum + "," + mastercardTitanium;
    }
}
