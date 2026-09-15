package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.models.User;
import com.ga.acme.services.TransactionService;

import java.util.List;
import java.util.Scanner;

import static com.ga.acme.scenarios.Common.*;

public class DepositScenario {
    public static void handle(Scanner sc, User user) {
        System.out.println("Would you like to deposit to your own account or another customer's account?");
        System.out.println("""
                1 - Own Account
                2 - Another Customer's account
                """);
        Boolean ownAccountDeposit = convertResponseToBoolean(sc, List.of("1", "2"));
        String depositPrompt = ownAccountDeposit ? "Which account would you like to deposit to?" : "Which account would you like to make the deposit from?";
        System.out.println(depositPrompt);
        AccountType accountType = null;
        try {
            accountType = getAccountTypeInput(sc, user, false);
            System.out.println("Which card would you like to use for making the deposit?");
            CardType cardType = getCardTypeInput(sc, accountType.equals(AccountType.CHECKING_ACCOUNT) ? user.getCheckingAccount() : user.getSavingsAccount());
            System.out.println("Enter the amount you would like to deposit:");
            double amount = validDoubleInput(sc);

            if (ownAccountDeposit) {
                TransactionService.deposit(user.getId(), amount, accountType, cardType);
            } else {
                System.out.println("Enter ID of user you would like to make the deposit to");
                String toUserID;
                User toUser = getCustomerSelectionInput(sc);
                System.out.println("To which account for " + toUser.getName() + " would you like to deposit to?");
                AccountType toAccountType = null;
                toAccountType = getAccountTypeInput(sc, toUser, false);
                TransactionService.transfer(user.getId(), amount, accountType, cardType, toUser, toAccountType, true);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}