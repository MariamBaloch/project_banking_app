package com.ga.acme.scenarios;

import com.ga.acme.enums.DateFilters;
import com.ga.acme.models.User;
import com.ga.acme.services.TransactionHistoryService;

import java.util.Scanner;

public class TransactionHistoryScenario {
    public static void handle(Scanner sc, User user) {
        Common.printHeader("TRANSACTION HISTORY");
        try {
            System.out.println("For which period would you like to view your transaction history?");
            DateFilters selectedFilter = getDateFilterInput(sc);
            TransactionHistoryService.printFilteredTransactions(user.getId(), selectedFilter);
        } catch (RuntimeException e) {
            if ("RETURN_TO_MENU".equals(e.getMessage())) {
                return;
            }
            System.out.println(e.getMessage());
        }
    }

    private static DateFilters getDateFilterInput(Scanner sc) {
        System.out.println("""
                1 - Today
                2 - Yesterday
                3 - Last Week
                4 - Last 7 Days
                5 - Last 30 Days
                6 - Last Month
                7 - View All History
                """);

        while (true) {
            String input = Common.readInputWithMenuEscape(sc).trim();
            switch (input) {
                case "1":
                    return DateFilters.TODAY;
                case "2":
                    return DateFilters.YESTERDAY;
                case "3":
                    return DateFilters.LAST_WEEK;
                case "4":
                    return DateFilters.LAST_7_DAYS;
                case "5":
                    return DateFilters.LAST_30_DAYS;
                case "6":
                    return DateFilters.LAST_MONTH;
                case "7":
                    return DateFilters.ALL_HISTORY;
                default:
                    System.out.println("Invalid input. Please enter a number from 1 to 7.");
            }
        }
    }
}