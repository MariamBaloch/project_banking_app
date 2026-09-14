package com.ga.acme;


import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.exceptions.AccountLockedException;
import com.ga.acme.exceptions.InvalidPasswordException;
import com.ga.acme.interfaces.IUser;
import com.ga.acme.services.AccountService;
import com.ga.acme.services.AuthService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String userInput = "";
        IUser user = AuthService.getLoggInUser();


        if (user != null) {
            System.out.println("Welcome to ACME Banking System " + user.getName());
        } else {
            System.out.println("Welcome to ACME Banking System");
        }
        while (!userInput.equals("exit")) {

            if (user == null) {
                System.out.println("Login or Signup to the system");
            } else {
                printInitialMenu();
            }
            userInput = sc.nextLine().toLowerCase().trim();

            switch (userInput) {
                case "login":
                    LoginScenarioReturn loginScenarioReturn = handleLoginScenario(sc, userInput);
                    user = loginScenarioReturn.user();
                    userInput = loginScenarioReturn.userInput();
                    break;
                case "signup":
                    handleSignUpScenario(sc);
                    break;
                case "logout":
                case "7":
                    user = AuthService.logout(user.getId());
                    break;
                case "1":
                    handleAddAccountScenario(sc, user);
                    break;
                default:
                    if (!userInput.equals("exit")) {
                        System.out.println("Invalid input");
                    }
                    break;
            }
        }
    }

    private static void handleSignUpScenario(Scanner sc) {
        System.out.println("Sign up to the system");
        System.out.println("""
                Select Role:
                1 - Customer
                2 - Banker
                """);

        Roles role = null;
        while (role == null) {
            String roleInput = sc.nextLine().trim();

            switch (roleInput) {
                case "1" -> role = Roles.CUSTOMER;
                case "2" -> role = Roles.BANKER;
                default -> System.out.println("Invalid role. Please select 1 or 2.");
            }
        }
        System.out.println("Enter ID: ");
        String id = sc.nextLine().trim();
        System.out.println("Enter you name: ");
        String name = sc.nextLine().trim();
        System.out.println("Enter Password: ");
        String password = sc.nextLine().trim();


        try {
            AuthService.signup(id, name, password, role);
        } catch (AccountAlreadyExistsException e) {
            System.out.println(e.getMessage());
        }
    }

    private static LoginScenarioReturn handleLoginScenario(Scanner sc, String userInput) {
        System.out.println("Enter your ID: ");
        String id = sc.nextLine().trim();

        System.out.println("Enter your Password: ");
        String password = sc.nextLine().trim();

        IUser user = null;

        try {
            user = AuthService.login(id, password);
        } catch (AccountLockedException | InvalidPasswordException e) {
            System.out.println(e.getMessage());
            userInput = "login";
        }
        return new LoginScenarioReturn(user, userInput);
    }

    private static void printInitialMenu() {
        System.out.println("Please select an operation to perform:");
        System.out.println("""
                1 - Add account
                2 - Withdraw money
                3 - Deposit money
                4 - Transfer money
                5 - View Transaction History
                6 - View Account Statement
                7 - Logout
                
                Type 'exit' to shut down system
                """);
    }

    private static void handleAddAccountScenario(Scanner sc, IUser user) {
        System.out.println("Which type of account would you like to add?");
        System.out.println("""
                1 - Checking Account
                2 - Savings Account
                """);

        AccountType accountType = null;
        while (accountType == null) {
            String accountTypeInput = sc.nextLine().trim();
            switch (accountTypeInput) {
                case "1" -> accountType = AccountType.CHECKING_ACCOUNT;
                case "2" -> accountType = AccountType.SAVINGS_ACCOUNT;
                default -> System.out.println("Invalid input. Please select 1 or 2.");
            }
        }
        System.out.println("Would you like to add Mastercard for this account? yes | no");
        Boolean mastercard = getYesNoInput(sc);
        System.out.println("Would you like to add Mastercard Platinum for this account? yes | no");
        Boolean mastercardPlatinum = getYesNoInput(sc);
        System.out.println("Would you like to add Mastercard Titanium for this account? yes | no");
        Boolean mastercardTitanium = getYesNoInput(sc);
        AccountService.addAccount(user.getId(), accountType, mastercard, mastercardPlatinum, mastercardTitanium);
    }

    private static Boolean getYesNoInput(Scanner sc) {
        while (true) {
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("yes")) {
                return true;
            }
            if (input.equals("no")) {
                return false;
            }
            System.out.println("Invalid input. Please enter yes or no.");
        }
    }

    private record LoginScenarioReturn(IUser user, String userInput) {
    }


}