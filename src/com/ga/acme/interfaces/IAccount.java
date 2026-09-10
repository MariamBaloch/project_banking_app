package com.ga.acme.interfaces;

public interface IAccount {

    String getId();

    void setId(String id);

    double getBalance();

    void setBalance(double balance);

    void withdraw(double amount);

    void deposit(double amount);

    void transferFunds(double amount, IAccount account);

    String getUserId();

    void setUserId(String userId);

    boolean isMastercard();

    void setMastercard(boolean mastercard);

    boolean isMastercardTitanium();

    void setMastercardTitanium(boolean mastercardTitanium);

    boolean isMastercardPlatinum();

    void setMastercardPlatinum(boolean mastercardPlatinum);

}
