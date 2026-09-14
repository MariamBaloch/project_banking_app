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
        User user = AuthService.getUserById(userId);
        HashMap<String, Map<String, String>> transactions = getDataFromFile(FilePath.CUSTOMER_TRANSACTIONS.getPath() + user.getId() + "-" + user.getName());
        Account account = accountType == AccountType.CHECKING_ACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();
        System.out.print("--------------------------------------ACME BANK ACCOUNT STATEMENT--------------------------------------\n\n");
        System.out.printf("%-15s %-25s %-15s %-25s %-10s", "Customer ID", "Customer Name", "Account ID", "Account Type", "Total Account Balance\n");
        System.out.printf("%-40s %-40s %-10s", "-------------------------------", "-------------------------------", "-------------------------------\n");
        System.out.printf("%-15s %-25s %-15s %-25s %-10s", user.getId(), user.getName(), account.getId(), accountType.getDisplayName(), "$" + account.getBalance() + "\n\n");
        System.out.println("----------------------------------------------TRANSACTIONS----------------------------------------------");

        Map<String, Map<String, String>> filteredTransactions = transactions.entrySet().stream()
                .filter(outerEntry -> accountType.getDisplayName().equals(outerEntry.getValue().get("accountType")))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        printUserTransactions(filteredTransactions);
    }

    public static void printFilteredTransactions(String userId, DateFilters dateFilter) {
        User user = AuthService.getUserById(userId);
        HashMap<String, Map<String, String>> transactions = getDataFromFile(FilePath.CUSTOMER_TRANSACTIONS.getPath() + user.getId() + "-" + user.getName());
        LocalDateTime today = LocalDateTime.now();

        System.out.println("----------------------------------------------TRANSACTIONS FOR " + dateFilter.getDisplayName().toUpperCase() + "----------------------------------------------");

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
        }
        return result;
    }


    public static void printUserTransactions(Map<String, Map<String, String>> transactions) {
        for (Map.Entry<String, Map<String, String>> entry : transactions.entrySet()) {
            System.out.print("[ Transaction ID" + ": " + entry.getKey() + ", ");
            String formattedDate = LocalDateTime.parse(entry.getValue().get("transactionDate")).format(DateTimeFormatter.ofPattern("dd-MMMM-yyyy HH:mm:ss"));
            System.out.print("Transaction Date" + ": " + formattedDate + ", ");
            System.out.print("Transaction Type" + ": " + entry.getValue().get("transactionType") + ", ");
            System.out.print("Account Type" + ": " + entry.getValue().get("accountType") + ", ");
            System.out.print("Card Used" + ": " + entry.getValue().get("cardType") + ", ");
            String toUser = entry.getValue().get("toUser");
            String toAccount = entry.getValue().get("toAccountType");
            if (toUser != null && toAccount != null) {
                System.out.print("To User" + ": " + toUser + ", ");
                System.out.print("To Account Type" + ": " + toAccount + ", ");
            }
            System.out.print("Balance Before" + ": $" + entry.getValue().get("balanceBefore") + ", ");
            System.out.print("Transaction Amount" + ": $" + entry.getValue().get("transactionAmount") + ", ");
            System.out.print("Balance After" + ": $" + entry.getValue().get("balanceAfter") + ", ");
            String overdraftAmount = entry.getValue().get("overdraftAmount").equals("null") ? "0.00$" : entry.getValue().get("overdraftAmount");
            System.out.println("Overdraft Amount" + ": $" + overdraftAmount + " ]");
            System.out.println("-----------------------------------------------------------------");
        }
    }
}