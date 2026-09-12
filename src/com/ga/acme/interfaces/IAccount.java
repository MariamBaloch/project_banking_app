package com.ga.acme.interfaces;

import java.time.LocalDate;

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

    int getOverdrafts();

    void setOverdrafts(int overdrafts);

    double getOverdraftAmount();

    void setOverdraftAmount(double overdraftAmount);

    boolean isLocked();

    void setLocked(boolean locked);

    double getDailyWithdrawn();

    void setDailyWithdrawn(double dailyWithdrawn);

    double getDailyDeposited();

    void setDailyDeposited(double dailyDeposited);

    double getDailyTransferred();

    void setDailyTransferred(double dailyTransferred);

    LocalDate getLastTransactionDate();

    void setLastTransactionDate(LocalDate lastTransactionDate);

}
