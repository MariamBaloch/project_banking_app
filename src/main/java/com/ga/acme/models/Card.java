package com.ga.acme.models;

import com.ga.acme.enums.CardType;
import com.ga.acme.interfaces.ICard;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

public abstract class Card implements ICard {
    private String id;
    private String accountId;
    private CardType cardType;
    private double withdrawLimitPerDay;
    private double transferLimitPerDay;
    private double depositLimitPerDay;
    private double transferLimitPerDayOwnAccount;
    private double depositLimitPerDayOwnAccount;
    private double dailyWithdrawn;
    private double dailyDeposited;
    private double dailyTransferred;
    private double dailyTransferredOwnAccount;
    private double dailyDepositedOwnAccount;
    private LocalDate lastTransactionDate;

    public Card(String accountId, CardType type) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.accountId = accountId;
        this.cardType = type;
    }

    public static ICard mapToCardObject(Map<String, String> values) {
        ICard card = null;
        String type = values.get("type");
        String accountId = values.get("accountId");

        if (type.equalsIgnoreCase(CardType.MASTERCARD.toString())) {
            card = new Mastercard(accountId);
        } else if (type.equalsIgnoreCase(CardType.MASTERCARD_PLATINUM.getDisplayName())) {
            card = new MastercardPlatinum(accountId);
        } else if (type.equalsIgnoreCase(CardType.MASTERCARD_TITANIUM.getDisplayName())) {
            card = new MastercardTitanium(accountId);
        }
        card.setId(values.get("id"));
        card.setAccountId(values.get("accountId"));
        card.setDailyWithdrawn(Double.parseDouble(values.get("dailyWithdrawn")));
        card.setDailyDeposited(Double.parseDouble(values.get("dailyDeposited")));
        card.setDailyTransferred(Double.parseDouble(values.get("dailyTransferred")));
        card.setDailyDepositedOwnAccount(Double.parseDouble(values.get("dailyDepositedOwnAccount")));
        card.setDailyTransferredOwnAccount(Double.parseDouble(values.get("dailyTransferredOwnAccount")));
        String lastTransactionDate = values.get("lastTransactionDate");
        if (!lastTransactionDate.isEmpty() && !lastTransactionDate.equals("null")) {
            card.setLastTransactionDate(LocalDate.parse(lastTransactionDate));
        } else {
            card.setLastTransactionDate(null);
        }

        return card;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public double getDailyWithdrawn() {
        return dailyWithdrawn;
    }

    public void setDailyWithdrawn(double dailyWithdrawn) {
        this.dailyWithdrawn = dailyWithdrawn;
    }

    public double getDailyDeposited() {
        return dailyDeposited;
    }

    public void setDailyDeposited(double dailyDeposited) {
        this.dailyDeposited = dailyDeposited;
    }

    public double getDailyTransferred() {
        return dailyTransferred;
    }

    public void setDailyTransferred(double dailyTransferred) {
        this.dailyTransferred = dailyTransferred;
    }

    public LocalDate getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDate lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    public double getDailyDepositedOwnAccount() {
        return dailyDepositedOwnAccount;
    }

    public void setDailyDepositedOwnAccount(double dailyDepositedOwnAccount) {
        this.dailyDepositedOwnAccount = dailyDepositedOwnAccount;
    }

    public double getDailyTransferredOwnAccount() {
        return dailyTransferredOwnAccount;
    }

    public void setDailyTransferredOwnAccount(double dailyTransferredOwnAccount) {
        this.dailyTransferredOwnAccount = dailyTransferredOwnAccount;
    }

    @Override
    public String toString() {
        return "id=" + id + ";"
                + "accountId=" + accountId + ";"
                + "type=" + cardType.getDisplayName() + ";"
                + "dailyWithdrawn=" + dailyWithdrawn + ";"
                + "dailyDeposited=" + dailyDeposited + ";"
                + "dailyTransferred=" + dailyTransferred + ";"
                + "dailyTransferredOwnAccount=" + dailyTransferredOwnAccount + ";"
                + "dailyDepositedOwnAccount=" + dailyDepositedOwnAccount + ";"
                + "lastTransactionDate=" + (lastTransactionDate == null ? "null" : lastTransactionDate);
    }
}