package ru.job4j.api.business;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class AchievementService {

    @PostConstruct
    public void init() {
        System.out.println("AchievementService bean is going through init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("AchievementService bean will be destroyed now.");
    }

}
