package com.ga.acme;

import com.ga.acme.enums.Roles;

public interface IUser {
    public String getId();
    public void setId(String id);

    public Roles getRole();

    public void setRole(Roles role);

    public String getHashedPassword();
    public void setHashedPassword(String hashedPassword) ;

    public String getName();

    public void setName(String name);

}
