package com.abhisheksoni.hunter.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Keyword {
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
        Keyword keyword = (Keyword) o;
        return name.equals(keyword.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
