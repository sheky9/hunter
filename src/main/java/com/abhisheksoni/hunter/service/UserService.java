package com.abhisheksoni.hunter.service;

import com.abhisheksoni.hunter.entity.Keyword;
import com.abhisheksoni.hunter.entity.Subscription;
import com.abhisheksoni.hunter.entity.User;
import com.abhisheksoni.hunter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private SubscriptionService subscriptionService;

    public void addUserKeywords(String discordUserId, List<String> keywordsToAdd, List<String> keywordsToRemove) {
        Optional<User> optionalUser = userRepository.findByDiscordUserId(discordUserId);
        if (optionalUser.isPresent()) {
            addExistingUserKeywords(optionalUser.get(), keywordsToAdd, keywordsToRemove);
        } else {
            createUser(discordUserId, keywordsToAdd);
        }
    }

    public void addUserSubscriptions(String discordUserId, List<String> subscriptionsToAdd, List<String> subscriptionsToRemove) {
        Optional<User> optionalUser = userRepository.findByDiscordUserId(discordUserId);
        if (optionalUser.isPresent()) {
            addExistingUserSubscriptions(optionalUser.get(), subscriptionsToAdd, subscriptionsToRemove);
        } else {
            createUser(discordUserId, subscriptionsToAdd);
        }
    }

    public void addExistingUserKeywords(User user, List<String> keywordNamesToAdd, List<String> keywordNamesToRemove) {
        Set<Keyword> keywords = user.getKeywords();
        for (String keywordName : keywordNamesToAdd) {
            keywords.add(keywordService.getOrCreateKeyword(keywordName));
        }
        for (String keywordName : keywordNamesToRemove) {
            keywords.remove(keywordService.getOrCreateKeyword(keywordName));
        }
        user.setKeywords(keywords);
        userRepository.save(user);
    }

    public void addExistingUserSubscriptions(User user, List<String> subscriptionNamesToAdd, List<String> subscriptionNamesToRemove) {
        Set<Subscription> subscriptions = user.getSubscriptions();
        for (String subscriptionName : subscriptionNamesToAdd) {
            subscriptions.add(subscriptionService.getOrCreateSubscription(subscriptionName));
        }
        for (String subscriptionName : subscriptionNamesToRemove) {
            subscriptions.remove(subscriptionService.getOrCreateSubscription(subscriptionName));
        }
        user.setSubscriptions(subscriptions);
        userRepository.save(user);
    }

    private void createUser(String discordUserId, List<String> keywords) {
        User user = new User();
        user.setDiscordUserId(discordUserId);
        user.setKeywords(keywordService.getKeywords(keywords));
        userRepository.save(user);
    }

    private User getOrCreateUserForDiscordUserId(String discordUserId) {
        Optional<User> optionalUser = userRepository.findByDiscordUserId(discordUserId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        return createUserForDiscordUserId(discordUserId);
    }

    private User createUserForDiscordUserId(String discordUserId) {
        User user = new User();
        user.setDiscordUserId(discordUserId);
        return userRepository.save(user);
    }

    public User getUserForDiscordUserId(String discordUserId) {
        Optional<User> optionalUser = userRepository.findByDiscordUserId(discordUserId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        return createUserForDiscordUserId(discordUserId);
    }

    public Set<User> getUsersForSubscriptionAndKeywords(String subscription, Set<Keyword> keywords) {
        return userRepository.findBySubscriptionsNameAndKeywordsIn(subscription, keywords);
    }
}
