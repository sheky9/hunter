package com.abhisheksoni.hunter.listner;

import com.abhisheksoni.hunter.service.EventService;
import com.abhisheksoni.hunter.util.HunterEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class HunterListener extends ListenerAdapter {
    @Autowired
    private EventService eventService;

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (eventService.isABotEvent(event)) {
            return;
        }
        if (!eventService.isAHunterEvent(event)) {
            eventService.sendMessagesForEvent(event);
            return;
        }
        HunterEvent hunterEvent = eventService.getHunterEvent(event);
        switch (hunterEvent) {
            case KEYS_ADD:
                eventService.addUserKeywords(event);
            case KEYS:
                eventService.sendMessage(eventService.buildInfoMessage(event), event);
                break;
            case SUBS_ADD:
                eventService.addUserSubscriptions(event);
            case SUBS:
                eventService.sendMessage(eventService.buildInfoMessage(event), event);
                break;
            case INFO:
                eventService.sendMessage(eventService.buildInfoMessage(event), event);
                break;
            case COLLAB:
                break;
            case HELP:
                break;
            case UNKNOWN:
                break;
        }
    }
}
