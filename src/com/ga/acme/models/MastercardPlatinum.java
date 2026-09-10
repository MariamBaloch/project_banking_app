package com.ga.acme.models;

public class MastercardPlatinum extends Card {
    public MastercardPlatinum() {
        setWithdrawLimitPerDay(20_000);
        setTransferLimitPerDay(40_000);
        setTransferLimitPerDayOwnAccount(80_000);
        setDepositLimitPerDay(100_000);
        setDepositLimitPerDayOwnAccount(200_000);
    }
}
