package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.models.User;
import com.ga.acme.services.TransactionService;

import java.util.Scanner;

import static com.ga.acme.scenarios.Common.*;

public class WithdrawScenario {
    public static void handle(Scanner sc, User user) {
        Common.printHeader("WITHDRAW");
        try {
            System.out.println("Which account would you like to withdraw from?");
            AccountType accountType = getAccountTypeInput(sc, user, false);
            System.out.println("Which card would you like to use for making the withdrawal?");
            CardType cardType = getCardTypeInput(sc, accountType.equals(AccountType.CHECKING_ACCOUNT) ? user.getCheckingAccount() : user.getSavingsAccount());
            System.out.println("Enter the amount you would like to withdraw:");
            double amount = validDoubleInput(sc);

            TransactionService.withdraw(user.getId(), amount, accountType, cardType);
        } catch (RuntimeException e) {
            if ("RETURN_TO_MENU".equals(e.getMessage())) {
                return;
            }
            System.out.println(e.getMessage());
        }
    }
}
