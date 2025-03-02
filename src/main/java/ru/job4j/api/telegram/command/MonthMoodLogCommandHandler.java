package ru.job4j.api.telegram.command;

import org.springframework.stereotype.Component;
import ru.job4j.api.content.Content;
import ru.job4j.api.services.MoodService;

import java.util.Optional;

@Component
public class MonthMoodLogCommandHandler implements CommandHandler {

    private final MoodService moodService;

    public MonthMoodLogCommandHandler(MoodService moodService) {
        this.moodService = moodService;
    }

    @Override
    public Optional<Content> execute(long chatId, long clientId) {
        return moodService.monthMoodLogCommand(chatId, clientId);
    }
}
