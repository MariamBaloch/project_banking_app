package com.ga.acme.models;

import com.ga.acme.enums.CardType;

public class MastercardTitanium extends Card {
    public MastercardTitanium(String accountId) {
        super(accountId, CardType.MASTERCARD_TITANIUM);
        setWithdrawLimitPerDay(10_000);
        setTransferLimitPerDay(20_000);
        setTransferLimitPerDayOwnAccount(40_000);
        setDepositLimitPerDay(100_000);
        setDepositLimitPerDayOwnAccount(200_000);
    }
}