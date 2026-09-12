package com.ga.acme.models;

public class MastercardTitanium extends Card {
    public MastercardTitanium(String accountId) {
        super(accountId);
        setWithdrawLimitPerDay(10_000);
        setTransferLimitPerDay(20_000);
        setTransferLimitPerDayOwnAccount(40_000);
        setDepositLimitPerDay(100_000);
        setDepositLimitPerDayOwnAccount(200_000);
    }
}