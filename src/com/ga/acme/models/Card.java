package com.ga.acme.models;

import com.ga.acme.interfaces.ICard;

import java.time.LocalDate;
import java.util.UUID;

abstract class Card implements ICard {
    private String id;
    private String accountId;
    private double withdrawLimitPerDay;
    private double transferLimitPerDay;
    private double depositLimitPerDay;
    private double transferLimitPerDayOwnAccount;
    private double depositLimitPerDayOwnAccount;
    private double dailyWithdrawn;
    private double dailyDeposited;
    private double dailyTransferred;
    private LocalDate lastTransactionDate;

    public Card(String accountId) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.accountId = accountId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getWithdrawLimitPerDay() {
        return withdrawLimitPerDay;
    }

    public void setWithdrawLimitPerDay(double withdrawLimitPerDay) {
        this.withdrawLimitPerDay = withdrawLimitPerDay;
    }

    public double getDepositLimitPerDayOwnAccount() {
        return depositLimitPerDayOwnAccount;
    }

    public void setDepositLimitPerDayOwnAccount(double depositLimitPerDayOwnAccount) {
        this.depositLimitPerDayOwnAccount = depositLimitPerDayOwnAccount;
    }

    public double getTransferLimitPerDayOwnAccount() {
        return transferLimitPerDayOwnAccount;
    }

    public void setTransferLimitPerDayOwnAccount(double transferLimitPerDayOwnAccount) {
        this.transferLimitPerDayOwnAccount = transferLimitPerDayOwnAccount;
    }

    public double getDepositLimitPerDay() {
        return depositLimitPerDay;
    }

    public void setDepositLimitPerDay(double depositLimitPerDay) {
        this.depositLimitPerDay = depositLimitPerDay;
    }

    public double getTransferLimitPerDay() {
        return transferLimitPerDay;
    }

    public void setTransferLimitPerDay(double transferLimitPerDay) {
        this.transferLimitPerDay = transferLimitPerDay;
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
        return "id=" + id + ";" + "accountId=" + accountId + ";" + "type=" + getClass().getSimpleName().toUpperCase() + ";" + "dailyWithdrawn=" + dailyWithdrawn + ";" + "dailyDeposited=" + dailyDeposited + ";" + "dailyTransferred=" + dailyTransferred + ";" + "lastTransactionDate=" + (lastTransactionDate == null ? "null" : lastTransactionDate);
    }
}