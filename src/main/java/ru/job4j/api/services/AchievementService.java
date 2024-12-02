package ru.job4j.api.services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Service;

@Service
public class AchievementService implements BeanNameAware {

    @PostConstruct
    public void init() {
        System.out.println("AchievementService bean is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("AchievementService bean will be destroyed now.");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("Bean name : " + name);
    }
}
