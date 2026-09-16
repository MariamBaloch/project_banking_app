package com.ga.acme.models;

import com.ga.acme.enums.CardType;

public class MastercardPlatinum extends Card {
    public MastercardPlatinum(String accountId) {
        super(accountId, CardType.MASTERCARD_PLATINUM);
        setWithdrawLimitPerDay(20_000);
        setTransferLimitPerDay(40_000);
        setTransferLimitPerDayOwnAccount(80_000);
        setDepositLimitPerDay(100_000);
        setDepositLimitPerDayOwnAccount(200_000);
    }
}