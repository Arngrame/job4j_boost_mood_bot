package ru.job4j.api.telegram;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import ru.job4j.content.Content;

@Service
public class BotCommandHandler {

    void receive(Content content) {
        System.out.println(content);
    }

    @PostConstruct
    public void init() {
        System.out.println("BotCommandHandler bean is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("BotCommandHandler bean will be destroyed now.");
    }
}
