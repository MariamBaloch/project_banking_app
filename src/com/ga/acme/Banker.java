package com.ga.acme;

import com.ga.acme.enums.Roles;

import javax.management.relation.Role;

public class Banker extends User {
    public Banker() {}
    public Banker(String id, String name, String password, Roles role) {
        super(id, name, password, Roles.BANKER);
    }
}
