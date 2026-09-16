package com.ga.acme.scenarios;

import com.ga.acme.enums.Roles;
import com.ga.acme.exceptions.AccountAlreadyExistsException;
import com.ga.acme.services.AuthService;

import java.util.Scanner;

public class SignupScenario {
    public static void handle(Scanner sc) {
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
}