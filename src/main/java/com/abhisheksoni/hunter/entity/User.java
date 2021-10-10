package com.abhisheksoni.hunter.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue
    private long id;
    private String discordUserId;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Keyword> keywords;
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Subscription> subscriptions;
}
