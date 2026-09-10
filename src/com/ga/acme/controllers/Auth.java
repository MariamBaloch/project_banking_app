package com.ga.acme.controllers;

import com.ga.acme.enums.FilePath;
import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.exceptions.AccountLockedException;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.exceptions.UserAlreadyLoggedIn;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.models.Banker;
import com.ga.acme.models.Customer;
import com.ga.acme.util.FileHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;

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
        HashMap<String, List<String>> users = getDataFromFile(FilePath.USERS.getPath());
        try {
            if (users.containsKey(id)) {
                List<String> values = users.get(id);
                if (values.get(2).equalsIgnoreCase("customer")) {
                    user = new Customer();
                } else if (values.get(2).equalsIgnoreCase("banker")) {
                    user = new Banker();
                }
                user.setId(id);
                user.setName(values.get(0));
                user.setHashedPassword(values.get(1));
                user.setRole(Roles.valueOf(values.get(2)));
                user.setLoginAttempts(Integer.parseInt(values.get(3)));
                user.setLockedUntil(!values.get(4).equals("null") ? LocalTime.parse(values.get(4)) : null);
                user.setIsLoggedIn(values.get(5).equals("true"));
//                user.getCheckingAccount().setId(values.get(6));
//                user.setSavingsAccount()
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
        IUser user = null;
        try {
            user = getUserById(id);
            if (!user.getIsLoggedIn()) {
                if (checkPassword(password, user.getHashedPassword()) && LocalTime.now().isAfter(user.getLockedUntil())) {
                    user.setIsLoggedIn(true);
                    user.setLoginAttempts(0);
                    user.setLockedUntil(null);
                } else {
                    if (user.getLoginAttempts() == MAX_LOGIN_ATTEMPT && user.getLockedUntil() != null && LocalTime.now().isBefore(user.getLockedUntil())) {
                        throw new AccountLockedException("Max login attempt reached, please try again after one minute.");
                    } else if (user.getLoginAttempts() == MAX_LOGIN_ATTEMPT && user.getLockedUntil() != null && LocalTime.now().isAfter(user.getLockedUntil())) {
                        user.setLoginAttempts(0);
                        user.setLockedUntil(null);
                    } else if (user.getLoginAttempts() == MAX_LOGIN_ATTEMPT) {
                        user.setLockedUntil(LocalTime.now().plusMinutes(1));
                    } else {
                        user.setLoginAttempts(getUserById(id).getLoginAttempts() + 1);
                    }
                }
                FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
                return user;
            } else {
                throw new UserAlreadyLoggedIn();
            }
        } catch (AccountLockedException | UserAlreadyLoggedIn e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static void logout(String id) {
        IUser user = getUserById(id);
        user.setIsLoggedIn(false);
        FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
    }
}
