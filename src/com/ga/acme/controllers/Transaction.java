package com.ga.acme.controllers;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.exceptions.*;
import com.ga.acme.interfaces.IAccount;
import com.ga.acme.interfaces.ICard;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.util.FileHandler;

import java.time.LocalDate;

import static com.ga.acme.controllers.Account.getVerifiedAccount;
import static com.ga.acme.controllers.Card.getCardByTypeForAccount;

public class Transaction {

    public static final int OVERDRAFT_LIMIT = 2;
    public static final double OVERDRAFT_AMOUNT = 35;


    public static void initialChecks(IUser user, AccountType accountType, CardType cardType) throws AccountNotLoggedInException, AccountTypeNotSupportedException, RecordNotFoundException, CardNotSupportedException {
        if (!user.getIsLoggedIn()) {
            throw new AccountNotLoggedInException();
        }
        if (accountType != AccountType.CHECKINGACCOUNT && accountType != AccountType.SAVINGSACCOUNT) {
            throw new AccountTypeNotSupportedException();
        }

        IAccount account = accountType == AccountType.CHECKINGACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();

        if (account == null) {
            String outputAccountType = accountType == AccountType.SAVINGSACCOUNT ? "savings account" : "checking account";
            throw new RecordNotFoundException("No " + outputAccountType + " found for user: " + user.getName());
        }

        boolean isCardSupported = switch (cardType) {
            case MASTERCARD -> account.getMastercard() != null;
            case MASTERCARDPLATINUM -> account.getMastercardPlatinum() != null;
            case MASTERCARDTITANIUM -> account.getMastercardTitanium() != null;
        };

        if (!isCardSupported) {
            throw new CardNotSupportedException();
        }
    }

    public static void withdraw(String userId, double amount, AccountType accountType, CardType cardType) {
        IAccount account = getVerifiedAccount(userId, accountType, cardType);
        if (account == null) {
            return;
        }
        ICard card = getCardByTypeForAccount(account, cardType);
        try {
            handleDailyLimits(card, amount, card.getDailyWithdrawn(), card.getWithdrawLimitPerDay(), "Withdraw");
            if (account.isLocked()) {
                throw new AccountLockedException(
                        "Account is locked due to reaching overdraft limit. Pay off your negative balance and $" + account.getOverdraftAmount() + " overdraft fee to unlock.");
            }
            if (account.getBalance() < 0 && amount > 100) {
                throw new WithdrawLimitException(
                        "Negative balance, withdrawals are limited to $100. Please enter a lower amount.");
            }
            account.withdraw(amount);
            card.setDailyWithdrawn(card.getDailyWithdrawn() + amount);

            if (account.getBalance() < 0) {
                account.setOverdraftAmount(account.getOverdraftAmount() + OVERDRAFT_AMOUNT);
                account.setOverdrafts(account.getOverdrafts() + 1);
                System.out.println("Negative balance, overdraft fee of $" + OVERDRAFT_AMOUNT + " charged. " + "Total overdraft fees owed: $" + account.getOverdraftAmount());
                if (account.getOverdrafts() >= OVERDRAFT_LIMIT) {
                    account.setLocked(true);
                    System.out.println("Account has been locked due to reaching overdraft limit. " + "Resolve your negative balance and pay $" + account.getOverdraftAmount() + " to unlock account.");
                }
            }
            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
            FileHandler.updateLineInFile(FilePath.CARDS.getPath(), card.getId(), card.toString());
        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void resolveOverdraft(String userId, double paymentAmount, AccountType accountType, CardType cardType) {
        IAccount account = getVerifiedAccount(userId, accountType, cardType);
        if (account == null) {
            return;
        }
        try {
            double negativeBalance = account.getBalance() < 0 ? Math.abs(account.getBalance()) : 0;
            double totalDebt = negativeBalance + account.getOverdraftAmount();

            if (totalDebt == 0) {
                System.out.println("No overdraft resolution needed.");
                return;
            }

            if (paymentAmount < totalDebt) {
                throw new InsufficientAmountException("Insufficient amount provided. Amount owed $" + totalDebt + " (negative balance: $" + negativeBalance + " + overdraft fees: $" + account.getOverdraftAmount() + ")."
                );
            }

            double surplus = paymentAmount - totalDebt;
            account.setBalance(surplus);
            account.setOverdraftAmount(0);
            account.setOverdrafts(0);
            account.setLocked(false);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
            System.out.println("Overdraft resolved. Account unlocked. New balance: " + account.getBalance());

        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void transfer(String userId, double amount, AccountType fromAccountType, AccountType toAccountType, String toUserId, CardType cardType) {
        IAccount fromAccount = getVerifiedAccount(userId, fromAccountType, cardType);
        if (fromAccount == null) {
            return;
        }
        boolean ownTransfer = userId.equals(toUserId);
        if (ownTransfer && toAccountType.equals(fromAccountType)) {
            throw new IllegalArgumentException("Transferring to same account type for same user not allowed");
        }
        IUser toUser = Auth.getUserById(toUserId);

        IAccount toAccount = AccountType.CHECKINGACCOUNT.equals(toAccountType) ? toUser.getCheckingAccount() : toUser.getSavingsAccount();
        try {
            if (toAccount == null) {
                String outputAccountType = toAccountType == AccountType.SAVINGSACCOUNT ? "savings account" : "checking account";
                throw new RecordNotFoundException("No " + outputAccountType + " found for user: " + toUser.getName());
            }

            ICard card = getCardByTypeForAccount(fromAccount, cardType);

            double limit = ownTransfer ? card.getTransferLimitPerDayOwnAccount() : card.getTransferLimitPerDay();
            String transactionType = ownTransfer ? "Transfer to own account" : "Transfer";
            handleDailyLimits(card, amount, card.getDailyTransferred(), limit, transactionType);

            fromAccount.transferFunds(amount, toAccount);
            card.setDailyTransferred(card.getDailyTransferred() + amount);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), fromAccount.getId(), fromAccount.toString());
            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), toAccount.getId(), toAccount.toString());
            FileHandler.updateLineInFile(FilePath.CARDS.getPath(), card.getId(), card.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deposit(String userId, double amount, AccountType accountType, CardType cardType) {
        IAccount account = getVerifiedAccount(userId, accountType, cardType);
        if (account == null) {
            return;
        }
        ICard card = getCardByTypeForAccount(account, cardType);
        try {
            handleDailyLimits(card, amount, card.getDailyDeposited(),
                    card.getDepositLimitPerDayOwnAccount(), "Deposit");
            account.deposit(amount);
            card.setDailyDeposited(card.getDailyDeposited() + amount);
            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
            FileHandler.updateLineInFile(FilePath.CARDS.getPath(), card.getId(), card.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private static void handleDailyLimits(ICard card, double amount, double dailyAmount, double limit, String transactionType) throws DailyLimitExceededException {
        LocalDate today = LocalDate.now();
        if (card.getLastTransactionDate() == null || !today.equals(card.getLastTransactionDate())) {
            card.setDailyWithdrawn(0);
            card.setDailyDeposited(0);
            card.setDailyTransferred(0);
            card.setLastTransactionDate(today);
            dailyAmount = 0;
        }
        if (dailyAmount + amount > limit) {
            throw new DailyLimitExceededException(card.getClass().getSimpleName() + ": " + transactionType + " daily limit of $" + limit + " exceeded. Used: $" + dailyAmount + ", Requested: $" + amount + "."
            );
        }
    }

}