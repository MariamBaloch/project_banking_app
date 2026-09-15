package com.ga.acme.scenarios;

import com.ga.acme.models.User;
import com.ga.acme.services.UserService;

import java.util.Scanner;

public class UserProfileScenario {
    public static void handle(Scanner sc, User user) {
        Common.printHeader("CUSTOMER PROFILE");
        if (user == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        UserService.printUserProfile(user);
    }
}