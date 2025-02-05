package ru.job4j.api.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.api.content.Content;
import ru.job4j.api.events.UserEvent;
import ru.job4j.api.model.Award;
import ru.job4j.api.model.Mood;
import ru.job4j.api.model.MoodLog;
import ru.job4j.api.model.User;
import ru.job4j.api.storage.AwardFakeRepository;
import ru.job4j.api.storage.AwardRepository;
import ru.job4j.api.storage.MoodLogFakeRepository;
import ru.job4j.api.storage.MoodLogRepository;
import ru.job4j.api.telegram.SendContent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AchievementServiceTest {

    private static AwardRepository awardRepository;
    private static MoodLogRepository moodLogRepository;

    @BeforeAll
    public static void init() {
        awardRepository = new AwardFakeRepository();
        Award award1 = new Award();
        award1.setId(1L);
        award1.setDays(1);
        award1.setTitle("1 day title");
        award1.setDescription("1 day description");

        Award award2 = new Award();
        award2.setId(2L);
        award2.setDays(3);
        award2.setTitle("3 days title");
        award2.setDescription("3 days description");

        awardRepository.save(award1);
        awardRepository.save(award2);

        User user1 = new User(1L, 100, 10);
        User user2 = new User(1L, 200, 20);

        MoodLog ml1 = createFakeMoodLog(1L, "bad", false, user1);
        MoodLog ml20 = createFakeMoodLog(20L, "nice", true, user2);
        MoodLog ml21 = createFakeMoodLog(21L, "excellent", true, user2);
        MoodLog ml22 = createFakeMoodLog(22L, "good", true, user2);
        MoodLog ml23 = createFakeMoodLog(23L, "bad", false, user2);

        moodLogRepository = new MoodLogFakeRepository();
        moodLogRepository.saveAll(Arrays.asList(ml1, ml20, ml21, ml22, ml23));
    }

    private static MoodLog createFakeMoodLog(long id, String moodText, boolean isGoodMood, User user) {
        MoodLog moodLog = new MoodLog();
        moodLog.setId(id);
        moodLog.setMood(new Mood(moodText, isGoodMood));
        moodLog.setUser(user);
        moodLog.setCreatedAt(OffsetDateTime.now().toEpochSecond());

        return moodLog;
    }

    @Test
    public void whenUserGetsAchievement() {
        List<Content> result = new ArrayList<>();
        SendContent sendContent = new SendContent() {
            @Override
            public void send(Content content) {
                result.add(content);
            }
        };

        AchievementService achievementService = new AchievementService(sendContent,
                awardRepository, moodLogRepository);

        User user2 = new User();
        user2.setChatId(20);
        user2.setClientId(200);

        UserEvent userEvent = new UserEvent(this, user2);
        achievementService.onApplicationEvent(userEvent);

        String expectedResult = "Your achievements:"
                + System.lineSeparator()
                + "1 day title : 1 day description";
        String actualResult = result.iterator().next().getText();
        assertEquals(expectedResult, actualResult);
    }

}