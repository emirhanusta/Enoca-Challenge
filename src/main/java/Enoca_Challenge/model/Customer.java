package Enoca_Challenge.model;

import jakarta.persistence.Entity;

@Entity
public class Customer extends BaseEntity {
    private String name;
    private String email;

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Customer() {

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
