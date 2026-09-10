package com.ga.acme.interfaces;

import com.ga.acme.enums.AccountType;

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

}
