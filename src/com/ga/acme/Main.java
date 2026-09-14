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
                    user = AuthService.logout(user.getId());
                    break;
                case "1":
                    handleAddAccountScenario(sc, user);
                    break;
                default:
                    System.out.println("Invalid input");
                    userInput = sc.nextLine().toLowerCase().trim();
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
        String roleInput = sc.nextLine().trim();
        System.out.println("Enter ID: ");
        String id = sc.nextLine().trim();
        System.out.println("Enter you name: ");
        String name = sc.nextLine().trim();
        System.out.println("Enter Password: ");
        String password = sc.nextLine().trim();

        //TODO idk what to do for invald input
        Roles role = roleInput.equals("1") ? Roles.CUSTOMER : Roles.BANKER;

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
                """);
    }

    private static void handleAddAccountScenario(Scanner sc, IUser user) {
        System.out.println("Which type of account would you like to add?");
        System.out.println("""
                1 - Checking Account
                2 - Savings Account
                """);
        String accountTypeInput = sc.nextLine().trim();
        System.out.println("Would you like to add Mastercard for this account? yes | no");
        Boolean mastercard = sc.nextLine().trim().equals("yes");
        System.out.println("Would you like to add Mastercard Platinum for this account? yes | no");
        Boolean mastercardPlantinum = sc.nextLine().trim().equals("yes");
        System.out.println("Would you like to add Mastercard Titanium for this account? yes | no");
        Boolean mastercardTitanuium = sc.nextLine().trim().equals("yes");

        //TODO idk what to do for invald input
        AccountType accountType = accountTypeInput.equals("1") ? AccountType.CHECKINGACCOUNT : AccountType.SAVINGSACCOUNT;
        AccountService.addAccount(user.getId(), accountType, mastercard, mastercardPlantinum, mastercardTitanuium);
    }

    private record LoginScenarioReturn(IUser user, String userInput) {
    }


}