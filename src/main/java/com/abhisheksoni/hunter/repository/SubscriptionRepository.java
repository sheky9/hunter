package com.abhisheksoni.hunter.repository;

import com.abhisheksoni.hunter.entity.Subscription;
import com.abhisheksoni.hunter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByName(String name);
    List<Subscription> findByUser(User user);
}
