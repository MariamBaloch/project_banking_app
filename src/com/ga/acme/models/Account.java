package com.ga.acme.models;

import com.ga.acme.enums.AccountType;

import java.util.Map;
import java.util.UUID;

import static com.ga.acme.services.CardService.getCardById;

public class Account {
    private String id;
    private double balance;
    private String userId;
    private AccountType accountType;
    private Mastercard mastercard;
    private MastercardPlatinum mastercardPlatinum;
    private MastercardTitanium mastercardTitanium;
    private int overdrafts = 0;
    private double overdraftAmount;
    private boolean isLocked;

    public Account() {
    }

    public Account(String userId, AccountType accountType, Mastercard mastercard, MastercardPlatinum mastercardPlatinum, MastercardTitanium mastercardTitanium) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.accountType = accountType;
        this.userId = userId;
        this.mastercard = mastercard;
        this.mastercardPlatinum = mastercardPlatinum;
        this.mastercardTitanium = mastercardTitanium;
    }

    public static Account mapToAccountObject(Map<String, String> values) {
        Account acc = new Account();

        acc.setId(values.get("id"));
        acc.setBalance(Double.parseDouble(values.get("balance")));
        acc.setUserId(values.get("userId"));
        if (values.get("accountType").equalsIgnoreCase(AccountType.CHECKING_ACCOUNT.getDisplayName())) {
            acc.setAccountType(AccountType.CHECKING_ACCOUNT);
        } else if (values.get("accountType").equalsIgnoreCase(AccountType.SAVINGS_ACCOUNT.getDisplayName())) {
            acc.setAccountType(AccountType.SAVINGS_ACCOUNT);
        }
        if (!values.get("mastercardId").equals("null")) {
            acc.setMastercard((Mastercard) getCardById(values.get("mastercardId")));
        }
        if (!values.get("mastercardPlatinumId").equals("null")) {
            acc.setMastercardPlatinum((MastercardPlatinum) getCardById(values.get("mastercardPlatinumId")));
        }
        if (!values.get("mastercardTitaniumId").equals("null")) {
            acc.setMastercardTitanium((MastercardTitanium) getCardById(values.get("mastercardTitaniumId")));
        }
        acc.setOverdrafts(Integer.parseInt(values.get("overdrafts")));
        acc.setOverdraftAmount(Double.parseDouble(values.get("overdraftAmount")));
        acc.setLocked(Boolean.parseBoolean(values.get("isLocked")));

        return acc;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
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

    public void transferFunds(double amount, Account account) {
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

        return "id=" + id + ";"
                + "balance=" + balance + ";"
                + "userId=" + userId + ";"
                + "accountType=" + accountType.getDisplayName() + ";"
                + "mastercardId=" + mastercardId + ";"
                + "mastercardPlatinumId=" + mastercardPlatinumId + ";"
                + "mastercardTitaniumId=" + mastercardTitaniumId + ";"
                + "overdrafts=" + overdrafts + ";"
                + "overdraftAmount=" + overdraftAmount + ";"
                + "isLocked=" + isLocked;
    }
}