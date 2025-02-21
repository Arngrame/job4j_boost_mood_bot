package ru.job4j.api.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.job4j.api.content.Content;
import ru.job4j.api.events.UserEvent;
import ru.job4j.api.model.*;
import ru.job4j.api.recommendation.RecommendationEngine;
import ru.job4j.api.storage.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

@Service
public class MoodService {

    private final ApplicationEventPublisher publisher;
    private final MoodLogRepository moodLogRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final MoodRepository moodRepository;
    private final AdviceRepository adviceRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private static final int DAYS_MINUS = 7;
    private static final int MONTHS_MINUS = 1;

    public MoodService(MoodLogRepository moodLogRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository,
                       MoodRepository moodRepository,
                       AdviceRepository adviceRepository,
                       ApplicationEventPublisher publisher) {
        this.moodLogRepository = moodLogRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.moodRepository = moodRepository;
        this.adviceRepository = adviceRepository;
        this.publisher = publisher;
    }

    public Content chooseMood(User user, Long moodId) {
        Mood foundMood = moodRepository.findById(moodId).get();

        long savingDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        MoodLog moodLog = new MoodLog();
        moodLog.setMood(foundMood);
        moodLog.setUser(user);
        moodLog.setCreatedAt(savingDate);

        moodLogRepository.save(moodLog);

        publisher.publishEvent(new UserEvent(this, user));

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

    public Optional<Content> setDailyAdvice(long chatId, long clientId, boolean isEnabled) {
        User byClientId = userRepository.findByClientId(clientId);
        byClientId.setDailyAdviceOn(isEnabled);
        userRepository.save(byClientId);

        Content content = new Content(chatId);
        content.setText("Опция \"Совет дня\" " + (isEnabled ? "включена" : "выключена"));
        return Optional.of(content);
    }

    public Optional<Content> getDailyAdvice(long chatId, long clientId) {
        boolean moodType = moodLogRepository.findAll().stream()
                .filter(value -> value.getUser().getClientId() == clientId)
                .max(Comparator.comparing(MoodLog::getCreatedAt))
                .get().getMood().isGood();

        List<Advice> adviceList = adviceRepository.findAll()
                .stream().filter(advice -> advice.isGood() == moodType).toList();

        Random rnd = new Random();
        Advice randomAdvice = adviceList.get(rnd.nextInt(adviceList.size()));

        Content content = new Content(chatId);
        content.setText(randomAdvice.getText() + System.lineSeparator() + randomAdvice.getDescription());

        return Optional.of(content);
    }
}
