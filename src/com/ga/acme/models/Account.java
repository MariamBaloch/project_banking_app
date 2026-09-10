package com.ga.acme.models;

import com.ga.acme.interfaces.IAccount;

abstract class Account implements IAccount {
    private String id;
    private double balance;
    private String userId;

    public Account() {
    }

    public Account(String id, String userId) {
        this.id = id;
        this.userId = userId;
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

    @Override
    public String toString() {
        return id + "," + balance + "," + userId + "," + getClass().getSimpleName();
    }
}
