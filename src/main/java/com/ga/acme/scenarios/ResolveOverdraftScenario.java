package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.models.User;
import com.ga.acme.services.TransactionService;
import com.ga.acme.services.UserService;

import java.util.Scanner;

import static com.ga.acme.scenarios.Common.getAccountTypeInput;
import static com.ga.acme.scenarios.Common.validDoubleInput;

public class ResolveOverdraftScenario {
    public static void handle(Scanner sc, String userid) {
        Common.printHeader("RESOLVE OVERDRAFT");
        User user = UserService.getUserById(userid);
        try {
            System.out.println("Which account would you like to resolve overdraft for?");
            AccountType accountType = getAccountTypeInput(sc, user.getId(), false);

            System.out.println("Enter the payment amount to resolve overdraft:");
            double amount = validDoubleInput(sc);

            TransactionService.resolveOverdraft(user.getId(), amount, accountType);
        } catch (RuntimeException e) {
            if ("RETURN_TO_MENU".equals(e.getMessage())) {
                return;
            }
            System.out.println(e.getMessage());
        }
    }
}
