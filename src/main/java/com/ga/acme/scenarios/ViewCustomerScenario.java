package com.ga.acme.scenarios;

import com.ga.acme.models.User;
import com.ga.acme.services.UserService;

import java.util.Scanner;

import static com.ga.acme.scenarios.Common.getCustomerSelectionInput;

public class ViewCustomerScenario {
    public static void handle(Scanner sc) {
        try {
            Common.printHeader("ALL CUSTOMERS");
            System.out.println("\nType an ID to view more details for that customer\n");
            User user = getCustomerSelectionInput(sc, false);
            UserService.printUserProfile(user);
        } catch (RuntimeException e) {
            if ("RETURN_TO_MENU".equals(e.getMessage())) {
                return;
            }
            System.out.println(e.getMessage());
        }
    }
}
