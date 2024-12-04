package ru.job4j.api.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.job4j.api.storage.UserRepository;
import ru.job4j.api.telegram.TelegramRemoteService;

@Service
public class ReminderService implements BeanNameAware {

    private final TelegramRemoteService telegramRemoteService;
    private final UserRepository userRepository;

    public ReminderService(TelegramRemoteService tgRemoteService, UserRepository userRepository) {
        this.telegramRemoteService = tgRemoteService;
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRateString = "${remind.period}")
    public void ping() {
        for (var user : userRepository.findAll()) {
            var message = new SendMessage();
            message.setChatId(user.getChatId());
            message.setText("Ping");
            telegramRemoteService.send(message);
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("ReminderService bean is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("ReminderService bean will be destroyed now.");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("Bean name : " + name);
    }
}
