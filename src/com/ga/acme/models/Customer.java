package com.ga.acme.models;

import com.ga.acme.enums.Roles;

public class Customer extends User {
    public Customer() {
    }

    public Customer(String id, String name, String password, Roles role) {
        super(id, name, password, Roles.CUSTOMER);
    }
}
