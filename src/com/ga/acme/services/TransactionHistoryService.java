package com.ga.acme.services;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.DateFilters;
import com.ga.acme.enums.FilePath;
import com.ga.acme.models.Account;
import com.ga.acme.models.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ga.acme.util.FileHandler.getDataFromFile;


public class TransactionHistoryService {
    public static void printUserAccountStatement(String userId, AccountType accountType) {
        User user = UserService.getUserById(userId);
        HashMap<String, Map<String, String>> transactions = getDataFromFile(FilePath.CUSTOMER_TRANSACTIONS.getPath() + user.getId() + "-" + user.getName());
        Account account = accountType == AccountType.CHECKING_ACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();

        System.out.println("=================================================================");
        System.out.println("                      ACME BANK ACCOUNT STATEMENT");
        System.out.println("=================================================================");
        System.out.printf("%s: %s%n", "Customer ID", user.getId());
        System.out.printf("%s: %s%n", "Customer Name", user.getName());
        System.out.printf("%s: %s%n", "Account ID", account.getId());
        System.out.printf("%s: %s%n", "Account Type", accountType.getDisplayName());
        System.out.printf("%s: $%.2f%n", "Total Account Balance", account.getBalance());
        System.out.println("=================================================================");

        Map<String, Map<String, String>> filteredTransactions = transactions.entrySet().stream()
                .filter(outerEntry -> accountType.getDisplayName().equals(outerEntry.getValue().get("accountType")))
                .sorted((e1, e2) -> {
                    LocalDateTime date1 = LocalDateTime.parse(e1.getValue().get("transactionDate"));
                    LocalDateTime date2 = LocalDateTime.parse(e2.getValue().get("transactionDate"));
                    return date1.compareTo(date2);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        System.out.println("TRANSACTIONS");
        System.out.println("-----------------------------------------------------------------");
        printUserTransactions(filteredTransactions);
    }

    public static void printFilteredTransactions(String userId, DateFilters dateFilter) {
        User user = UserService.getUserById(userId);
        HashMap<String, Map<String, String>> transactions = getDataFromFile(FilePath.CUSTOMER_TRANSACTIONS.getPath() + user.getId() + "-" + user.getName());

        System.out.println("=================================================================");
        System.out.println("                 TRANSACTIONS FOR " + dateFilter.getDisplayName().toUpperCase());
        System.out.println("=================================================================");

        Map<String, Map<String, String>> filteredTransactions = transactions.entrySet().stream()
                .filter(outerEntry -> applyDateFilter(LocalDateTime.parse(outerEntry.getValue().get("transactionDate")), dateFilter))
                .sorted((e1, e2) -> {
                    LocalDateTime date1 = LocalDateTime.parse(e1.getValue().get("transactionDate"));
                    LocalDateTime date2 = LocalDateTime.parse(e2.getValue().get("transactionDate"));
                    return date1.compareTo(date2);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));

        printUserTransactions(filteredTransactions);
    }

    public static boolean applyDateFilter(LocalDateTime dateTime, DateFilters dateFilter) {
        boolean result = false;

        switch (dateFilter) {
            case TODAY:
                result = dateTime.toLocalDate().equals(LocalDate.now());
                break;
            case YESTERDAY:
                result = dateTime.toLocalDate().isEqual(LocalDate.now().minusDays(1));
                break;
            case LAST_WEEK:
                LocalDateTime previousWeekStartDate = LocalDateTime.now()
                        .minusWeeks(1)
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                        .with(LocalTime.MIN);

                LocalDateTime startDateOfWeek = LocalDateTime.now()
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                        .with(LocalTime.MIN);

                result = (dateTime.isAfter(previousWeekStartDate) || dateTime.isEqual(previousWeekStartDate)) && dateTime.isBefore(startDateOfWeek);
                break;
            case LAST_7_DAYS:
                result = dateTime.isAfter(LocalDateTime.now().minusDays(8));
                break;
            case LAST_30_DAYS:
                result = dateTime.isAfter(LocalDateTime.now().minusMonths(1).minusDays(1));
                break;
            case LAST_MONTH:
                LocalDateTime previousMonthStartDate = LocalDateTime.now()
                        .minusMonths(1)
                        .with(TemporalAdjusters.firstDayOfMonth())
                        .with(LocalTime.MIN);

                LocalDateTime startDateOfMonth = LocalDateTime.now()
                        .with(TemporalAdjusters.firstDayOfMonth())
                        .with(LocalTime.MIN);

                result = (dateTime.isAfter(previousMonthStartDate) || dateTime.isEqual(previousMonthStartDate)) && dateTime.isBefore(startDateOfMonth);
                break;
            case ALL_HISTORY:
                result = true;
                break;
        }
        return result;
    }


    public static void printUserTransactions(Map<String, Map<String, String>> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            System.out.println("No transactions found for this selection.");
            return;
        }

        for (Map.Entry<String, Map<String, String>> entry : transactions.entrySet()) {
            String formattedDate = LocalDateTime.parse(entry.getValue().get("transactionDate"))
                    .format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm:ss"));
            String overdraftAmount = entry.getValue().get("overdraftAmount").equals("null") ? "0.00" : entry.getValue().get("overdraftAmount");
            String toUser = entry.getValue().get("toUser");
            String toAccount = entry.getValue().get("toAccountType");

            System.out.println("--------------------------------------------------------------");
            System.out.printf("Transaction ID   : %s%n", entry.getKey());
            System.out.printf("Date             : %s%n", formattedDate);
            System.out.printf("Type             : %s%n", entry.getValue().get("transactionType"));
            System.out.printf("Account Type     : %s%n", entry.getValue().get("accountType"));
            System.out.printf("Card Used        : %s%n", entry.getValue().get("cardType"));
            if (!toUser.equals("null") && !toAccount.equals("null")) {
                System.out.printf("To User          : %s%n", toUser);
                System.out.printf("To Account Type  : %s%n", toAccount);
            }
            System.out.printf("Balance Before   : $%.2f%n", Double.parseDouble(entry.getValue().get("balanceBefore")));
            System.out.printf("Transaction Amt  : $%.2f%n", Double.parseDouble(entry.getValue().get("transactionAmount")));
            System.out.printf("Balance After    : $%.2f%n", Double.parseDouble(entry.getValue().get("balanceAfter")));
            System.out.printf("Overdraft Amount : $%.2f%n", Double.parseDouble(overdraftAmount));
        }
        System.out.println("=================================================================");
    }
}