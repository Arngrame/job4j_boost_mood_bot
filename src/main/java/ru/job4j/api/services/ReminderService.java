package ru.job4j.api.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Service;

@Service
public class ReminderService implements BeanNameAware {

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
