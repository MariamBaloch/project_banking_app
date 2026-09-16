package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.models.User;
import com.ga.acme.services.TransactionService;

import java.util.List;
import java.util.Scanner;

import static com.ga.acme.scenarios.Common.*;

public class TransferScenario {
    public static void handle(Scanner sc, User user) {
        Common.printHeader("TRANSFER");
        try {
            System.out.println("Would you like to transfer to your own account or another customer's account?");
            System.out.println("""
                    1 - Own Account
                    2 - Another Customer's account
                    """);
            Boolean ownAccountTransfer = convertResponseToBoolean(sc, List.of("1", "2"));

            System.out.println("Which account would you like to transfer from?");
            AccountType fromAccountType = getAccountTypeInput(sc, user, false);

            System.out.println("Which card would you like to use for making the transfer?");
            CardType cardType = getCardTypeInput(sc, fromAccountType.equals(AccountType.CHECKING_ACCOUNT) ? user.getCheckingAccount() : user.getSavingsAccount());

            System.out.println("Enter the amount you would like to transfer:");
            double amount = validDoubleInput(sc);

            if (ownAccountTransfer) {
                System.out.println("To which of your accounts would you like to transfer to?");
                AccountType toAccountType = getAccountTypeInput(sc, user, false);
                TransactionService.transfer(user.getId(), amount, fromAccountType, cardType, user, toAccountType, false);
            } else {
                System.out.println("Enter ID of user you would like to transfer to");
                User toUser = getCustomerSelectionInput(sc, true);
                System.out.println("To which account for " + toUser.getName() + " would you like to transfer to?");
                AccountType toAccountType = getAccountTypeInput(sc, toUser, false);
                TransactionService.transfer(user.getId(), amount, fromAccountType, cardType, toUser, toAccountType, false);
            }
        } catch (RuntimeException e) {
            if ("RETURN_TO_MENU".equals(e.getMessage())) {
                return;
            }
            System.out.println(e.getMessage());
        }
    }
}