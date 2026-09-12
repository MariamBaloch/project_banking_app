package com.ga.acme.interfaces;

import java.time.LocalDate;

public interface ICard {
    String getId();

    void setId(String id);

    String getAccountId();

    void setAccountId(String accountId);

    double getWithdrawLimitPerDay();

    void setWithdrawLimitPerDay(double withdrawLimitPerDay);

    double getDepositLimitPerDayOwnAccount();

    void setDepositLimitPerDayOwnAccount(double depositLimitPerDayOwnAccount);

    double getTransferLimitPerDayOwnAccount();

    void setTransferLimitPerDayOwnAccount(double transferLimitPerDayOwnAccount);

    double getDepositLimitPerDay();

    void setDepositLimitPerDay(double depositLimitPerDay);

    double getTransferLimitPerDay();

    void setTransferLimitPerDay(double transferLimitPerDay);

    double getDailyWithdrawn();

    void setDailyWithdrawn(double dailyWithdrawn);

    double getDailyDeposited();

    void setDailyDeposited(double dailyDeposited);

    double getDailyTransferred();

    void setDailyTransferred(double dailyTransferred);

    LocalDate getLastTransactionDate();

    void setLastTransactionDate(LocalDate lastTransactionDate);

}