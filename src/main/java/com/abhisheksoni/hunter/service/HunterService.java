package com.abhisheksoni.hunter.service;

import com.abhisheksoni.hunter.util.BotCommand;
import com.abhisheksoni.hunter.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class HunterService {
    @Autowired
    private DiscordService discordService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void startBot() {
        BotCommand start = BotCommand.START;
        discordService.sendServerCommand(start);
        logger.info(getBotMessage(start));
    }

    public void stopBot() {
        BotCommand stop = BotCommand.STOP;
        discordService.sendServerCommand(stop);
        logger.info(getBotMessage(stop));
    }

    public String getBotMessage(BotCommand botCommand) {
        String action = "?";
        if (botCommand == BotCommand.START) action = "started";
        if (botCommand == BotCommand.STOP) action = "stopped";
        return String.format("%s %s".toUpperCase(), Constants.BOT_NAME, action);
    }
}
