package ru.job4j.api.services;


import org.junit.jupiter.api.Test;
import ru.job4j.api.content.Content;
import ru.job4j.api.model.Mood;
import ru.job4j.api.model.MoodLog;
import ru.job4j.api.model.User;
import ru.job4j.api.storage.MoodFakeRepository;
import ru.job4j.api.storage.MoodLogFakeRepository;
import ru.job4j.api.telegram.SendContent;
import ru.job4j.api.telegram.TelegramUI;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderServiceTest {

    @Test
    public void whenMoodGood() {
        var result = new ArrayList<Content>();
        var sendContent = new SendContent() {
            @Override
            public void send(Content content) {
                result.add(content);
            }
        };

        var moodRepository = new MoodFakeRepository();
        Mood good = new Mood("Good", true);
        moodRepository.save(good);

        var user = new User();
        user.setChatId(100);

        var moodLog = new MoodLog();
        moodLog.setUser(user);
        moodLog.setMood(good);
        var yesterday = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLog.setCreatedAt(yesterday);

        var moodLogRepository = new MoodLogFakeRepository();
        moodLogRepository.save(moodLog);

        var telegramUI = new TelegramUI(moodRepository);
        ReminderService reminderService = new ReminderService(sendContent, moodLogRepository, telegramUI);
        reminderService.remindUsers();

        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }
}