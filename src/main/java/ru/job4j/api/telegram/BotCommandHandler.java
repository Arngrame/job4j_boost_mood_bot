package ru.job4j.api.telegram;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(BotCommandHandler.class);

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
            case "/daily_advice" -> handleDailyAdviceCommand(chatId, clientId);
            default -> Optional.empty();
        };
    }

    Optional<Content> handleDailyAdviceCallback(CallbackQuery callbackQuery) {
        Long clientId = callbackQuery.getFrom().getId();
        User byClientId = userRepository.findByClientId(clientId);
        Long chatId = byClientId.getChatId();

        return switch (callbackQuery.getData()) {
            case "/daily_advice_on" -> moodService.setDailyAdvice(chatId, clientId, true);
            case "/daily_advice_off" -> moodService.setDailyAdvice(chatId, clientId, false);
            case "/daily_advice_get" -> moodService.getDailyAdvice(chatId, clientId);
            default -> Optional.empty();
        };
    }

    Optional<Content> handleCallback(CallbackQuery callback) {
        Long clientId = callback.getFrom().getId();
        if (callback.getData() != null && callback.getData().contains("/daily_advice_")) {
            return handleDailyAdviceCallback(callback);
        }

        Long moodId = Long.valueOf(callback.getData());
        User user = userRepository.findByClientIdAndChatId(clientId, callback.getMessage().getChatId());
        Content content = moodService.chooseMood(user, moodId);
        return Optional.of(content);
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        User user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);

        try {
            userRepository.save(user);
        } catch (ConstraintViolationException | DataIntegrityViolationException ex) {
            LOGGER.warn("User already exists", ex);
        }

        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(telegramUI.buildButtons());

        return Optional.of(content);
    }

    Optional<Content> handleDailyAdviceCommand(long chatId, Long clientId) {
        User user = new User();
        user.setClientId(clientId);
        user.setChatId(chatId);

        try {
            userRepository.save(user);
        } catch (ConstraintViolationException | DataIntegrityViolationException ex) {
            LOGGER.warn("User already exists", ex);
        }

        var content = new Content(user.getChatId());
        content.setText("Совет дня:");
        content.setMarkup(telegramUI.buildDailyAdviceButtons());

        return Optional.of(content);
    }
}
