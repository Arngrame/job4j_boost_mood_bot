package ru.job4j.api.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.api.content.Content;
import ru.job4j.api.model.Advice;
import ru.job4j.api.model.MoodLog;
import ru.job4j.api.model.User;
import ru.job4j.api.storage.AdviceRepository;
import ru.job4j.api.storage.MoodLogRepository;
import ru.job4j.api.telegram.SendContent;
import ru.job4j.api.telegram.TelegramUI;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReminderService {
    private final SendContent sendContent;
    private final MoodLogRepository moodLogRepository;
    private final TelegramUI telegramUI;
    private final AdviceRepository adviceRepository;

    public ReminderService(SendContent sendContent,
                           MoodLogRepository moodLogRepository,
                           TelegramUI telegramUI,
                           AdviceRepository adviceRepository) {
        this.sendContent = sendContent;
        this.moodLogRepository = moodLogRepository;
        this.telegramUI = telegramUI;
        this.adviceRepository = adviceRepository;
    }

    @Scheduled(initialDelay = 50000, fixedRateString = "${recommendation.alert.period}")
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

    @Scheduled(cron = "${cron.dailyAdvice}", zone = "GMT+4:00")
    public void remindDailyAdvice() {
        List<MoodLog> allMoodLogs = moodLogRepository.findAll();
        Set<User> userWithEnabledDailyAdvice = allMoodLogs.stream()
                .map(MoodLog::getUser).filter(User::isDailyAdviceOn)
                .collect(Collectors.toSet());

        for (User user : userWithEnabledDailyAdvice) {
            boolean moodType = allMoodLogs.stream()
                    .filter(value -> value.getUser().equals(user))
                    .max(Comparator.comparing(MoodLog::getCreatedAt))
                    .get().getMood().isGood();

            List<Advice> adviceList = adviceRepository.findAll()
                    .stream().filter(advice -> advice.isGood() == moodType).toList();

            Random rnd = new Random();
            Advice randomAdvice = adviceList.get(rnd.nextInt(adviceList.size()));

            Content content = new Content(user.getChatId());
            content.setText(randomAdvice.getText() + System.lineSeparator() + randomAdvice.getDescription());

            sendContent.send(content);
        }
    }

}
