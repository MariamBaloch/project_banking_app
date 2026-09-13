package com.ga.acme.controllers;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.enums.TransactionType;
import com.ga.acme.exceptions.*;
import com.ga.acme.interfaces.IAccount;
import com.ga.acme.interfaces.ICard;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.models.TransactionRecord;
import com.ga.acme.util.FileHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static com.ga.acme.controllers.Account.getVerifiedAccount;
import static com.ga.acme.controllers.Card.getCardByTypeForAccount;
import static com.ga.acme.util.FileHandler.getDataFromFile;

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

        if (cardType != null) {
            boolean isCardSupported = switch (cardType) {
                case MASTERCARD -> account.getMastercard() != null;
                case MASTERCARDPLATINUM -> account.getMastercardPlatinum() != null;
                case MASTERCARDTITANIUM -> account.getMastercardTitanium() != null;
            };

            if (!isCardSupported) {
                throw new CardNotSupportedException();
            }
        }

    }

    public static void withdraw(String userId, double amount, AccountType accountType, CardType cardType) {
        Account.VerifiedAccountResult verifiedAccountResult = getVerifiedAccount(userId, accountType, cardType);
        if (verifiedAccountResult == null) {
            return;
        }
        IAccount account = verifiedAccountResult.account();
        IUser user = verifiedAccountResult.user();

        ICard card = getCardByTypeForAccount(account, cardType);
        try {
            if (account.getBalance() >= 0 && account.getBalance() - amount < -100) {
                throw new InsufficientAmountException("You do not have enough balance for this transaction");
            }

            handleDailyLimits(card, amount, card.getDailyWithdrawn(), card.getWithdrawLimitPerDay(), "Withdraw");
            if (account.isLocked()) {
                throw new AccountLockedException(
                        "Account is locked due to reaching overdraft limit. Pay off your negative balance of $" + Math.abs(account.getBalance()) + " and $" + account.getOverdraftAmount() + " overdraft fee to unlock.");
            }
            if (account.getBalance() < 0 && amount > 100) {
                throw new WithdrawLimitException(
                        "Negative balance, withdrawals are limited to $100. Please enter a lower amount.");
            }

            TransactionRecord.Builder transactionBuilder = new TransactionRecord.Builder().
                    setBalanceBefore(account.getBalance());

            account.withdraw(amount);
            card.setDailyWithdrawn(card.getDailyWithdrawn() + amount);

            if (account.getBalance() < 0) {
                account.setOverdraftAmount(account.getOverdraftAmount() + OVERDRAFT_AMOUNT);
                account.setOverdrafts(account.getOverdrafts() + 1);
                System.out.println("Negative balance, overdraft fee of $" + OVERDRAFT_AMOUNT + " charged. " + "Total overdraft fees owed: $" + account.getOverdraftAmount());
                if (account.getOverdrafts() >= OVERDRAFT_LIMIT) {
                    account.setLocked(true);
                    System.out.println("Account has been locked due to reaching overdraft limit. " + "Resolve your negative balance of $" + Math.abs(account.getBalance()) + " and pay $" + account.getOverdraftAmount() + " to unlock account.");
                }
            }

            TransactionRecord transactionRecord = transactionBuilder.
                    setTransactionType(TransactionType.WITHDRAWAL).
                    setAccountType(accountType).
                    setBalanceAfter(account.getBalance()).
                    setTransactionAmount(amount).
                    setCardType(cardType).
                    setOverdraftAmount(account.getOverdraftAmount()).
                    setTransactionDate(LocalDateTime.now()).build();

            writeTransactionToFile(user, transactionRecord);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
            FileHandler.updateLineInFile(FilePath.CARDS.getPath(), card.getId(), card.toString());
        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void resolveOverdraft(String userId, double paymentAmount, AccountType accountType) {
        Account.VerifiedAccountResult verifiedAccountResult = getVerifiedAccount(userId, accountType, null);
        if (verifiedAccountResult == null) {
            return;
        }
        IAccount account = verifiedAccountResult.account();
        IUser user = verifiedAccountResult.user();
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

            TransactionRecord.Builder transactionBuilder = new TransactionRecord.Builder().
                    setBalanceBefore(account.getBalance());

            account.setBalance(surplus);
            account.setOverdraftAmount(0);
            account.setOverdrafts(0);
            account.setLocked(false);

            TransactionRecord transactionRecord = transactionBuilder.
                    setTransactionType(TransactionType.OVERDRAFT_RESOLUTION).
                    setAccountType(accountType).
                    setBalanceAfter(account.getBalance()).
                    setTransactionAmount(paymentAmount).
                    setCardType(null).
                    setOverdraftAmount(account.getOverdraftAmount()).
                    setTransactionDate(LocalDateTime.now()).build();

            writeTransactionToFile(user, transactionRecord);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
            System.out.println("Overdraft resolved. Account unlocked. New balance: " + account.getBalance());

        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void transfer(String userId, double amount, AccountType fromAccountType, CardType cardType, String toUserId, AccountType toAccountType, Boolean depositToAnotherAccount) {
        Account.VerifiedAccountResult verifiedAccountResult = getVerifiedAccount(userId, fromAccountType, cardType);
        if (verifiedAccountResult == null) {
            return;
        }
        IAccount fromAccount = verifiedAccountResult.account();
        IUser fromUser = verifiedAccountResult.user();

        boolean ownTransfer = userId.equals(toUserId);
        try {
            if (fromAccount.getBalance() < amount) {
                throw new InsufficientAmountException("You do not have enough balance for this transaction");
            }

            if (ownTransfer && toAccountType.equals(fromAccountType)) {
                throw new IllegalArgumentException("Transferring to same account type for same user not allowed");
            }

            IUser toUser = Auth.getUserById(toUserId);
            IAccount toAccount = AccountType.CHECKINGACCOUNT.equals(toAccountType) ? toUser.getCheckingAccount() : toUser.getSavingsAccount();

            if (toAccount == null) {
                String outputAccountType = toAccountType == AccountType.SAVINGSACCOUNT ? AccountType.SAVINGSACCOUNT.getDisplayName() : AccountType.CHECKINGACCOUNT.getDisplayName();
                throw new RecordNotFoundException("No " + outputAccountType + " found for user: " + toUser.getName());
            }

            ICard card = getCardByTypeForAccount(fromAccount, cardType);

            double limit = 0;
            String transactionType = null;
            double dailyUsed = 0;

            TransactionRecord.Builder transactionBuilder = new TransactionRecord.Builder().
                    setBalanceBefore(fromAccount.getBalance());

            if (ownTransfer) {
                limit = card.getTransferLimitPerDayOwnAccount();
                transactionType = "Transfer to own account";
                dailyUsed = card.getDailyTransferredOwnAccount();
                transactionBuilder.setTransactionType(TransactionType.TRANSFER_TO_OWN_ACCOUNT);
            } else if (depositToAnotherAccount) {
                limit = card.getDepositLimitPerDay();
                transactionType = "Deposit to another account";
                dailyUsed = card.getDailyDeposited();
                transactionBuilder.setTransactionType(TransactionType.DEPOSIT).
                        setToUser(toUser.getId() + "-" + toUser.getName()).
                        setToAccountType(toAccountType);
            } else {
                limit = card.getTransferLimitPerDay();
                transactionType = "Transfer";
                dailyUsed = card.getDailyTransferred();
                transactionBuilder.setTransactionType(TransactionType.TRANSFER).
                        setToUser(toUser.getId() + "-" + toUser.getName()).
                        setToAccountType(toAccountType);
            }

            handleDailyLimits(card, amount, dailyUsed, limit, transactionType);

            fromAccount.transferFunds(amount, toAccount);

            if (ownTransfer) {
                card.setDailyTransferredOwnAccount(card.getDailyTransferredOwnAccount() + amount);
            } else if (depositToAnotherAccount) {
                card.setDailyDeposited(card.getDailyDeposited() + amount);
            } else {
                card.setDailyTransferred(card.getDailyTransferred() + amount);
            }

            TransactionRecord transactionRecord = transactionBuilder.
                    setAccountType(fromAccountType).
                    setBalanceAfter(fromAccount.getBalance()).
                    setTransactionAmount(amount).
                    setCardType(cardType).
                    setTransactionDate(LocalDateTime.now()).build();

            writeTransactionToFile(fromUser, transactionRecord);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), fromAccount.getId(), fromAccount.toString());
            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), toAccount.getId(), toAccount.toString());
            FileHandler.updateLineInFile(FilePath.CARDS.getPath(), card.getId(), card.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deposit(String userId, double amount, AccountType accountType, CardType cardType) {
        Account.VerifiedAccountResult verifiedAccount = getVerifiedAccount(userId, accountType, cardType);
        if (verifiedAccount == null) {
            return;
        }
        IAccount account = verifiedAccount.account();
        IUser user = verifiedAccount.user();

        ICard card = getCardByTypeForAccount(account, cardType);

        try {
            handleDailyLimits(card, amount, card.getDailyDepositedOwnAccount(),
                    card.getDepositLimitPerDayOwnAccount(), "Deposit");

            TransactionRecord.Builder transactionBuilder = new TransactionRecord.Builder().
                    setBalanceBefore(account.getBalance());

            account.deposit(amount);
            card.setDailyDeposited(card.getDailyDeposited() + amount);

            TransactionRecord transactionRecord = transactionBuilder.
                    setTransactionType(TransactionType.DEPOSIT_TO_OWN_ACCOUNT).
                    setAccountType(accountType).
                    setBalanceAfter(account.getBalance()).
                    setTransactionAmount(amount).
                    setCardType(cardType).
                    setTransactionDate(LocalDateTime.now()).build();

            writeTransactionToFile(user, transactionRecord);

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
            card.setDailyDepositedOwnAccount(0);
            card.setDailyTransferredOwnAccount(0);
            card.setLastTransactionDate(today);
            dailyAmount = 0;
        }
        if (dailyAmount + amount > limit) {
            throw new DailyLimitExceededException(card.getClass().getSimpleName() + ": " + transactionType + " daily limit of $" + limit + " exceeded. Used: $" + dailyAmount + ", Requested: $" + amount + ", Remaining: $" + (limit - dailyAmount));
        }
    }

    private static void writeTransactionToFile(IUser user, TransactionRecord transactionRecord) {
        String transactionFileName = FilePath.CUSTOMER_TRANSACTIONS.getPath() + user.getId() + "-" + user.getName();
        FileHandler.writeToFile(transactionFileName, transactionRecord);
    }


    public static void getUserTransactions(String userId) {
        IUser user = Auth.getUserById(userId);
        HashMap<String, Map<String, String>> transactions = getDataFromFile(FilePath.CUSTOMER_TRANSACTIONS.getPath() + user.getId() + "-" + user.getName());
        for (Map.Entry<String, Map<String, String>> entry : transactions.entrySet()) {
            System.out.print("[ Transaction ID" + ": " + entry.getKey() + ", ");
            String formattedDate = LocalDateTime.parse(entry.getValue().get("transactionDate")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.out.print("Transaction Date" + ": " + formattedDate + ", ");
            System.out.print("Transaction Type" + ": " + entry.getValue().get("transactionType") + ", ");
            System.out.print("Account Type" + ": " + entry.getValue().get("accountType") + ", ");
            System.out.print("Card Used" + ": " + entry.getValue().get("cardType") + ", ");
            String toUser = entry.getValue().get("toUser");
            String toAccount = entry.getValue().get("toAccount");
            if (toUser != null && toAccount != null) {
                System.out.print("To User" + ": " + toUser + ", ");
                System.out.print("To Account Type" + ": " + toAccount + ", ");
            }
            System.out.print("Balance Before" + ": $" + entry.getValue().get("balanceBefore") + ", ");
            System.out.print("Transaction Amount" + ": $" + entry.getValue().get("transactionAmount") + ", ");
            System.out.print("Balance After" + ": $" + entry.getValue().get("balanceAfter") + ", ");
            String overdraftAmount = entry.getValue().get("overdraftAmount").equals("null") ? "0.00$" : entry.getValue().get("overdraftAmount");
            System.out.print("Overdraft Amount" + ": " + overdraftAmount + " ]");
            System.out.println(" ");
            System.out.println("-----------------------------------------------------------------");
        }
    }

}