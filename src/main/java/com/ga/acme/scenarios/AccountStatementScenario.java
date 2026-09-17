package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.models.User;
import com.ga.acme.services.TransactionHistoryService;
import com.ga.acme.services.UserService;
import com.ga.acme.util.AccountStatementPDF;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.ga.acme.scenarios.Common.getAccountTypeInput;

public class AccountStatementScenario {
    public static void handle(Scanner sc, String userid) {
        Common.printHeader("ACCOUNT STATEMENT");
        User user = UserService.getUserById(userid);
        try {
            System.out.println("Which account would you like to print the account statement for?");
            AccountType accountType = getAccountTypeInput(sc, user.getId(), false);
            TransactionHistoryService.printUserAccountStatement(user.getId(), accountType);
            System.out.println("Would you like to generate a PDF of the account statement? yes | no");

            Map<String, Map<String, String>> transactions = TransactionHistoryService.getUserAccountTransactions(user.getId(), accountType);
            if (!transactions.isEmpty()) {
                Boolean generatePDF = Common.convertResponseToBoolean(sc, List.of("yes", "no"));
                if (generatePDF) {
                    AccountStatementPDF.generatePDF(transactions, user.getId(), accountType);
                }
            }
        } catch (RuntimeException e) {
            if ("RETURN_TO_MENU".equals(e.getMessage())) {
                return;
            }
            System.out.println(e.getMessage());
        }
    }
}
