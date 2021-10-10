package com.abhisheksoni.hunter.repository;

import com.abhisheksoni.hunter.entity.Keyword;
import com.abhisheksoni.hunter.entity.Subscription;
import com.abhisheksoni.hunter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByDiscordUserId(String discordUserId);
    Set<User> findBySubscriptionsNameAndKeywordsIn(String subscription, Set<Keyword> keywords);
}
