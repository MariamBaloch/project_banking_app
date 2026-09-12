package com.ga.acme.controllers;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.exceptions.*;
import com.ga.acme.interfaces.IAccount;
import com.ga.acme.interfaces.ICard;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.models.*;
import com.ga.acme.util.FileHandler;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ga.acme.util.FileHandler.getDataFromFile;

public class AccountTransactions {

    public static final int OVERDRAFT_LIMIT = 2;
    public static final double OVERDRAFT_AMOUNT = 35;

    public static void addAccount(String userId, AccountType type, Boolean mastercard, Boolean mastercardPlatinum, Boolean mastercardTitanium) {
        IUser user = Auth.getUserById(userId);
        IAccount acc = null;
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        try {
            switch (type) {
                case CHECKINGACCOUNT:
                    if (user.getCheckingAccount() == null) {
                        acc = new CheckingAccount(uniqueId, user.getId(), mastercard, mastercardPlatinum, mastercardTitanium);
                        user.setCheckingAccount((CheckingAccount) acc);
                    } else {
                        throw new AccountAlreadyExistsException("Checking account for this user already exists");
                    }
                    break;
                case SAVINGSACCOUNT:
                    if (user.getSavingsAccount() == null) {
                        acc = new SavingsAccount(uniqueId, user.getId(), mastercard, mastercardPlatinum, mastercardTitanium);
                        user.setSavingsAccount((SavingsAccount) acc);
                    } else {
                        throw new AccountAlreadyExistsException("Savings account for this user already exists");
                    }
                    break;
                default:
                    throw new AccountTypeNotSupportedException();
            }
            FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
            FileHandler.writeToFile(FilePath.ACCOUNTS.getPath(), acc.toString());

        } catch (AccountTypeNotSupportedException | AccountAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }
    }

    public static IAccount getAccountById(String id) {
        IAccount acc = null;
        HashMap<String, Map<String, String>> accounts = getDataFromFile(FilePath.ACCOUNTS.getPath());
        try {
            if (accounts.containsKey(id)) {
                Map<String, String> values = accounts.get(id);
                if (values.get("type").equalsIgnoreCase(AccountType.CHECKINGACCOUNT.toString())) {
                    acc = new CheckingAccount();
                } else if (values.get("type").equalsIgnoreCase(AccountType.SAVINGSACCOUNT.toString())) {
                    acc = new SavingsAccount();
                }
                acc.setId(id);
                acc.setBalance(Double.parseDouble(values.get("balance")));
                acc.setUserId(values.get("userId"));
                acc.setMastercard(Boolean.parseBoolean(values.get("mastercard")));
                acc.setMastercardPlatinum(Boolean.parseBoolean(values.get("mastercardPlatinum")));
                acc.setMastercardTitanium(Boolean.parseBoolean(values.get("mastercardTitanium")));
                acc.setOverdrafts(Integer.parseInt(values.get("overdrafts")));
                acc.setOverdraftAmount(Double.parseDouble(values.get("overdraftAmount")));
                acc.setLocked(Boolean.parseBoolean(values.get("isLocked")));
                acc.setDailyWithdrawn(Double.parseDouble(values.get("dailyWithdrawn")));
                acc.setDailyDeposited(Double.parseDouble(values.get("dailyDeposited")));
                acc.setDailyTransferred(Double.parseDouble(values.get("dailyTransferred")));
                String lastTransactionDate = values.get("lastTransactionDate");
                if (!lastTransactionDate.isEmpty() && !lastTransactionDate.equals("null")) {
                    acc.setLastTransactionDate(LocalDate.parse(lastTransactionDate));
                } else {
                    acc.setLastTransactionDate(null);
                }

            } else {
                throw new RecordNotFoundException("Account with id " + id + " not found");
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return acc;
    }

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
            case MASTERCARD -> account.isMastercard();
            case MASTERCARD_PLATINUM -> account.isMastercardPlatinum();
            case MASTERCARD_TITANIUM -> account.isMastercardTitanium();
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
        ICard card = getCardForType(cardType);
        try {
            handleDailyLimits(account, amount, account.getDailyWithdrawn(), card.getWithdrawLimitPerDay(), "Withdraw");
            if (account.isLocked()) {
                throw new AccountLockedException(
                        "Account is locked due to reaching overdraft limit. Pay off your negative balance and $" + account.getOverdraftAmount() + " overdraft fee to unlock.");
            }
            if (account.getBalance() < 0 && amount > 100) {
                throw new WithdrawLimitException(
                        "Negative balance, withdrawals are limited to $100. Please enter a lower amount.");
            }
            account.withdraw(amount);
            account.setDailyWithdrawn(account.getDailyWithdrawn() + amount);

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
        } catch (Exception e) {
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void transfer(String userId, double amount, AccountType fromAccountType, AccountType toAccountType, String toUserId, CardType cardType, boolean ownTransfer) {
        IAccount fromAccount = getVerifiedAccount(userId, fromAccountType, cardType);
        if (fromAccount == null) {
            return;
        }
        IAccount toAccount = getAccountById(toUserId);
        ICard card = getCardForType(cardType);
        try {
            double limit = ownTransfer ? card.getTransferLimitPerDayOwnAccount() : card.getTransferLimitPerDay();
            String transactionType = ownTransfer ? "Transfer to own account" : "Transfer";
            handleDailyLimits(fromAccount, amount, fromAccount.getDailyTransferred(), limit, transactionType);

            fromAccount.transferFunds(amount, toAccount);
            fromAccount.setDailyTransferred(fromAccount.getDailyTransferred() + amount);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), fromAccount.getId(), fromAccount.toString());
            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), toAccount.getId(), toAccount.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deposit(String userId, double amount, AccountType accountType, CardType cardType) {
        IAccount account = getVerifiedAccount(userId, accountType, cardType);
        if (account == null) {
            return;
        }
        ICard card = getCardForType(cardType);
        try {
            handleDailyLimits(account, amount, account.getDailyDeposited(),
                    card.getDepositLimitPerDayOwnAccount(), "Deposit");
            account.deposit(amount);
            account.setDailyDeposited(account.getDailyDeposited() + amount);
            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static IAccount getVerifiedAccount(String userId, AccountType accountType, CardType cardType) {
        IUser user = Auth.getUserById(userId);
        try {
            initialChecks(user, accountType, cardType);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return accountType == AccountType.CHECKINGACCOUNT
                ? user.getCheckingAccount()
                : user.getSavingsAccount();
    }

    private static ICard getCardForType(CardType cardType) {
        return switch (cardType) {
            case MASTERCARD -> new Mastercard();
            case MASTERCARD_PLATINUM -> new MastercardPlatinum();
            case MASTERCARD_TITANIUM -> new MastercardTitanium();
        };
    }

    private static void handleDailyLimits(IAccount account, double amount, double dailyAmount, double limit, String transactionType) throws DailyLimitExceededException {
        LocalDate today = LocalDate.now();
        if (account.getLastTransactionDate() == null || !today.equals(account.getLastTransactionDate())) {
            account.setDailyWithdrawn(0);
            account.setDailyDeposited(0);
            account.setDailyTransferred(0);
            account.setLastTransactionDate(today);
            dailyAmount = 0;
        }
        if (dailyAmount + amount > limit) {
            throw new DailyLimitExceededException(transactionType + " daily limit of $" + limit + " exceeded. Used: $" + dailyAmount + ", Requested: $" + amount + ".");
        }
    }
}