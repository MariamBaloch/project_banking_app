package com.ga.acme.models;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionRecord {
    private String id;
    private TransactionType transactionType;
    private AccountType accountType;
    private double balanceBefore;
    private double balanceAfter;
    private double transactionAmount;
    private String toUser = null;
    private AccountType toAccountType = null;
    private Double overdraftAmount = null;
    private CardType cardType;
    private LocalDateTime transactionDate;

    public TransactionRecord(Builder builder) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.transactionType = builder.transactionType;
        this.accountType = builder.accountType;
        this.balanceBefore = builder.balanceBefore;
        this.balanceAfter = builder.balanceAfter;
        this.transactionAmount = builder.transactionAmount;
        this.toUser = builder.toUser;
        this.toAccountType = builder.toAccountType;
        this.overdraftAmount = builder.overdraftAmount;
        this.cardType = builder.cardType;
        this.transactionDate = builder.transactionDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(double balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public double getOverdraftAmount() {
        return overdraftAmount;
    }

    public void setOverdraftAmount(double overdraftAmount) {
        this.overdraftAmount = overdraftAmount;
    }

    public void setOverdraftAmount(Double overdraftAmount) {
        this.overdraftAmount = overdraftAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        String toAcc = toAccountType != null ? toAccountType.getDisplayName() : "null";
        String cardName = cardType != null ? cardType.getDisplayName() : "null";
        return "id=" + id + ";"
                + "transactionType=" + transactionType.getDisplayName() + ";"
                + "accountType=" + accountType.getDisplayName() + ";"
                + "cardType=" + cardName + ";"
                + "balanceBefore=" + balanceBefore + ";"
                + "balanceAfter=" + balanceAfter + ";"
                + "transactionAmount=" + transactionAmount + ";"
                + "toUser=" + toUser + ";"
                + "toAccountType=" + toAcc + ";"
                + "overdraftAmount=" + overdraftAmount + ";"
                + "transactionDate=" + transactionDate;
    }

    public AccountType getToAccountType() {
        return toAccountType;
    }

    public void setToAccountType(AccountType toAccountType) {
        this.toAccountType = toAccountType;
    }

    public static class Builder {
        private String id;
        private TransactionType transactionType;
        private AccountType accountType;
        private double balanceBefore;
        private double balanceAfter;
        private double transactionAmount;
        private String toUser;
        private AccountType toAccountType = null;
        private Double overdraftAmount;
        private CardType cardType;
        private LocalDateTime transactionDate;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setTransactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public Builder setAccountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        public Builder setBalanceBefore(double balanceBefore) {
            this.balanceBefore = balanceBefore;
            return this;
        }

        public Builder setBalanceAfter(double balanceAfter) {
            this.balanceAfter = balanceAfter;
            return this;
        }

        public Builder setTransactionAmount(double transactionAmount) {
            this.transactionAmount = transactionAmount;
            return this;
        }

        public Builder setToUser(String toUser) {
            this.toUser = toUser;
            return this;
        }

        public Builder setToAccountType(AccountType toAccountType) {
            this.toAccountType = toAccountType;
            return this;
        }

        public Builder setOverdraftAmount(Double overdraftAmount) {
            this.overdraftAmount = overdraftAmount;
            return this;
        }

        public Builder setCardType(CardType cardType) {
            this.cardType = cardType;
            return this;
        }

        public Builder setTransactionDate(LocalDateTime transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public TransactionRecord build() {
            return new TransactionRecord(this);
        }
    }
}
