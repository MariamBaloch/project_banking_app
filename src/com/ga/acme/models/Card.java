package com.ga.acme.models;

import com.ga.acme.interfaces.ICard;

abstract class Card implements ICard {
    private double withdrawLimitPerDay;
    private double transferLimitPerDay;
    private double depositLimitPerDay;
    private double transferLimitPerDayOwnAccount;
    private double depositLimitPerDayOwnAccount;

    public Card() {
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
}
