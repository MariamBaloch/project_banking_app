package com.ga.acme.interfaces;

import com.ga.acme.models.Mastercard;
import com.ga.acme.models.MastercardPlatinum;
import com.ga.acme.models.MastercardTitanium;

public interface IAccount {

    String getId();

    void setId(String id);

    double getBalance();

    void setBalance(double balance);

    void withdraw(double amount);

    void deposit(double amount);

    void transferFunds(double amount, IAccount account);

    String getUserId();

    void setUserId(String userId);

    Mastercard getMastercard();

    void setMastercard(Mastercard mastercard);

    MastercardTitanium getMastercardTitanium();

    void setMastercardTitanium(MastercardTitanium mastercardTitanium);

    MastercardPlatinum getMastercardPlatinum();

    void setMastercardPlatinum(MastercardPlatinum mastercardPlatinum);

    int getOverdrafts();

    void setOverdrafts(int overdrafts);

    double getOverdraftAmount();

    void setOverdraftAmount(double overdraftAmount);

    boolean isLocked();

    void setLocked(boolean locked);
}