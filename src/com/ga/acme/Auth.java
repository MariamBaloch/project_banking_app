package com.ga.acme;

import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.exceptions.AccountLockedException;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.util.FileHandler;
import com.ga.acme.enums.FilePath;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.*;
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


    public static IUser getUserById(String id) throws RecordNotFoundException {
            IUser user = null;
            HashMap<String, List<String>> users =  getDataFromFile(FilePath.USERS.getPath());
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
                user.setLockedTime(!values.get(4).equals("null") ? LocalTime.parse(values.get(4)) : null);
            } else {
                throw new RecordNotFoundException("User with this id does not exist");
            }
        return user;
    }

    public static boolean checkPassword(String userInputPassword, String storedPassword){
        if(userInputPassword == null || storedPassword == null) {return false;}
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
        try {
           user =  getUserById(id);
           if (user.getId() != null) {
               throw new AccountAlreadyExistsException("Account already exists, try a different id");
           }
        } catch (RecordNotFoundException e) {
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
        }
        return user;
    }

    public static IUser login(String id, String password) {
        IUser user = null;
        try {
            user = getUserById(id);
            if(checkPassword(password, user.getHashedPassword())) {
                return user;
            } else {
                if (user.getLoginAttempts() == MAX_LOGIN_ATTEMPT && user.getLockedTime() != null && LocalTime.now().isBefore(user.getLockedTime().plusMinutes(1))) {
                    throw new AccountLockedException("Max login attempt reached, please try again after one minute.");
                } else if(user.getLoginAttempts() == MAX_LOGIN_ATTEMPT && user.getLockedTime() != null && LocalTime.now().isAfter(user.getLockedTime().plusMinutes(1))) {
                    user.setLoginAttempts(0);
                    user.setLockedTime(null);
                } else if (user.getLoginAttempts() == MAX_LOGIN_ATTEMPT ) {
                    user.setLockedTime(LocalTime.now());
                }  else {
                    user.setLoginAttempts(getUserById(id).getLoginAttempts() + 1);
                }
                FileHandler.updateLineInFile(FilePath.USERS.getPath(), user.getId(), user.toString());
            }
        } catch (AccountLockedException | RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
