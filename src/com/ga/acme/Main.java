package com.ga.acme;


import com.ga.acme.models.User;
import com.ga.acme.scenarios.AddAccountScenario;
import com.ga.acme.scenarios.DepositScenario;
import com.ga.acme.scenarios.LoginScenario;
import com.ga.acme.scenarios.SignupScenario;
import com.ga.acme.services.AuthService;

import java.util.Scanner;

import static com.ga.acme.scenarios.Common.printInitialMenu;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String userInput = "";
        User user = AuthService.getLoggInUser();


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
                    LoginScenario.LoginScenarioReturn loginScenarioReturn = LoginScenario.handle(sc, userInput);
                    user = loginScenarioReturn.user();
                    userInput = loginScenarioReturn.userInput();
                    break;
                case "signup":
                    SignupScenario.handle(sc);
                    break;
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
                    // handle withdraw scenario
                    break;
                case "4":
                case "transfer":
                    // handle transfer scenario
                    break;
                case "5":
                case "transactionhistory":
                    // handle history scenario
                    break;
                case "6":
                case "accountstatement":
                    // handle accstatement scenario
                    break;
                case "7":
                case "logout":
                    user = AuthService.logout(user.getId());
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