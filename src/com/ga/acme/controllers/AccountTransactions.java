package com.ga.acme.controllers;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.exceptions.*;
import com.ga.acme.interfaces.IAccount;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.models.CheckingAccount;
import com.ga.acme.models.SavingsAccount;
import com.ga.acme.util.FileHandler;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.ga.acme.util.FileHandler.getDataFromFile;

public class AccountTransactions {

    public static final int OVERDRAFT_LIMIT = 2;
    public static final double OVERDRAFT_AMOUNT = 35;

    public static void addAccount(String userId, String type, Boolean mastercard, Boolean mastercardPlatinum, Boolean mastercardTitanium) {
        IUser user = Auth.getUserById(userId);
        IAccount acc = null;
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        try {
            switch (type.toLowerCase()) {
                case "checkingaccount":
                    if (user.getCheckingAccount() == null) {
                        acc = new CheckingAccount(uniqueId, user.getId(), mastercard, mastercardPlatinum, mastercardTitanium);
                        user.setCheckingAccount((CheckingAccount) acc);
                    } else {
                        throw new AccountAlreadyExistsException("Checking account for this user already exists");
                    }
                    break;
                case "savingsaccount":
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
        HashMap<String, List<String>> accounts = getDataFromFile(FilePath.ACCOUNTS.getPath());
        try {
            if (accounts.containsKey(id)) {
                List<String> values = accounts.get(id);
                if (values.get(2).equalsIgnoreCase(AccountType.CHECKINGACCOUNT.toString())) {
                    acc = new CheckingAccount();
                } else if (values.get(2).equalsIgnoreCase(AccountType.SAVINGSACCOUNT.toString())) {
                    acc = new SavingsAccount();
                }
                acc.setId(id);
                acc.setBalance(Double.parseDouble(values.get(0)));
                acc.setUserId(values.get(1));
                acc.setMastercard(Boolean.parseBoolean(values.get(3)));
                acc.setMastercardPlatinum(Boolean.parseBoolean(values.get(4)));
                acc.setMastercardTitanium(Boolean.parseBoolean(values.get(5)));
                acc.setOverdrafts(Integer.parseInt(values.get(6)));
                acc.setOverdraftAmount(Double.parseDouble(values.get(7)));
            } else {
                throw new RecordNotFoundException("Account with id " + id + " not found");
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return acc;
    }

    public static void initialChecks(IUser user, AccountType accountType, CardType cardType) {
        try {
            if (!user.getIsLoggedIn()) {
                throw new AccountNotLoggedInException();
            }
            if (accountType != AccountType.CHECKINGACCOUNT && accountType != AccountType.SAVINGSACCOUNT) {
                throw new AccountTypeNotSupportedException();
            }

            IAccount account = accountType == AccountType.CHECKINGACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();

            if (account == null) {
                throw new RecordNotFoundException("No " + accountType.toString().toLowerCase() + " found");
            }

            boolean isCardSupported = switch (cardType) {
                case MASTERCARD -> account.isMastercard();
                case MASTERCARD_PLATINUM -> account.isMastercardPlatinum();
                case MASTERCARD_TITANIUM -> account.isMastercardTitanium();
            };

            if (!isCardSupported) {
                throw new CardNotSupportedException();
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public static double withdraw(String userId, double amount, AccountType accountType, CardType cardType) {
        IUser user = Auth.getUserById(userId);
        initialChecks(user, accountType, cardType);
        IAccount account = accountType == AccountType.CHECKINGACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();
        try {
            if (account.getOverdraftAmount() == 0 && account.getBalance() >= 0) {
                account.setLocked(false);
            }
            if (account.isLocked()) {
                throw new AccountLockedException("Account locked, resolve overdraft fees and negative balance to unlock account");
            } else if (!account.isLocked()) {
                if (account.getOverdrafts() == OVERDRAFT_LIMIT) {
                    account.setLocked(true);
                    throw new AccountLockedException("Overdraft limit reached, account has been locked");
                }
                if (account.getBalance() <= 0 && amount > 100) {
                    throw new WithdrawLimitException("Negative balance, unable to withdraw more than 100$. Please enter a lower amount");
                }
                if (account.getBalance() <= 0 && amount <= 100) {
                    account.setOverdraftAmount(account.getOverdraftAmount() + OVERDRAFT_AMOUNT);
                    account.setOverdrafts(account.getOverdrafts() + 1);
                }
            }
            account.withdraw(amount);
            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return account.getBalance();
    }

    public static double deposit(String userId, double amount, AccountType accountType, CardType cardType) {
        IUser user = Auth.getUserById(userId);
        initialChecks(user, accountType, cardType);
        IAccount account = accountType == AccountType.CHECKINGACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();
        account.deposit(amount);
        FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
        return account.getBalance();
    }

    public static void payOverdraft(String userId, double amount, AccountType accountType, CardType cardType) {
        IUser user = Auth.getUserById(userId);
        initialChecks(user, accountType, cardType);
        IAccount account = accountType == AccountType.CHECKINGACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();
        if (amount > account.getOverdrafts()) {
            amount = account.getOverdrafts();
        }
        account.setOverdraftAmount(account.getOverdraftAmount() - OVERDRAFT_AMOUNT);
    }
}
