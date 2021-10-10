package com.abhisheksoni.hunter.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue
    private long id;
    private String name;
    @ManyToOne
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription subscription = (Subscription) o;
        return name.equals(subscription.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
