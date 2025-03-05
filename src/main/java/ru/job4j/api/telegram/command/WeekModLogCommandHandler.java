package ru.job4j.api.telegram.command;

import org.springframework.stereotype.Component;
import ru.job4j.api.content.Content;
import ru.job4j.api.services.MoodService;

import java.util.Optional;

@Component
public class WeekModLogCommandHandler implements CommandHandler {

    private final MoodService moodService;

    public WeekModLogCommandHandler(MoodService moodService) {
        this.moodService = moodService;
    }

    @Override
    public Optional<Content> execute(long chatId, long clientId) {
        return moodService.weekMoodLogCommand(chatId, clientId);
    }
}
