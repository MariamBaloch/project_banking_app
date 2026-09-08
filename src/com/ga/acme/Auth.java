package com.ga.acme;

import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.util.FileHandler;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import static com.ga.acme.util.FileHandler.getDataFromFile;

public class Auth {

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
            HashMap<String, List<String>> users =  getDataFromFile("Data/users.txt");
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

    public static IUser signup(String id, String name, String password, String role) {
        IUser user = null;
        String transformedRole = role.toLowerCase();
        switch (transformedRole) {
            case "customer":
                user = new Customer();
                break;
            case "banker":
                user = new Banker();
                break;
        }
        user.setId(id);
        user.setName(name);
        user.setHashedPassword(password);
        user.setRole(Roles.valueOf(role));
        FileHandler.writeToFile("Data/users.txt", user);
        return user;
    }

    public static IUser login(String id, String password) {
        IUser user = null;
        try {
            user = getUserById(id);
            if(checkPassword(password, user.getHashedPassword())) {
                return user;
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }
}
