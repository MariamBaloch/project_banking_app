package com.ga.acme.services;

import com.ga.acme.enums.FilePath;
import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.models.User;
import com.ga.acme.util.FileHandler;

import java.util.HashMap;
import java.util.Map;

import static com.ga.acme.util.FileHandler.getDataFromFile;

public class UserService {

    public static User getUserById(String id) {
        User user = null;
        HashMap<String, Map<String, String>> users = getDataFromFile(FilePath.USERS.getPath());
        try {
            if (users.containsKey(id)) {
                Map<String, String> values = users.get(id);
                user = User.mapToUserObject(values);
            } else {
                throw new RecordNotFoundException("User with ID: " + id + " does not exist");
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    public static User getCustomerById(String id) {
        User user = null;
        HashMap<String, Map<String, String>> users = getDataFromFile(FilePath.USERS.getPath());
        try {
            if (users.containsKey(id)) {
                Map<String, String> values = users.get(id);
                if (values.get("role").equals(Roles.CUSTOMER.toString())) {
                    user = User.mapToUserObject(values);
                }
            } else {
                throw new RecordNotFoundException("Customer with ID: " + id + " does not exist");
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }


    public static Map<String, Map<String, String>> getAllUsers() {
        return FileHandler.getDataFromFile(FilePath.USERS.getPath());
    }

    public static void printAllCustomerIDAndName(Boolean excludeLoggedIn) {
        Map<String, Map<String, String>> users = getAllUsers();
        System.out.printf("%-15s %-10s", "Customer ID", "Customer Name\n");
        System.out.println("---------------------------------------");

        users.entrySet().stream()
                .filter(outerEntry -> {
                            Boolean isCustomer = outerEntry.getValue().get("role").equals(Roles.CUSTOMER.toString());
                            Boolean isLoggedIn = outerEntry.getValue().get("isLoggedIn").equals("false");

                            if (excludeLoggedIn) {
                                return isCustomer && isLoggedIn;
                            } else {
                                return isCustomer;
                            }
                        }
                )
                .forEach((k -> {
                    System.out.printf("%-15s %-1s", k.getKey(), k.getValue().get("name") + "\n");
                }));
    }

}
