package com.ga.acme.models;

import com.ga.acme.enums.Roles;

public class Banker extends User {
    public Banker() {
    }

    public Banker(String id, String name, String password, Roles role) {
        super(id, name, password, Roles.BANKER);
    }
}
