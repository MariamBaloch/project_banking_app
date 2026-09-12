package com.ga.acme.models;

import com.ga.acme.interfaces.IAccount;

import java.time.LocalDate;

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
    private double dailyWithdrawn;
    private double dailyDeposited;
    private double dailyTransferred;
    private LocalDate lastTransactionDate;

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

    public double getDailyWithdrawn() {
        return dailyWithdrawn;
    }

    public void setDailyWithdrawn(double dailyWithdrawn) {
        this.dailyWithdrawn = dailyWithdrawn;
    }

    public double getDailyDeposited() {
        return dailyDeposited;
    }

    public void setDailyDeposited(double dailyDeposited) {
        this.dailyDeposited = dailyDeposited;
    }

    public double getDailyTransferred() {
        return dailyTransferred;
    }

    public void setDailyTransferred(double dailyTransferred) {
        this.dailyTransferred = dailyTransferred;
    }

    public LocalDate getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDate lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    @Override
    public String toString() {
        return "id=" + id + ";balance=" + balance + ";userId=" + userId + ";type=" + getClass().getSimpleName() +
                ";mastercard=" + mastercard + ";mastercardPlatinum=" + mastercardPlatinum + ";mastercardTitanium=" + mastercardTitanium +
                ";overdrafts=" + overdrafts + ";overdraftAmount=" + overdraftAmount + ";isLocked=" + isLocked +
                ";dailyWithdrawn=" + dailyWithdrawn + ";dailyDeposited=" + dailyDeposited + ";dailyTransferred=" + dailyTransferred + ";lastTransactionDate=" + lastTransactionDate;
    }
}
