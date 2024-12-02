package ru.job4j.api.business;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class ReminderService {

    @PostConstruct
    public void init() {
        System.out.println("ReminderService bean is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("ReminderService bean will be destroyed now.");
    }

}
