package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.models.User;
import com.ga.acme.services.TransactionService;

import java.util.Scanner;

import static com.ga.acme.scenarios.Common.*;

public class ResolveOverdraftScenario {
    public static void handle(Scanner sc, User user) {
        Common.printHeader("RESOLVE OVERDRAFT");
        try {
            System.out.println("Which account would you like to resolve overdraft for?");
            AccountType accountType = getAccountTypeInput(sc, user, false);

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
