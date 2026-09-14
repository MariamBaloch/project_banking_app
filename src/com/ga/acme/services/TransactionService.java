package com.ga.acme.services;

import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;
import com.ga.acme.enums.FilePath;
import com.ga.acme.enums.TransactionType;
import com.ga.acme.exceptions.*;
import com.ga.acme.interfaces.ICard;
import com.ga.acme.models.Account;
import com.ga.acme.models.TransactionRecord;
import com.ga.acme.models.User;
import com.ga.acme.util.FileHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.ga.acme.services.AccountService.getVerifiedAccount;
import static com.ga.acme.services.CardService.getCardByTypeForAccount;

public class TransactionService {

    public static final int OVERDRAFT_LIMIT = 2;
    public static final double OVERDRAFT_AMOUNT = 35;


    public static void initialChecks(User user, AccountType accountType, CardType cardType) throws AccountNotLoggedInException, AccountTypeNotSupportedException, RecordNotFoundException, CardNotSupportedException {
        if (!user.getIsLoggedIn()) {
            throw new AccountNotLoggedInException();
        }
        if (accountType != AccountType.CHECKING_ACCOUNT && accountType != AccountType.SAVINGS_ACCOUNT) {
            throw new AccountTypeNotSupportedException();
        }

        Account account = accountType == AccountType.CHECKING_ACCOUNT ? user.getCheckingAccount() : user.getSavingsAccount();

        if (account == null) {
            String outputAccountType = accountType == AccountType.SAVINGS_ACCOUNT ? "savings account" : "checking account";
            throw new RecordNotFoundException("No " + outputAccountType + " found for user: " + user.getName());
        }

        if (cardType != null) {
            boolean isCardSupported = switch (cardType) {
                case MASTERCARD -> account.getMastercard() != null;
                case MASTERCARD_PLATINUM -> account.getMastercardPlatinum() != null;
                case MASTERCARD_TITANIUM -> account.getMastercardTitanium() != null;
            };

            if (!isCardSupported) {
                throw new CardNotSupportedException();
            }
        }

    }

