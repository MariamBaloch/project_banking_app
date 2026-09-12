package com.ga.acme.controllers;

import com.ga.acme.enums.FilePath;
import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.exceptions.AccountLockedException;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.exceptions.UserAlreadyLoggedInException;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.models.Banker;
import com.ga.acme.models.CheckingAccount;
import com.ga.acme.models.Customer;
import com.ga.acme.models.SavingsAccount;
import com.ga.acme.util.FileHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import static com.ga.acme.util.FileHandler.getDataFromFile;

public class Auth {
    private static final int MAX_LOGIN_ATTEMPT = 2; // since it starts from 0 so its 3;

    public static String encryptPassword(String password) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hashBytes = md.digest(password.getBytes());
        return HexFormat.of().formatHex(hashBytes);
    }


    public static IUser getUserById(String id) {
        IUser user = null;
        HashMap<String, Map<String, String>> users = getDataFromFile(FilePath.USERS.getPath());
        try {
            if (users.containsKey(id)) {
                Map<String, String> values = users.get(id);
                if (values.get("role").equalsIgnoreCase("customer")) {
                    user = new Customer();
                } else if (values.get("role").equalsIgnoreCase("banker")) {
                    user = new Banker();
                }
                user.setId(id);
                user.setName(values.get("name"));
                user.setHashedPassword(values.get("hashedPassword"));
                user.setRole(Roles.valueOf(values.get("role")));
                user.setLoginAttempts(Integer.parseInt(values.get("loginAttempts")));
                user.setLockedUntil(!values.get("lockedUntil").equals("null") ? LocalTime.parse(values.get("lockedUntil")) : null);
                user.setIsLoggedIn(Boolean.parseBoolean(values.get("isLoggedIn")));
                if (!values.get("checkingId").equals("null")) {
                    user.setCheckingAccount((CheckingAccount) AccountTransactions.getAccountById(values.get("checkingId")));
                }
                if (!values.get("savingId").equals("null")) {
                    user.setSavingsAccount((SavingsAccount) AccountTransactions.getAccountById(values.get("savingId")));
                }
            } else {
                throw new RecordNotFoundException("User with this id " + id + " does not exist");
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return user;
    }

    public static boolean checkPassword(String userInputPassword, String storedPassword) {
        if (userInputPassword == null || storedPassword == null) {
            return false;
        }
        byte[] storedHashBytes = HexFormat.of().parseHex(storedPassword);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] enteredHashBytes = md.digest(userInputPassword.getBytes());

        return MessageDigest.isEqual(storedHashBytes, enteredHashBytes);
    }

    public static IUser signup(String id, String name, String password, String role) throws AccountAlreadyExistsException {
        IUser user = null;

        user = getUserById(id);
        if (user.getId() != null) {
            throw new AccountAlreadyExistsException("Account already exists, try a different id");
        }

        String transformedRole = role.toLowerCase();
        user = switch (transformedRole) {
            case "customer" -> new Customer();
            case "banker" -> new Banker();
            default -> user;
        };
        user.setId(id);
        user.setName(name);
        user.setHashedPassword(password);
        user.setRole(Roles.valueOf(role));
        FileHandler.writeToFile(FilePath.USERS.getPath(), user);

        return user;
    }

    public static IUser login(String id, String password) {
        try {
            IUser user = getUserById(id);

            if (user.getIsLoggedIn()) {
                throw new UserAlreadyLoggedInException();
            }

            LocalTime now = LocalTime.now();
            boolean isLocked = user.getLockedUntil() != null && now.isBefore(user.getLockedUntil());
            boolean isPasswordCorrect = checkPassword(password, user.getHashedPassword());

            if (isPasswordCorrect && !isLocked) {
                user.setIsLoggedIn(true);
                user.setLoginAttempts(0);
                user.setLockedUntil(null);
            } else {
                if (user.getLoginAttempts() == MAX_LOGIN_ATTEMPT) {
                    if (user.getLockedUntil() == null) {
                        user.setLockedUntil(now.plusMinutes(1));
                    } else if (now.isBefore(user.getLockedUntil())) {
                        throw new AccountLockedException("Max login attempt reached, please try again after one minute.");
                    } else {
                        user.setLoginAttempts(0);
                        user.setLockedUntil(null);
                    }
                } else {
                    user.setLoginAttempts(user.getLoginAttempts() + 1);
                }
            }

            FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
            return user;
        } catch (AccountLockedException | UserAlreadyLoggedInException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void logout(String id) {
        IUser user = getUserById(id);
        user.setIsLoggedIn(false);
        FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
    }
}