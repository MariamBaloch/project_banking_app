package com.ga.acme.interfaces;

import com.ga.acme.enums.Roles;
import com.ga.acme.models.CheckingAccount;
import com.ga.acme.models.SavingsAccount;

import java.time.LocalTime;

public interface IUser {
    String getId();

    void setId(String id);

    Roles getRole();

    void setRole(Roles role);

    String getHashedPassword();

    void setHashedPassword(String hashedPassword);

    String getName();

    void setName(String name);

    int getLoginAttempts();

    void setLoginAttempts(int loginAttempts);

    LocalTime getLockedUntil();

    void setLockedUntil(LocalTime lockedUntil);

    boolean getIsLoggedIn();

    void setIsLoggedIn(boolean isLoggedIn);

    boolean isActive();

    void setActive(boolean active);

    SavingsAccount getSavingsAccount();

    void setSavingsAccount(SavingsAccount savingsAccount);

    CheckingAccount getCheckingAccount();

    void setCheckingAccount(CheckingAccount checkingAccount);
}