    public static void withdraw(String userId, double amount, AccountType accountType, CardType cardType) {
        AccountService.VerifiedAccountResult verifiedAccountResult = getVerifiedAccount(userId, accountType, cardType);
        if (verifiedAccountResult == null) {
            return;
        }
        Account account = verifiedAccountResult.account();
        User user = verifiedAccountResult.user();

        ICard card = getCardByTypeForAccount(account, cardType);
        try {
            if (account.getBalance() >= 0 && account.getBalance() - amount < -100) {
                throw new InsufficientAmountException("You do not have enough balance for this transaction");
            }

            handleDailyLimits(card, amount, card.getDailyWithdrawn(), card.getWithdrawLimitPerDay(), "Withdraw");
            if (account.isLocked()) {
                throw new AccountLockedException(
                        "Account is locked due to reaching overdraft limit. Pay off your negative balance of $" + Math.abs(account.getBalance()) + " and $" + account.getOverdraftAmount() + " overdraft fee to unlock.");
            }
            if (account.getBalance() < 0 && amount > 100) {
                throw new WithdrawLimitException(
                        "Negative balance, withdrawals are limited to $100. Please enter a lower amount.");
            }

            TransactionRecord.Builder transactionBuilder = new TransactionRecord.Builder().
                    setBalanceBefore(account.getBalance());

            account.withdraw(amount);
            card.setDailyWithdrawn(card.getDailyWithdrawn() + amount);

            if (account.getBalance() < 0) {
                account.setOverdraftAmount(account.getOverdraftAmount() + OVERDRAFT_AMOUNT);
                account.setOverdrafts(account.getOverdrafts() + 1);
                System.out.println("Negative balance, overdraft fee of $" + OVERDRAFT_AMOUNT + " charged. " + "Total overdraft fees owed: $" + account.getOverdraftAmount());
                if (account.getOverdrafts() >= OVERDRAFT_LIMIT) {
                    account.setLocked(true);
                    System.out.println("Account has been locked due to reaching overdraft limit. " + "Resolve your negative balance of $" + Math.abs(account.getBalance()) + " and pay $" + account.getOverdraftAmount() + " to unlock account.");
                }
            }

            TransactionRecord transactionRecord = transactionBuilder.
                    setTransactionType(TransactionType.WITHDRAWAL).
                    setAccountType(accountType).
                    setBalanceAfter(account.getBalance()).
                    setTransactionAmount(amount).
                    setCardType(cardType).
                    setOverdraftAmount(account.getOverdraftAmount()).
                    setTransactionDate(LocalDateTime.now()).build();

            writeTransactionToFile(user, transactionRecord);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
            FileHandler.updateLineInFile(FilePath.CARDS.getPath(), card.getId(), card.toString());
        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void resolveOverdraft(String userId, double paymentAmount, AccountType accountType) {
        AccountService.VerifiedAccountResult verifiedAccountResult = getVerifiedAccount(userId, accountType, null);
        if (verifiedAccountResult == null) {
            return;
        }
        Account account = verifiedAccountResult.account();
        User user = verifiedAccountResult.user();
        try {
            double negativeBalance = account.getBalance() < 0 ? Math.abs(account.getBalance()) : 0;
            double totalDebt = negativeBalance + account.getOverdraftAmount();

            if (totalDebt == 0) {
                System.out.println("No overdraft resolution needed.");
                return;
            }

            if (paymentAmount < totalDebt) {
                throw new InsufficientAmountException("Insufficient amount provided. Amount owed $" + totalDebt + " (negative balance: $" + negativeBalance + " + overdraft fees: $" + account.getOverdraftAmount() + ")."
                );
            }

            double surplus = paymentAmount - totalDebt;

            TransactionRecord.Builder transactionBuilder = new TransactionRecord.Builder().
                    setBalanceBefore(account.getBalance());

            account.setBalance(surplus);
            account.setOverdraftAmount(0);
            account.setOverdrafts(0);
            account.setLocked(false);

            TransactionRecord transactionRecord = transactionBuilder.
                    setTransactionType(TransactionType.OVERDRAFT_RESOLUTION).
                    setAccountType(accountType).
                    setBalanceAfter(account.getBalance()).
                    setTransactionAmount(paymentAmount).
                    setCardType(null).
                    setOverdraftAmount(account.getOverdraftAmount()).
                    setTransactionDate(LocalDateTime.now()).build();

            writeTransactionToFile(user, transactionRecord);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
            System.out.println("Overdraft resolved. Account unlocked. New balance: " + account.getBalance());

        } catch (
                Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void transfer(String userId, double amount, AccountType fromAccountType, CardType cardType, String toUserId, AccountType toAccountType, Boolean depositToAnotherAccount) {
        AccountService.VerifiedAccountResult verifiedAccountResult = getVerifiedAccount(userId, fromAccountType, cardType);
        if (verifiedAccountResult == null) {
            return;
        }
        Account fromAccount = verifiedAccountResult.account();
        User fromUser = verifiedAccountResult.user();

        boolean ownTransfer = userId.equals(toUserId);
        try {
            if (fromAccount.getBalance() < amount) {
                throw new InsufficientAmountException("You do not have enough balance for this transaction");
            }

            if (ownTransfer && toAccountType.equals(fromAccountType)) {
                throw new IllegalArgumentException("Transferring to same account type for same user not allowed");
            }

            User toUser = AuthService.getUserById(toUserId);
            Account toAccount = AccountType.CHECKING_ACCOUNT.equals(toAccountType) ? toUser.getCheckingAccount() : toUser.getSavingsAccount();

            if (toAccount == null) {
                String outputAccountType = toAccountType == AccountType.SAVINGS_ACCOUNT ? AccountType.SAVINGS_ACCOUNT.getDisplayName() : AccountType.CHECKING_ACCOUNT.getDisplayName();
                throw new RecordNotFoundException("No " + outputAccountType + " found for user: " + toUser.getName());
            }

            ICard card = getCardByTypeForAccount(fromAccount, cardType);

            double limit = 0;
            String transactionType = null;
            double dailyUsed = 0;

            TransactionRecord.Builder transactionBuilder = new TransactionRecord.Builder().
                    setBalanceBefore(fromAccount.getBalance());

            if (ownTransfer) {
                limit = card.getTransferLimitPerDayOwnAccount();
                transactionType = "Transfer to own account";
                dailyUsed = card.getDailyTransferredOwnAccount();
                transactionBuilder.setTransactionType(TransactionType.TRANSFER_TO_OWN_ACCOUNT);
            } else if (depositToAnotherAccount) {
                limit = card.getDepositLimitPerDay();
                transactionType = "Deposit to another account";
                dailyUsed = card.getDailyDeposited();
                transactionBuilder.setTransactionType(TransactionType.DEPOSIT).
                        setToUser(toUser.getId() + "-" + toUser.getName()).
                        setToAccountType(toAccountType);
            } else {
                limit = card.getTransferLimitPerDay();
                transactionType = "Transfer";
                dailyUsed = card.getDailyTransferred();
                transactionBuilder.setTransactionType(TransactionType.TRANSFER).
                        setToUser(toUser.getId() + "-" + toUser.getName()).
                        setToAccountType(toAccountType);
            }

            handleDailyLimits(card, amount, dailyUsed, limit, transactionType);

            fromAccount.transferFunds(amount, toAccount);

            if (ownTransfer) {
                card.setDailyTransferredOwnAccount(card.getDailyTransferredOwnAccount() + amount);
            } else if (depositToAnotherAccount) {
                card.setDailyDeposited(card.getDailyDeposited() + amount);
            } else {
                card.setDailyTransferred(card.getDailyTransferred() + amount);
            }

            TransactionRecord transactionRecord = transactionBuilder.
                    setAccountType(fromAccountType).
                    setBalanceAfter(fromAccount.getBalance()).
                    setTransactionAmount(amount).
                    setCardType(cardType).
                    setTransactionDate(LocalDateTime.now()).build();

            writeTransactionToFile(fromUser, transactionRecord);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), fromAccount.getId(), fromAccount.toString());
            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), toAccount.getId(), toAccount.toString());
            FileHandler.updateLineInFile(FilePath.CARDS.getPath(), card.getId(), card.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deposit(String userId, double amount, AccountType accountType, CardType cardType) {
        AccountService.VerifiedAccountResult verifiedAccount = getVerifiedAccount(userId, accountType, cardType);
        if (verifiedAccount == null) {
            return;
        }
        Account account = verifiedAccount.account();
        User user = verifiedAccount.user();

        ICard card = getCardByTypeForAccount(account, cardType);

        try {
            handleDailyLimits(card, amount, card.getDailyDepositedOwnAccount(),
                    card.getDepositLimitPerDayOwnAccount(), "Deposit");

            TransactionRecord.Builder transactionBuilder = new TransactionRecord.Builder().
                    setBalanceBefore(account.getBalance());

            account.deposit(amount);
            card.setDailyDeposited(card.getDailyDeposited() + amount);

            TransactionRecord transactionRecord = transactionBuilder.
                    setTransactionType(TransactionType.DEPOSIT_TO_OWN_ACCOUNT).
                    setAccountType(accountType).
                    setBalanceAfter(account.getBalance()).
                    setTransactionAmount(amount).
                    setCardType(cardType).
                    setTransactionDate(LocalDateTime.now()).build();

            writeTransactionToFile(user, transactionRecord);

            FileHandler.updateLineInFile(FilePath.ACCOUNTS.getPath(), account.getId(), account.toString());
            FileHandler.updateLineInFile(FilePath.CARDS.getPath(), card.getId(), card.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleDailyLimits(ICard card, double amount, double dailyAmount, double limit, String transactionType) throws DailyLimitExceededException {
        LocalDate today = LocalDate.now();
        if (card.getLastTransactionDate() == null || !today.equals(card.getLastTransactionDate())) {
            card.setDailyWithdrawn(0);
            card.setDailyDeposited(0);
            card.setDailyTransferred(0);
            card.setDailyDepositedOwnAccount(0);
            card.setDailyTransferredOwnAccount(0);
            card.setLastTransactionDate(today);
            dailyAmount = 0;
        }
        if (dailyAmount + amount > limit) {
            throw new DailyLimitExceededException(card.getClass().getSimpleName() + ": " + transactionType + " daily limit of $" + limit + " exceeded. Used: $" + dailyAmount + ", Requested: $" + amount + ", Remaining: $" + (limit - dailyAmount));
        }
    }

    private static void writeTransactionToFile(User user, TransactionRecord transactionRecord) {
        String transactionFileName = FilePath.CUSTOMER_TRANSACTIONS.getPath() + user.getId() + "-" + user.getName();
        FileHandler.writeToFile(transactionFileName, transactionRecord);
    }


}