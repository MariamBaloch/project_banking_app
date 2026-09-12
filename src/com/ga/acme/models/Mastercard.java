package com.ga.acme.models;

public class Mastercard extends Card {
    public Mastercard(String accountId) {
        super(accountId);
        setWithdrawLimitPerDay(5_000);
        setTransferLimitPerDay(10_000);
        setTransferLimitPerDayOwnAccount(20_000);
        setDepositLimitPerDay(100_000);
        setDepositLimitPerDayOwnAccount(200_000);
    }
}