package com.ga.acme.services;

import com.ga.acme.enums.FilePath;
import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.exceptions.AccountLockedException;
import com.ga.acme.exceptions.InvalidPasswordException;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.models.User;
import com.ga.acme.util.FileHandler;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

import static com.ga.acme.util.FileHandler.getDataFromFile;

public class AuthService {
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


    public static User getUserById(String id) {
        User user = null;
        HashMap<String, Map<String, String>> users = getDataFromFile(FilePath.USERS.getPath());
        try {
            if (users.containsKey(id)) {
                Map<String, String> values = users.get(id);
                user = User.mapToUserObject(values);
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

    public static User signup(String id, String name, String password, Roles role) throws AccountAlreadyExistsException {
        HashMap<String, Map<String, String>> users = getDataFromFile(FilePath.USERS.getPath());
        User user = new User();
        if (users.containsKey(id)) {
            throw new AccountAlreadyExistsException("Account already exists, try a different id");
        }
        switch (role) {
            case CUSTOMER -> user.setRole(Roles.CUSTOMER);
            case BANKER -> user.setRole(Roles.BANKER);
            default -> throw new IllegalArgumentException("Role not supported, enter either BANKER or CUSTOMER");
        }

        user.setId(id);
        user.setName(name);
        user.setHashedPassword(encryptPassword(password));
        user.setRole(role);
        FileHandler.writeToFile(FilePath.USERS.getPath(), user);
        System.out.println("Sign up successful, Please login using your account");
        return user;
    }

    public static User login(String id, String password) throws AccountLockedException, InvalidPasswordException {
        User user = getUserById(id);
        if (user == null) {
            return null;
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
                    FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
                    throw new AccountLockedException("Max login attempt reached, please try again after one minute.");
                } else if (now.isBefore(user.getLockedUntil())) {
                    throw new AccountLockedException("Max login attempt reached, please try again after one minute.");
                } else {
                    user.setLoginAttempts(0);
                    user.setLockedUntil(null);
                }
            } else {
                user.setLoginAttempts(user.getLoginAttempts() + 1);
            }

            FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
            if (!isPasswordCorrect) {
                int remainingAttempts = MAX_LOGIN_ATTEMPT - user.getLoginAttempts() + 1;
                throw new InvalidPasswordException("Incorrect password, account will be locked after " + remainingAttempts + " more attempt(s)");
            }
        }

        FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
        System.out.println("Login successful");
        return user;

    }

    public static User logout(String id) {
        User user = getUserById(id);
        user.setIsLoggedIn(false);
        FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
        System.out.println("Logged out successful");
        return null;
    }

    public static User getLoggInUser() {
        User user = null;
        Map<String, Map<String, String>> users = getDataFromFile(FilePath.USERS.getPath());

        Map<String, String> values = users.values().stream()
                .filter(userData -> "true".equals(userData.get("isLoggedIn")))
                .findFirst()
                .orElse(null);

        if (values == null) {
            return null;
        }
        user = User.mapToUserObject(values);
        return user;
    }
}