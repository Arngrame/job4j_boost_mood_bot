package ru.job4j.api.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.api.content.Content;
import ru.job4j.api.model.User;
import ru.job4j.api.storage.MoodLogRepository;
import ru.job4j.api.telegram.SendContent;
import ru.job4j.api.telegram.TelegramUI;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ReminderService {
    private final SendContent sendContent;
    private final MoodLogRepository moodLogRepository;
    private final TelegramUI telegramUI;

    public ReminderService(SendContent sendContent,
                           MoodLogRepository moodLogRepository, TelegramUI telegramUI) {
        this.sendContent = sendContent;
        this.moodLogRepository = moodLogRepository;
        this.telegramUI = telegramUI;
    }

    @Scheduled(fixedRateString = "${recommendation.alert.period}")
    public void remindUsers() {
        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var endOfDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;

        for (User user : moodLogRepository.findUsersWhoDidNotVoteToday(startOfDay, endOfDay)) {
            var content = new Content(user.getChatId());
            content.setText("Как настроение?");
            content.setMarkup(telegramUI.buildButtons());
            sendContent.send(content);
        }
    }

}
