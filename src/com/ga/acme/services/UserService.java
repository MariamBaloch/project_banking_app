package com.ga.acme.services;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.models.Account;
import com.ga.acme.models.Card;
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
        System.out.println("=======================================");

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
    public static void printUserProfile(User user) {
        System.out.printf("%-22s %s%n", "Customer ID:", user.getId());
        System.out.printf("%-22s %s%n", "Customer Name:", user.getName());
        System.out.printf("%-22s %s%n", "Role:", user.getRole());
        System.out.println("=====================================================================");

        printAccountProfile(user.getCheckingAccount(), AccountType.CHECKING_ACCOUNT);
        printAccountProfile(user.getSavingsAccount(), AccountType.SAVINGS_ACCOUNT);

        System.out.println("=====================================================================");
    }

    private static void printAccountProfile(Account account, AccountType accountType) {
        if (account == null) {
            System.out.println("No " + accountType.getDisplayName() + " linked to this customer.");
            return;
        }

        System.out.println("Account: " + accountType.getDisplayName());
        System.out.printf("   %-18s %s%n", "Account ID:", account.getId());
        System.out.printf("   %-18s $%.2f%n", "Balance:", account.getBalance());
        System.out.printf("   %-18s %s%n", "Status:", account.isLocked() ? "Locked" : "Active");
        System.out.println("   Cards:");

        printCardProfile(account.getMastercard(), "Mastercard");
        printCardProfile(account.getMastercardPlatinum(), "Mastercard Platinum");
        printCardProfile(account.getMastercardTitanium(), "Mastercard Titanium");

        System.out.println();
    }

    private static void printCardProfile(Card card, String cardLabel) {
        if (card == null) {
            return;
        }

        System.out.printf("   %s | Card ID: %s%n", cardLabel, card.getId());
        System.out.printf("   Withdraw: limit $%.2f | used $%.2f | remaining $%.2f%n",
                card.getWithdrawLimitPerDay(), card.getDailyWithdrawn(), card.getWithdrawLimitPerDay() - card.getDailyWithdrawn());
        System.out.printf("   Deposit: limit $%.2f | used $%.2f | remaining $%.2f%n",
                card.getDepositLimitPerDay(), card.getDailyDeposited(), card.getDepositLimitPerDay() - card.getDailyDeposited());
        System.out.printf("   Transfer: limit $%.2f | used $%.2f | remaining $%.2f%n",
                card.getTransferLimitPerDay(), card.getDailyTransferred(), card.getTransferLimitPerDay() - card.getDailyTransferred());
        System.out.printf("   Own acc deposit: limit $%.2f | used $%.2f | remaining $%.2f%n",
                card.getDepositLimitPerDayOwnAccount(), card.getDailyDepositedOwnAccount(), card.getDepositLimitPerDayOwnAccount() - card.getDailyDepositedOwnAccount());
        System.out.printf("   Own acc transfer: limit $%.2f | used $%.2f | remaining $%.2f%n",
                card.getTransferLimitPerDayOwnAccount(), card.getDailyTransferredOwnAccount(), card.getTransferLimitPerDayOwnAccount() - card.getDailyTransferredOwnAccount());
        System.out.println();
    }

}