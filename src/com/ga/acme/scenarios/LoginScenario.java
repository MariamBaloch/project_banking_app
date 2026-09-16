package com.ga.acme.scenarios;

import com.ga.acme.exceptions.AccountLockedException;
import com.ga.acme.exceptions.InvalidPasswordException;
import com.ga.acme.models.User;
import com.ga.acme.services.AuthService;

import java.util.Scanner;

public class LoginScenario {
    public static ReturnType handle(Scanner sc, String userInput) {
        System.out.println("Enter your ID: ");
        String id = sc.nextLine().trim();

        System.out.println("Enter your Password: ");
        String password = sc.nextLine().trim();

        User user = null;

        try {
            user = AuthService.login(id, password);
            userInput = "";
        } catch (AccountLockedException | InvalidPasswordException e) {
            System.out.println(e.getMessage());
            userInput = "login";
        }
        return new ReturnType(user, userInput);
    }

    public record ReturnType(User user, String userInput) {
    }
}