package ru.job4j.api.services;

import org.springframework.stereotype.Service;
import ru.job4j.api.content.Content;
import ru.job4j.api.model.Achievement;
import ru.job4j.api.model.Mood;
import ru.job4j.api.model.MoodLog;
import ru.job4j.api.model.User;
import ru.job4j.api.recommendation.RecommendationEngine;
import ru.job4j.api.storage.AchievementRepository;
import ru.job4j.api.storage.MoodLogRepository;
import ru.job4j.api.storage.MoodRepository;
import ru.job4j.api.storage.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class MoodService {

    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final MoodRepository moodRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private static final int DAYS_MINUS = 7;
    private static final int MONTHS_MINUS = 1;

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       MoodRepository moodRepository) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.moodRepository = moodRepository;
    }

    public Content chooseMood(User user, Long moodId) {
        Mood foundMood = moodRepository.findById(moodId).get();

        long savingDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        MoodLog moodLog = new MoodLog();
        moodLog.setMood(foundMood);
        moodLog.setUser(user);
        moodLog.setCreatedAt(savingDate);

        moodLogRepository.save(moodLog);

        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        long resultedDate = LocalDateTime.now().minusDays(DAYS_MINUS).toEpochSecond(ZoneOffset.UTC);

        Predicate<MoodLog> byClientId = moodLog -> moodLog.getUser().getClientId() == clientId;
        Predicate<MoodLog> byCreatedDate = moodLog -> moodLog.getCreatedAt() == resultedDate;

        List<MoodLog> filteredLog = moodLogRepository.findAll().stream()
                .filter(byClientId)
                .filter(byCreatedDate)
                .toList();

        Content content = new Content(chatId);
        content.setText(formatMoodLogs(filteredLog, "7 days log"));

        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        long resultedDate = LocalDateTime.now().minusMonths(MONTHS_MINUS).toEpochSecond(ZoneOffset.UTC);

        Predicate<MoodLog> byClientId = moodLog -> moodLog.getUser().getClientId() == clientId;
        Predicate<MoodLog> byCreatedDate = moodLog -> moodLog.getCreatedAt() == resultedDate;

        List<MoodLog> filteredLog = moodLogRepository.findAll().stream()
                .filter(byClientId)
                .filter(byCreatedDate)
                .toList();

        Content content = new Content(chatId);
        content.setText(formatMoodLogs(filteredLog, "1 month log"));

        return Optional.of(content);
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        Predicate<Achievement> byClientId = achievement -> achievement.getUser().getClientId() == clientId;

        List<Achievement> achievements = achievementRepository.findAll().stream()
                .filter(byClientId)
                .toList();
        Content content = new Content(chatId);
        content.setText(formatAwardLogs(achievements, "Awards"));
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + " : there are no moods";
        }
        StringBuilder sb = new StringBuilder(title + " : " + System.lineSeparator());
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append(System.lineSeparator());
        });
        return sb.toString();
    }

    private String formatAwardLogs(List<Achievement> logs, String title) {
        if (logs.isEmpty()) {
            return title + " : there are no awards";
        }

        StringBuilder sb = new StringBuilder(title + " : " + System.lineSeparator());
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochSecond(log.getCreateAt()));
            sb.append(formattedDate).append(": ").append(log.getAward().getTitle()).append(System.lineSeparator());
        });
        return sb.toString();
    }
}
