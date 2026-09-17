package com.ga.acme.scenarios;

import com.ga.acme.models.User;
import com.ga.acme.services.UserService;

import java.util.Scanner;

public class UserProfileScenario {
    public static void handle(Scanner sc, String userid) {
        Common.printHeader("CUSTOMER PROFILE");
        User user = UserService.getUserById(userid);
        if (user == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        UserService.printUserProfile(user);
    }
}