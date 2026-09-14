package com.ga.acme.services;

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

import static com.ga.acme.services.CardService.addCard;
import static com.ga.acme.services.TransactionService.initialChecks;
import static com.ga.acme.util.FileHandler.getDataFromFile;

public class AccountService {
    public static void addAccount(String userId, AccountType type, Boolean mastercard, Boolean mastercardPlatinum, Boolean mastercardTitanium) {
        IUser user = AuthService.getUserById(userId);
        IAccount acc = null;

        try {
            switch (type) {
                case CHECKING_ACCOUNT:
                    if (user.getCheckingAccount() == null) {
                        acc = new CheckingAccount(user.getId(), null, null, null);
                        user.setCheckingAccount((CheckingAccount) acc);
                    } else {
                        throw new AccountAlreadyExistsException("Checking account for this user already exists");
                    }
                    break;
                case SAVINGS_ACCOUNT:
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
            acc.setMastercardPlatinum((MastercardPlatinum) addCard(mastercardPlatinum, acc.getId(), CardType.MASTERCARD_PLATINUM));
            acc.setMastercardTitanium((MastercardTitanium) addCard(mastercardTitanium, acc.getId(), CardType.MASTERCARD_TITANIUM));

            FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
            FileHandler.writeToFile(FilePath.ACCOUNTS.getPath(), acc.toString());
            System.out.println("Successfully added " + type.getDisplayName() + " for user " + user.getName());

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
                acc = Account.mapToAccountObject(values);
            } else {
                throw new RecordNotFoundException("Account with id " + id + " not found");
            }
        } catch (
                RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return acc;
    }

    protected static VerifiedAccountResult getVerifiedAccount(String userId, AccountType accountType, CardType cardType) {
        IUser user = AuthService.getUserById(userId);
        if (user == null) return null;
        try {
            initialChecks(user, accountType, cardType);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        IAccount account = accountType == AccountType.CHECKING_ACCOUNT
                ? user.getCheckingAccount()
                : user.getSavingsAccount();

        return new VerifiedAccountResult(user, account);
    }

    protected record VerifiedAccountResult(IUser user, IAccount account) {
    }
}