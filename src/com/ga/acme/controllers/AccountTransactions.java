package com.ga.acme.controllers;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.exceptions.AccountTypeNotSupported;
import com.ga.acme.exceptions.RecordNotFoundException;
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

    public static void addAccount(IUser user, String type) {
        IAccount acc = null;
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        try {
            switch (type.toLowerCase()) {
                case "checkingaccount":
                    if (user.getCheckingAccount() == null) {
                        acc = new CheckingAccount(uniqueId, user.getId());
                        user.setCheckingAccount((CheckingAccount) acc);
                    } else {
                        throw new AccountAlreadyExistsException("Checking account for this user already exists");
                    }
                    break;
                case "savingsaccount":
                    if (user.getSavingsAccount() == null) {
                        acc = new SavingsAccount(uniqueId, user.getId());
                        user.setSavingsAccount((SavingsAccount) acc);
                    } else {
                        throw new AccountAlreadyExistsException("Savings account for this user already exists");
                    }
                    break;
                default:
                    throw new AccountTypeNotSupported();
            }
            FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
            FileHandler.writeToFile(FilePath.ACCOUNTS.getPath(), acc.toString());

        } catch (AccountTypeNotSupported | AccountAlreadyExistsException e) {
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
            } else {
                throw new RecordNotFoundException("Account with id " + id + " not found");
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return acc;
    }

}
