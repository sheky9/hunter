package com.abhisheksoni.hunter.service;

import com.abhisheksoni.hunter.exception.HunterException;
import com.abhisheksoni.hunter.listner.HunterListener;
import com.abhisheksoni.hunter.util.BotCommand;
import com.abhisheksoni.hunter.util.Constants;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscordService {
    @Autowired
    private HunterListener hunterListener;

    private static JDA jda;

    public void sendServerCommand(BotCommand botCommand) {
        if (botCommand == BotCommand.START) startBot();
        if (botCommand == BotCommand.STOP) stopBot();
    }

    private void startBot() {
        initializeJda();
    }

    private void stopBot() {
        jda.shutdown();
    }

    private void initializeJda() {
        jda = getJdaInstance();
        jda.addEventListener(hunterListener);
    }

    private JDA getJdaInstance() {
        try {
            return JDABuilder.createDefault(Constants.DISCORD_BOT_TOKEN).build();
        } catch (Exception e) {
            throw new HunterException("Failed to create JDA instance");
        }
    }
}
