package com.abhisheksoni.hunter.controller;

import com.abhisheksoni.hunter.service.HunterService;
import com.abhisheksoni.hunter.util.BotCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HunterController {
    @Autowired
    private HunterService hunterService;

    @GetMapping
    @RequestMapping("start")
    public String startServer() {
        hunterService.startBot();
        return hunterService.getBotMessage(BotCommand.START);
    }

    @GetMapping
    @RequestMapping("stop")
    public String stopServer() {
        hunterService.stopBot();
        return hunterService.getBotMessage(BotCommand.STOP);
    }
}
