package ru.job4j.api.telegram;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.job4j.content.Content;

@Service
public class TelegramBotService {

    private final BotCommandHandler handler;

    public TelegramBotService(BotCommandHandler handler) {
        this.handler = handler;
    }

    public void receive(Content content) {
        handler.receive(content);
    }

    @PostConstruct
    public void init() {
        System.out.println("TelegramBotService bean is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("TelegramBotService bean will be destroyed now.");
    }
}
