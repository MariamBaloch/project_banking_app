package com.ga.acme;


import com.ga.acme.enums.Roles;
import com.ga.acme.models.User;
import com.ga.acme.scenarios.*;
import com.ga.acme.services.AuthService;

import java.util.Scanner;

import static com.ga.acme.scenarios.Common.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String userInput = "";
        User user = AuthService.getLoggedInUser();

        if (user != null) {
            printHeader("Welcome to ACME Banking System " + user.getName());
        } else {
            printHeader("Welcome to ACME Banking System");
        }

        while (!userInput.equals("exit")) {

            if (user == null) {
                System.out.println("""
                        1 - Login
                        2 - Signup
                        """);
            } else {
                if (user.getRole().equals(Roles.CUSTOMER)) {
                    printInitialCustomerMenu();
                } else {
                    printInitialBankerMenu();
                }
            }

            userInput = sc.nextLine().toLowerCase().trim();

            if (user == null) {
                switch (userInput) {
                    case "1":
                    case "login":
                        LoginScenario.ReturnType loginScenarioReturn = LoginScenario.handle(sc, userInput);
                        user = loginScenarioReturn.user();
                        userInput = loginScenarioReturn.userInput();
                        break;
                    case "2":
                    case "signup":
                        SignupScenario.handle(sc);
                        break;
                    case "menu":
                        break;
                    default:
                        if (!userInput.equals("exit")) {
                            System.out.println("Invalid input");
                        }
                        break;
                }
            }
            if (!userInput.isEmpty() && user != null) {
                if (user.getRole().equals(Roles.CUSTOMER)) {
                    switch (userInput) {
                        case "1":
                        case "addaccount":
                            AddAccountScenario.handle(sc, user);
                            break;
                        case "2":
                        case "deposit":
                            DepositScenario.handle(sc, user);
                            break;
                        case "3":
                        case "withdraw":
                            WithdrawScenario.handle(sc, user);
                            break;
                        case "4":
                        case "transfer":
                            TransferScenario.handle(sc, user);
                            break;
                        case "5":
                        case "resolveoverdraft":
                            ResolveOverdraftScenario.handle(sc, user);
                            break;
                        case "6":
                        case "transactionhistory":
                            TransactionHistoryScenario.handle(sc, user);
                            break;
                        case "7":
                        case "accountstatement":
                            AccountStatementScenario.handle(sc, user);
                            break;
                        case "8":
                        case "profile":
                            UserProfileScenario.handle(sc, user);
                            break;
                        case "9":
                        case "logout":
                            user = AuthService.logout(user);
                            break;
                        case "menu":
                            break;
                        default:
                            if (!userInput.equals("exit")) {
                                System.out.println("Invalid input");
                            }
                            break;
                    }
                } else if (user.getRole().equals(Roles.BANKER)) {
                    switch (userInput) {
                        case "1":
                        case "customers":
                            ViewCustomerScenario.handle(sc);
                            break;
                        case "2":
                        case "logout":
                            user = AuthService.logout(user);
                            break;
                        case "menu":
                            break;
                        default:
                            if (!userInput.equals("exit")) {
                                System.out.println("Invalid input");
                            }
                            break;
                    }
                }
            }

        }
    }
}