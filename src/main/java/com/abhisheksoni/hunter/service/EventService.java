package com.abhisheksoni.hunter.service;

import com.abhisheksoni.hunter.entity.Keyword;
import com.abhisheksoni.hunter.util.SubscriptionType;
import com.abhisheksoni.hunter.util.HunterEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.abhisheksoni.hunter.util.Constants.*;

@Service
public class EventService {
    @Autowired
    private UserService userService;

    @Autowired
    private KeywordService keywordService;

    private void sendPrivateMessage(String message, User user) {
        user.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(message).queue();
        });
    }

    private void sendPrivateMessage(String message, com.abhisheksoni.hunter.entity.User user, MessageReceivedEvent event) {
        sendPrivateMessage(message, getDiscordUser(user, event));
    }

    private User getDiscordUser(com.abhisheksoni.hunter.entity.User user, MessageReceivedEvent event) {
        return event.getJDA().getUserById(user.getDiscordUserId());
    }

    public void sendMessage(String message, MessageReceivedEvent event) {
        event.getChannel().sendMessage(message).queue();
    }

    public String getMessage(PrivateMessageReceivedEvent event) {
        return event.getMessage().getContentRaw();
    }

    public String getMessage(MessageReceivedEvent event) {
        return event.getMessage().getContentRaw();
    }

    public boolean isABotEvent(MessageReceivedEvent event) {
        return event.getAuthor().isBot();
    }

    public boolean isAHunterEvent(MessageReceivedEvent event) {
        String hunterEventIndicator = String.format("!%s", BOT_TRIGGER_NAME);
        String message = getMessage(event);
        return message.startsWith(hunterEventIndicator);
    }

    public HunterEvent getHunterEvent(MessageReceivedEvent event) {
        String[] tokens = getEventTokens(event);
        if (tokens.length == 1) {
            return get1TokenHunterEvent();
        }
        if (tokens.length == 2) {
            return get2TokensHunterEvent(tokens);
        }
        return get2PlusTokensHunterEvent(tokens);
    }

    public String[] getEventTokens(MessageReceivedEvent event) {
        return getMessage(event).split(" ");
    }

    private HunterEvent get2PlusTokensHunterEvent(String[] tokens) {
        if (HunterEvent.KEYS.toString().equalsIgnoreCase(tokens[1])) {
            return HunterEvent.KEYS_ADD;
        }
        if (HunterEvent.SUBS.toString().equalsIgnoreCase(tokens[1])) {
            return HunterEvent.SUBS_ADD;
        }
        return HunterEvent.UNKNOWN;
    }

    private HunterEvent get2TokensHunterEvent(String[] tokens) {
        if (HunterEvent.KEYS.toString().equalsIgnoreCase(tokens[1])) {
            return HunterEvent.KEYS;
        }
        if (HunterEvent.SUBS.toString().equalsIgnoreCase(tokens[1])) {
            return HunterEvent.SUBS;
        }
        if (HunterEvent.HELP.toString().equalsIgnoreCase(tokens[1])) {
            return HunterEvent.HELP;
        }
        if (HunterEvent.COLLAB.toString().equalsIgnoreCase(tokens[1])) {
            return HunterEvent.COLLAB;
        }
        if (HunterEvent.INFO.toString().equalsIgnoreCase(tokens[1])) {
            return HunterEvent.INFO;
        }
        return HunterEvent.UNKNOWN;
    }

    private HunterEvent get1TokenHunterEvent() {
        return HunterEvent.HELP;
    }

    private User getUser(MessageReceivedEvent event) {
        return event.getAuthor();
    }

    public void addUserKeywords(MessageReceivedEvent event) {
        User user = getUser(event);
        String[] tokens = getEventTokens(event);
        List<String> keywordsToAdd = new LinkedList<>();
        List<String> keywordsToRemove = new LinkedList<>();
        for (int index = 2; index < tokens.length; index++) {
            String currentToken = tokens[index];
            if (currentToken.length() == 1 && ("+".equals(currentToken) || "-".equals(currentToken))) {
                continue;
            }
            if (currentToken.startsWith("-")) {
                keywordsToRemove.add(currentToken.substring(1));
            } else if (currentToken.startsWith("+")) {
                keywordsToAdd.add(currentToken.substring(1));
            } else {
                keywordsToAdd.add(currentToken);
            }
        }
        userService.addUserKeywords(user.getId(), keywordsToAdd, keywordsToRemove);
    }

    public String getUserKeywordsMessage(MessageReceivedEvent event) {
        User user = getUser(event);
        return keywordService.getKeywordsMessageForDiscordUserId(user.getId());
    }

    public void addUserSubscriptions(MessageReceivedEvent event) {
        User user = getUser(event);
        String[] tokens = getEventTokens(event);
        List<String> subscriptionsToAdd = new LinkedList<>();
        List<String> subscriptionsToRemove = new LinkedList<>();
        for (int index = 2; index < tokens.length; index++) {
            String currentToken = tokens[index];
            if (currentToken.length() == 1 && ("+".equals(currentToken) || "-".equals(currentToken))) {
                continue;
            }
            if (currentToken.startsWith("-")) {
                validateAndAddSubscription(currentToken.substring(1), subscriptionsToRemove);
            } else if (currentToken.startsWith("+")) {
                validateAndAddSubscription(currentToken.substring(1), subscriptionsToAdd);
            } else {
                validateAndAddSubscription(currentToken, subscriptionsToAdd);
            }
        }
        userService.addUserSubscriptions(user.getId(), subscriptionsToAdd, subscriptionsToRemove);
    }

    void validateAndAddSubscription(String subscriptionName, List<String> subscriptions) {
        if ("all".equalsIgnoreCase(subscriptionName)) {
            subscriptions.addAll(SubscriptionType.getChannelMap());
        } else if (SubscriptionType.getChannelMap().contains(subscriptionName)) {
            subscriptions.add(subscriptionName);
        }
    }

    public String getUserSubscriptionsMessage(MessageReceivedEvent event) {
        User user = getUser(event);
        return keywordService.getSubscriptionsMessageForDiscordUserId(user.getId());
    }

    public String buildInfoMessage(MessageReceivedEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getUserKeywordsMessage(event));
        stringBuilder.append("\n");
        stringBuilder.append(getUserSubscriptionsMessage(event));
        return stringBuilder.toString();
    }

    public SubscriptionType getSubscriptionType(MessageReceivedEvent event) {
        String message = getMessage(event);
        if (message.contains("?")) {
            return SubscriptionType.QUESTIONS;
        }
        return SubscriptionType.MENTIONS;
    }

    public void sendMessagesForEvent(MessageReceivedEvent event) {
        String[] tokens = getEventTokens(event);
        Set<Keyword> keywords = keywordService.getKeywords();
        Set<Keyword> selectedKeywords = new HashSet<>();
        for (String token : tokens) {
            Optional<Keyword> optionalKeyword = keywordService.getKeyword(token.replace("?", ""));
            if (optionalKeyword.isPresent()) {
                Keyword keyword = optionalKeyword.get();
                if (keywords.contains(keyword)) {
                    selectedKeywords.add(keyword);
                }
            }
        }
        if (selectedKeywords.size() == 0) {
            return;
        }
        Set<com.abhisheksoni.hunter.entity.User> users;
        SubscriptionType subscriptionType = getSubscriptionType(event);
        users = userService.getUsersForSubscriptionAndKeywords(subscriptionType.toString(), selectedKeywords);
        for (com.abhisheksoni.hunter.entity.User user : users) {
            Set<Keyword> userRelevantKeywords = buildUserRelevantKeywords(user.getKeywords(), selectedKeywords);
            String action = (subscriptionType == SubscriptionType.MENTIONS ? "mentioned about" : "asked about");
            sendPrivateMessage(String.format("%s %s: %s", getUser(event).getName(), action, buildKeywordMessage(userRelevantKeywords)), user, event);
        }
    }

    private Set<Keyword> buildUserRelevantKeywords(Set<Keyword> userKeywords, Set<Keyword> selectedKeywords) {
        Set<Keyword> userRelevantKeywords = new HashSet<>();
        for (Keyword userKeyword : userKeywords) {
            if (selectedKeywords.contains(userKeyword)) {
                userRelevantKeywords.add(userKeyword);
            }
        }
        return userRelevantKeywords;
    }

    private String buildKeywordMessage(Set<Keyword> keywords) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Keyword keyword : keywords) {
            stringBuilder.append(keyword.getName());
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}
