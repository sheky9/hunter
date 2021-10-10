package com.abhisheksoni.hunter.service;

import com.abhisheksoni.hunter.entity.Keyword;
import com.abhisheksoni.hunter.entity.Subscription;
import com.abhisheksoni.hunter.entity.User;
import com.abhisheksoni.hunter.exception.HunterException;
import com.abhisheksoni.hunter.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service
public class KeywordService {
    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserService userService;

    public static Set<Keyword> keywords;

    public Set<Keyword> getKeywords(List<String> names) {
        Set<Keyword> keywords = new HashSet<>();
        for (String name : names) {
            keywords.add(getOrCreateKeyword(name));
        }
        return keywords;
    }

    public Set<Keyword> getKeywords() {
        if (keywords == null) {
            List<Keyword> allKeywords = keywordRepository.findAll();
            for (Keyword keyword : allKeywords) {
                addKeywordToCache(keyword);
            }
        }
        return keywords;
    }

    public Keyword getOrCreateKeyword(String name) {
        Optional<Keyword> optionalKeyword = keywordRepository.findByName(name);
        return optionalKeyword.orElseGet(() -> createKeyword(name));
    }

    private Keyword createKeyword(String name) {
        Keyword keyword = new Keyword();
        keyword.setName(name);
        addKeywordToCache(keyword);
        return keywordRepository.save(keyword);
    }

    private void addKeywordToCache(Keyword keyword) {
        if (keywords == null) {
            keywords = new HashSet<>();
        }
        keywords.add(keyword);
    }

    public List<Keyword> getUserKeywords(User user) {
        return keywordRepository.findByUser(user);
    }

    public Optional<Keyword> getKeyword(String name) {
        for (Keyword keyword : keywords) {
            if (keyword.getName().equalsIgnoreCase(name)) {
                return Optional.of(keyword);
            }
        }
        return Optional.empty();
    }

    public String getKeywordsMessageForDiscordUserId(String discordUserId) {
        User user = userService.getUserForDiscordUserId(discordUserId);
        StringBuilder keywordsStringBuilder = new StringBuilder("Your Keywords: ");
        Set<Keyword> keywords = user.getKeywords();
        try {
            for (Keyword keyword : keywords) {
                keywordsStringBuilder.append(keyword.getName());
                keywordsStringBuilder.append(" ");
            }
        } catch (Exception e) {
        }
        return keywordsStringBuilder.toString();
    }

    public String getSubscriptionsMessageForDiscordUserId(String discordUserId) {
        User user = userService.getUserForDiscordUserId(discordUserId);
        StringBuilder subscriptionsStringBuilder = new StringBuilder("Your Subscriptions: ");
        Set<Subscription> subscriptions = user.getSubscriptions();
        if (subscriptions.size() == 0) {
            return subscriptionsStringBuilder.toString();
        }
        for (Subscription subscription : subscriptions) {
            subscriptionsStringBuilder.append(subscription.getName());
            subscriptionsStringBuilder.append(" ");
        }
        return subscriptionsStringBuilder.toString();
    }
}
