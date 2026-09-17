package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.models.Account;
import com.ga.acme.models.User;
import com.ga.acme.services.UserService;

import java.util.List;
import java.util.Scanner;

import static com.ga.acme.services.UserService.getUserById;
import static com.ga.acme.services.UserService.printAllCustomerIDAndName;

public class Common {
    public static void printHeader(String title) {
        int width = 70;
        String line = "=".repeat(width);
        System.out.println(line);
        System.out.printf("%" + ((width - title.length()) / 2 + title.length()) + "s%n", title);
        System.out.println(line);
    }

    public static boolean isMenuEscape(String input) {
        return input.equalsIgnoreCase("menu");
    }

    public static String readInputWithMenuEscape(Scanner sc) {
        String input = sc.nextLine().trim();
        if (isMenuEscape(input)) {
            throw new RuntimeException("RETURN_TO_MENU");
        }
        return input;
    }

    public static void printInitialCustomerMenu() {
        System.out.println("\nPlease select an operation to perform:");
        System.out.println("""
                1 - Add account
                2 - Deposit money
                3 - Withdraw money
                4 - Transfer money
                5 - Resolve Overdraft
                6 - View Transaction History
                7 - View Account Statement
                8 - View User Profile
                9 - Logout
                
                Type 'menu' at any prompt to return to the main menu
                Type 'exit' to shut down system
                """);
    }

    public static void printInitialBankerMenu() {
        System.out.println("\nPlease select an operation to perform:");
        System.out.println("""
                1 - View all customers
                2 - Logout
                
                Type 'menu' at any prompt to return to the main menu
                Type 'exit' to shut down system
                """);
    }

    public static Boolean convertResponseToBoolean(Scanner sc, List<String> allowedInput) {
        while (true) {
            String input = readInputWithMenuEscape(sc).toLowerCase();
            if (input.equals(allowedInput.get(0).toLowerCase())) {
                return true;
            }
            if (input.equals(allowedInput.get(1).toLowerCase())) {
                return false;
            }
            System.out.println("Invalid input. Please enter " + allowedInput.get(0).toLowerCase() + " or " + allowedInput.get(1).toLowerCase());
        }
    }

    public static AccountType getAccountTypeInput(Scanner sc, String userId, Boolean newAccount) {
        StringBuilder print = new StringBuilder();
        StringBuilder invalidOutput = new StringBuilder("Invalid input. Please enter ");
        User user = getUserById(userId);

        boolean userCheckingAccount = user.getCheckingAccount() != null;
        boolean userSavingAccount = user.getSavingsAccount() != null;
        boolean noUserAccount = !userCheckingAccount && !userSavingAccount && !newAccount;

        if (noUserAccount) {
            throw new RuntimeException("No accounts found for this user");
        }

        if (userCheckingAccount || newAccount) {
            print.append("1 - Checking Account\n");
            invalidOutput.append("1");
        }
        if (userSavingAccount || newAccount) {
            print.append("2 - Saving Account");
            invalidOutput.append(" or 2.");
        }
        System.out.println(print.toString());
        while (true) {
            String accountTypeInput = readInputWithMenuEscape(sc);
            if ((userCheckingAccount || newAccount) && accountTypeInput.equals("1")) {
                return AccountType.CHECKING_ACCOUNT;
            } else if ((userSavingAccount || newAccount) && accountTypeInput.equals("2")) {
                return AccountType.SAVINGS_ACCOUNT;
            } else {
                System.out.println(invalidOutput.toString());
            }
        }
    }

    public static CardType getCardTypeInput(Scanner sc, Account account) {
        boolean hasMastercard = account.getMastercard() != null;
        boolean hasMastercardPlatinum = account.getMastercardPlatinum() != null;
        boolean hasMastercardTitanium = account.getMastercardTitanium() != null;

        if (!hasMastercard && !hasMastercardPlatinum && !hasMastercardTitanium) {
            throw new RuntimeException("No cards found for this account");
        }

        StringBuilder print = new StringBuilder();
        StringBuilder invalidOutput = new StringBuilder("Invalid input. Please enter ");


        if (hasMastercard) {
            print.append("1 - Mastercard\n");
            invalidOutput.append("1");
        }
        if (hasMastercardPlatinum) {
            print.append("2 - Mastercard Platinum\n");
            invalidOutput.append("or 2");
        }
        if (hasMastercardTitanium) {
            print.append("3 - Mastercard Titanium\n");
            invalidOutput.append("or 3");
        }
        System.out.println(print.toString());
        while (true) {
            String cardTypeInput = readInputWithMenuEscape(sc);
            if (hasMastercard && cardTypeInput.equals("1")) {
                return CardType.MASTERCARD;
            } else if (hasMastercardPlatinum && cardTypeInput.equals("2")) {
                return CardType.MASTERCARD_PLATINUM;
            } else if (hasMastercardTitanium && cardTypeInput.equals("3")) {
                return CardType.MASTERCARD_TITANIUM;
            } else {
                System.out.println(invalidOutput.toString());
            }
        }
    }

    public static double validDoubleInput(Scanner sc) {
        while (true) {
            String input = readInputWithMenuEscape(sc);
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static User getCustomerSelectionInput(Scanner sc, boolean excludeLoggedIn) {
        User toUser = null;
        String toUserID;
        while (toUser == null) {
            printAllCustomerIDAndName(excludeLoggedIn);
            toUserID = readInputWithMenuEscape(sc);
            toUser = UserService.getCustomerById(toUserID);
            if (toUser == null) {
                System.out.println("Please enter one of the available IDs");
            }
        }
        return toUser;
    }
}