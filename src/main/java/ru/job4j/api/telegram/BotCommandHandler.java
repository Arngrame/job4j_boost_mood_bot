package ru.job4j.api.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.api.content.Content;
import ru.job4j.api.model.User;
import ru.job4j.api.services.MoodService;
import ru.job4j.api.storage.UserRepository;

import java.util.Optional;

@Service
public class BotCommandHandler {

    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TelegramUI telegramUI;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             TelegramUI telegramUI) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.telegramUI = telegramUI;
    }

    Optional<Content> commands(Message message) {
        var chatId = message.getChatId();
        var clientId = message.getFrom().getId();
        var command = message.getText();

        return switch (command) {
            case "/start" -> handleStartCommand(chatId, clientId);
            case "/week_mood_log" -> moodService.weekMoodLogCommand(chatId, clientId);
            case "/month_mood_log" -> moodService.monthMoodLogCommand(chatId, clientId);
            case "/award" -> moodService.awards(chatId, clientId);
            default -> Optional.empty();
        };
    }

    Optional<Content> handleCallback(CallbackQuery callback) {
        Long moodId = Long.valueOf(callback.getData());
        User user = userRepository.findByClientId(callback.getFrom().getId());
        Content content = moodService.chooseMood(user, moodId);
        return Optional.of(content);
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        User user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);
        userRepository.save(user);

        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(telegramUI.buildButtons());

        return Optional.of(content);
    }
}
