package com.ga.acme.models;

import com.ga.acme.interfaces.IAccount;

import java.util.UUID;

abstract class Account implements IAccount {
    private String id;
    private double balance;
    private String userId;
    private Mastercard mastercard;
    private MastercardPlatinum mastercardPlatinum;
    private MastercardTitanium mastercardTitanium;
    private int overdrafts = 0;
    private double overdraftAmount;
    private boolean isLocked;

    public Account() {
    }

    public Account(String userId, Mastercard mastercard, MastercardPlatinum mastercardPlatinum, MastercardTitanium mastercardTitanium) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
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

    public Mastercard getMastercard() {
        return mastercard;
    }

    public void setMastercard(Mastercard mastercard) {
        this.mastercard = mastercard;
    }

    public MastercardTitanium getMastercardTitanium() {
        return mastercardTitanium;
    }

    public void setMastercardTitanium(MastercardTitanium mastercardTitanium) {
        this.mastercardTitanium = mastercardTitanium;
    }

    public MastercardPlatinum getMastercardPlatinum() {
        return mastercardPlatinum;
    }

    public void setMastercardPlatinum(MastercardPlatinum mastercardPlatinum) {
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
        String mastercardId = mastercard != null ? mastercard.getId() : null;
        String mastercardPlatinumId = mastercardPlatinum != null ? mastercardPlatinum.getId() : null;
        String mastercardTitaniumId = mastercardTitanium != null ? mastercardTitanium.getId() : null;

        return "id=" + id + ";" + "balance=" + balance + ";" + "userId=" + userId + ";" + "type=" + getClass().getSimpleName() + ";" + "mastercardId=" + mastercardId + ";" + "mastercardPlatinumId=" + mastercardPlatinumId + ";" + "mastercardTitaniumId=" + mastercardTitaniumId + ";" + "overdrafts=" + overdrafts + ";" + "overdraftAmount=" + overdraftAmount + ";" + "isLocked=" + isLocked;
    }
}