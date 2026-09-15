package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.models.User;
import com.ga.acme.services.TransactionHistoryService;

import java.util.Scanner;

import static com.ga.acme.scenarios.Common.*;

public class AccountStatementScenario {
    public static void handle(Scanner sc, User user) {
        try {
            System.out.println("Which account would you like to print the account statement for?");
            AccountType accountType = getAccountTypeInput(sc, user, false);

            TransactionHistoryService.printUserAccountStatement(user.getId(), accountType);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
