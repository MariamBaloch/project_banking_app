package com.ga.acme.scenarios;

import com.ga.acme.enums.AccountType;
import com.ga.acme.models.User;
import com.ga.acme.services.AccountService;

import java.util.List;
import java.util.Scanner;

import static com.ga.acme.scenarios.Common.convertResponseToBoolean;
import static com.ga.acme.scenarios.Common.getAccountTypeInput;

public class AddAccountScenario {
    public static void handle(Scanner sc, User user) {
        Common.printHeader("ADD ACCOUNT");
        try {
            System.out.println("Which type of account would you like to add?");
            AccountType accountType = getAccountTypeInput(sc, user.getId(), true);

            List<String> allowedInput = List.of("yes", "no");

            System.out.println("Would you like to add Mastercard for this account? yes | no");
            Boolean mastercard = convertResponseToBoolean(sc, allowedInput);

            System.out.println("Would you like to add Mastercard Platinum for this account? yes | no");
            Boolean mastercardPlatinum = convertResponseToBoolean(sc, allowedInput);

            System.out.println("Would you like to add Mastercard Titanium for this account? yes | no");
            Boolean mastercardTitanium = convertResponseToBoolean(sc, allowedInput);

            AccountService.addAccount(user.getId(), accountType, mastercard, mastercardPlatinum, mastercardTitanium);
        } catch (RuntimeException e) {
            if ("RETURN_TO_MENU".equals(e.getMessage())) {
                return;
            }
            System.out.println(e.getMessage());
        }
    }
}
