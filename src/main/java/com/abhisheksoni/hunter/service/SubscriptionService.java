package com.abhisheksoni.hunter.service;

import com.abhisheksoni.hunter.entity.Subscription;
import com.abhisheksoni.hunter.entity.User;
import com.abhisheksoni.hunter.repository.SubscriptionRepository;
import com.abhisheksoni.hunter.util.SubscriptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserService userService;

    public Set<Subscription> getSubscriptions(List<String> names) {
        Set<Subscription> subscriptions = new HashSet<>();
        for (String name : names) {
            subscriptions.add(getOrCreateSubscription(name));
        }
        return subscriptions;
    }

    public Subscription getOrCreateSubscription(String name) {
        Optional<Subscription> optionalSubscription = subscriptionRepository.findByName(name);
        return optionalSubscription.orElseGet(() -> createSubscription(name));
    }

    private Subscription createSubscription(String name) {
        Subscription subscription = new Subscription();
        subscription.setName(name);
        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getUserSubscriptions(User user) {
        return subscriptionRepository.findByUser(user);
    }

    public String getSubscriptionsStringForDiscordUserId(String discordUserId) {
        User user = userService.getUserForDiscordUserId(discordUserId);
        StringBuilder subscriptionsStringBuilder = new StringBuilder();
        for (Subscription subscription : user.getSubscriptions()) {
            subscriptionsStringBuilder.append(subscription.getName());
            subscriptionsStringBuilder.append(" ");
        }
        return subscriptionsStringBuilder.toString();
    }
}
