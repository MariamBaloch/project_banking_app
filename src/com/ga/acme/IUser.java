package com.ga.acme;

import com.ga.acme.enums.Roles;

import java.time.LocalTime;

public interface IUser {
     String getId();

     void setId(String id);

     Roles getRole();

     void setRole(Roles role);

     String getHashedPassword();

     void setHashedPassword(String hashedPassword) ;

     String getName();

     void setName(String name);

     int getLoginAttempts();

     void setLoginAttempts(int loginAttempts);

     LocalTime getLockedTime();

     void setLockedTime(LocalTime lockedUntil);
}
