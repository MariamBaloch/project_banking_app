package com.ga.acme;


import com.ga.acme.controllers.Transaction;
import com.ga.acme.enums.AccountType;
import com.ga.acme.enums.CardType;

public class temp {

    public static void main(String[] args) {
//        Auth.signup("1", "mawwy", "potato", Roles.CUSTOMER);
//        Auth.signup("2", "wawwy", "potato", Roles.CUSTOMER);
//
//        Account.addAccount("1", AccountType.SAVINGSACCOUNT, true, false, false);
//        Account.addAccount("2", AccountType.CHECKINGACCOUNT, true, false, false);
//        Account.addAccount("2", AccountType.SAVINGSACCOUNT, true, false, false);

//        Auth.login("1", "potato");

//        Transaction.withdraw("1", 100, AccountType.SAVINGSACCOUNT, CardType.MASTERCARD);
//        Transaction.resolveOverdraft("1", 271, AccountType.SAVINGSACCOUNT);
        Transaction.deposit("1", 200, AccountType.SAVINGSACCOUNT, CardType.MASTERCARD);
//        Transaction.transfer("1", 10, AccountType.SAVINGSACCOUNT, CardType.MASTERCARD, "2", AccountType.CHECKINGACCOUNT, true);

//        Transaction.deposit("1", 100, AccountType.SAVINGSACCOUNT, CardType.MASTERCARD);

        Transaction.getUserTransactions("1");

    }
}
