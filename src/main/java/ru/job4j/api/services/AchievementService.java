package ru.job4j.api.services;

import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import ru.job4j.api.content.Content;
import ru.job4j.api.events.UserEvent;
import ru.job4j.api.model.MoodLog;
import ru.job4j.api.storage.AwardRepository;
import ru.job4j.api.storage.MoodLogRepository;
import ru.job4j.api.telegram.SendContent;

import java.util.List;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {

    private final SendContent sendContent;
    private final AwardRepository awardRepository;
    private final MoodLogRepository moodLogRepository;

    public AchievementService(SendContent sendContent,
                              AwardRepository awardRepository,
                              MoodLogRepository moodLogRepository) {
        this.sendContent = sendContent;
        this.awardRepository = awardRepository;
        this.moodLogRepository = moodLogRepository;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        long chatId = event.getUser().getChatId();

        int goodDaysCount = calculateDaysForAchievements(chatId);

        StringBuilder messageForUser = new StringBuilder();
        if (goodDaysCount <= 0) {
            messageForUser.append("You does not have achievements...");
        } else {
            messageForUser.append("Your achievements:").append(System.lineSeparator());

            awardRepository.findAll().stream()
                    .filter(award -> award.getDays() <= goodDaysCount)
                    .forEach(award -> messageForUser.append(award.getTitle())
                            .append(" : ")
                            .append(award.getDescription()));
        }

        Content content = new Content(chatId);
        content.setText(messageForUser.toString());

        sendContent.send(content);
    }

    private int calculateDaysForAchievements(long chatId) {
        List<MoodLog> userMoodLogs = moodLogRepository.findByUserChatId(chatId);

        int goodDaysCount = 0;
        for (MoodLog moodLog : userMoodLogs) {
            if (moodLog.getMood().isGood()) {
                goodDaysCount++;
            } else {
                goodDaysCount--;
            }
        }

        return goodDaysCount;
    }

}
