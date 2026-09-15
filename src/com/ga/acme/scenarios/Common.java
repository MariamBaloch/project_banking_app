package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.models.User;
import com.ga.acme.services.UserService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static com.ga.acme.services.UserService.printAllCustomerIDAndName;

public class Common {
    public static void printInitialMenu() {
        System.out.println("\nPlease select an operation to perform:");
        System.out.println("""
                1 - Add account
                2 - Deposit money
                3 - Withdraw money
                4 - Transfer money
                5 - View Transaction History
                6 - View Account Statement
                7 - Logout
                
                Type 'exit' to shut down system
                """);
    }

    public static Boolean convertResponseToBoolean(Scanner sc, List<String> allowedInput) {
        while (true) {
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals(allowedInput.get(0).toLowerCase())) {
                return true;
            }
            if (input.equals(allowedInput.get(1).toLowerCase())) {
                return false;
            }
            System.out.println("Invalid input. Please enter " + allowedInput.get(0).toLowerCase() + " or " + allowedInput.get(1).toLowerCase());
        }
    }

    public static AccountType getAccountTypeInput(Scanner sc, User user, Boolean newAccount) {
        StringBuilder print = new StringBuilder();
        StringBuilder invalidOutput = new StringBuilder("Invalid input. Please enter ");

        boolean userCheckingAccount = user.getCheckingAccount() != null;
        boolean userSavingAccount = user.getSavingsAccount() != null;
        boolean noUserAccount = !userCheckingAccount && !userSavingAccount && !newAccount;

        if (noUserAccount) {
            throw new RuntimeException("No accounts found for this user");
        }

        if (userCheckingAccount || newAccount) {
            print.append("1 - Checking Account");
            invalidOutput.append("1");
        }
        if (userSavingAccount || newAccount) {
            print.append("\n2 - Saving Account");
            invalidOutput.append(" or 2.");
        }
        System.out.println(print.toString());
        while (true) {
            String accountTypeInput = sc.nextLine().trim();
            if ((userCheckingAccount || newAccount) && accountTypeInput.equals("1")) {
                return AccountType.CHECKING_ACCOUNT;
            } else if ((userSavingAccount || newAccount) && accountTypeInput.equals("2")) {
                return AccountType.SAVINGS_ACCOUNT;
            } else {
                System.out.println(invalidOutput.toString());
            }
        }
    }

    public static CardType getCardTypeInput(Scanner sc) {
        System.out.println("""
                1 - Mastercard
                2 - Mastercard Platinum
                3 - Mastercard Titanium
                """);
        while (true) {
            String cardTypeInput = sc.nextLine().trim();
            switch (cardTypeInput) {
                case "1" -> {
                    return CardType.MASTERCARD;
                }
                case "2" -> {
                    return CardType.MASTERCARD_PLATINUM;
                }
                case "3" -> {
                    return CardType.MASTERCARD_TITANIUM;
                }
                default -> System.out.println("Invalid input. Please select 1, 2 or 3.");
            }
        }
    }

    public static double validDoubleInput(Scanner sc) {
        Double input = null;
        while (input == null) {
            try {
                input = sc.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
            }
        }
        sc.nextLine();
        return input;
    }

    public static User getCustomerSelectionInput(Scanner sc) {
        User toUser = null;
        String toUserID;
        while (toUser == null) {
            printAllCustomerIDAndName(true);
            toUserID = sc.nextLine();
            toUser = UserService.getCustomerById(toUserID);
            if (toUser == null) {
                System.out.println("Please enter one of the available IDs");
            }
        }
        return toUser;
    }
}
