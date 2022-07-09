package com.xaxoxuxu.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class ZoomService
{
    private final SeleniumService seleniumService;
    private final AtomicBoolean isRunning;

    public ZoomService(SeleniumService seleniumService)
    {
        this.seleniumService = seleniumService;
        this.isRunning = new AtomicBoolean(false);
    }

    public void StartBotRoutine(String meetingId, String meetingPassword)
    {
        if (isRunning.get())
        {
            log.error("Routine already running!");
            return;
        }

        log.info("StartBotRoutine meetingId:{} meetingPassword:{}", meetingId, meetingPassword);

        seleniumService.StartSeleniumRoutine(meetingId, meetingPassword);

        this.isRunning.set(true);
    }

    public void StopBotRoutine()
    {
        this.isRunning.set(false);
        seleniumService.StopSeleniumRoutine();
    }
}
