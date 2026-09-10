package com.ga.acme.interfaces;

public interface ICard {
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
}
