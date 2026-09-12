package com.ga.acme.controllers;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.exceptions.AccountTypeNotSupportedException;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.interfaces.IAccount;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.models.*;
import com.ga.acme.util.FileHandler;

import java.util.HashMap;
import java.util.Map;

import static com.ga.acme.controllers.Card.addCard;
import static com.ga.acme.controllers.Card.getCardById;
import static com.ga.acme.controllers.Transaction.initialChecks;
import static com.ga.acme.util.FileHandler.getDataFromFile;

public class Account {
    public static void addAccount(String userId, AccountType type, Boolean mastercard, Boolean mastercardPlatinum, Boolean mastercardTitanium) {
        IUser user = Auth.getUserById(userId);
        IAccount acc = null;

        try {
            switch (type) {
                case CHECKINGACCOUNT:
                    if (user.getCheckingAccount() == null) {
                        acc = new CheckingAccount(user.getId(), null, null, null);
                        user.setCheckingAccount((CheckingAccount) acc);
                    } else {
                        throw new AccountAlreadyExistsException("Checking account for this user already exists");
                    }
                    break;
                case SAVINGSACCOUNT:
                    if (user.getSavingsAccount() == null) {
                        acc = new SavingsAccount(user.getId(), null, null, null);
                        user.setSavingsAccount((SavingsAccount) acc);
                    } else {
                        throw new AccountAlreadyExistsException("Savings account for this user already exists");
                    }
                    break;
                default:
                    throw new AccountTypeNotSupportedException();
            }

            acc.setMastercard((Mastercard) addCard(mastercard, acc.getId(), CardType.MASTERCARD));
            acc.setMastercardPlatinum((MastercardPlatinum) addCard(mastercardPlatinum, acc.getId(), CardType.MASTERCARDPLATINUM));
            acc.setMastercardTitanium((MastercardTitanium) addCard(mastercardTitanium, acc.getId(), CardType.MASTERCARDTITANIUM));

            FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
            FileHandler.writeToFile(FilePath.ACCOUNTS.getPath(), acc.toString());

        } catch (
                AccountTypeNotSupportedException |
                AccountAlreadyExistsException e) {
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
                if (!values.get("mastercardId").equals("null")) {
                    acc.setMastercard((Mastercard) getCardById(values.get("mastercardId")));
                }
                if (!values.get("mastercardPlatinumId").equals("null")) {
                    acc.setMastercardPlatinum((MastercardPlatinum) getCardById(values.get("mastercardPlatinumId")));
                }
                if (!values.get("mastercardTitaniumId").equals("null")) {
                    acc.setMastercardTitanium((MastercardTitanium) getCardById(values.get("mastercardTitaniumId")));
                }
                acc.setOverdrafts(Integer.parseInt(values.get("overdrafts")));
                acc.setOverdraftAmount(Double.parseDouble(values.get("overdraftAmount")));
                acc.setLocked(Boolean.parseBoolean(values.get("isLocked")));

            } else {
                throw new RecordNotFoundException("Account with id " + id + " not found");
            }
        } catch (
                RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return acc;
    }

    protected static IAccount getVerifiedAccount(String userId, AccountType accountType, CardType cardType) {
        IUser user = Auth.getUserById(userId);
        try {
            initialChecks(user, accountType, cardType);
        } catch (
                Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return accountType == AccountType.CHECKINGACCOUNT
                ? user.getCheckingAccount()
                : user.getSavingsAccount();
    }
}