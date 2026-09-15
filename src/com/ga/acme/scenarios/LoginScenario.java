package com.ga.acme.scenarios;

import com.ga.acme.exceptions.AccountLockedException;
import com.ga.acme.exceptions.InvalidPasswordException;
import com.ga.acme.models.User;
import com.ga.acme.services.AuthService;

import java.util.Scanner;

public class LoginScenario {
    public static LoginScenarioReturn handle(Scanner sc, String userInput) {
        System.out.println("Enter your ID: ");
        String id = sc.nextLine().trim();

        System.out.println("Enter your Password: ");
        String password = sc.nextLine().trim();

        User user = null;

        try {
            user = AuthService.login(id, password);
        } catch (AccountLockedException | InvalidPasswordException e) {
            System.out.println(e.getMessage());
            userInput = "login";
        }
        return new LoginScenarioReturn(user, userInput);
    }

    public record LoginScenarioReturn(User user, String userInput) {
    }
}
