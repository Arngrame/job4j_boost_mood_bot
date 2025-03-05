package ru.job4j.api.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.api.content.Content;
import ru.job4j.api.model.User;
import ru.job4j.api.services.MoodService;
import ru.job4j.api.storage.UserRepository;
import ru.job4j.api.telegram.command.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

@Service
public class BotCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotCommandHandler.class);

    private final UserRepository userRepository;
    private final MoodService moodService;
    private final StartCommandHandler startCommandHandler;
    private final WeekModLogCommandHandler weekModLogCommandHandler;
    private final MonthMoodLogCommandHandler monthMoodLogCommandHandler;
    private final AwardCommandHandler awardCommandHandler;
    private final DailyAdviceCommandHandler dailyAdviceCommandHandler;

    private Map<String, BiFunction<Long, Long, Optional<Content>>> commandDispatcher;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             StartCommandHandler startCommandHandler,
                             WeekModLogCommandHandler weekModLogCommandHandler,
                             MonthMoodLogCommandHandler monthMoodLogCommandHandler,
                             AwardCommandHandler awardCommandHandler,
                             DailyAdviceCommandHandler dailyAdviceCommandHandler) {
        this.userRepository = userRepository;
        this.moodService = moodService;

        this.startCommandHandler = startCommandHandler;
        this.weekModLogCommandHandler = weekModLogCommandHandler;
        this.monthMoodLogCommandHandler = monthMoodLogCommandHandler;
        this.awardCommandHandler = awardCommandHandler;
        this.dailyAdviceCommandHandler = dailyAdviceCommandHandler;

        fillDispatcher();
    }

    private void fillDispatcher() {
        commandDispatcher = Map.of(
                BotCommands.START.name, startCommandHandler::execute,
                BotCommands.WEEK_MOD_LOG.name, weekModLogCommandHandler::execute,
                BotCommands.MONTH_MOD_LOG.name, monthMoodLogCommandHandler::execute,
                BotCommands.AWARD.name, awardCommandHandler::execute,
                BotCommands.DAILY_ADVICE.name, dailyAdviceCommandHandler::execute
        );
    }

    Optional<Content> commands(Message message) {
        var chatId = message.getChatId();
        var clientId = message.getFrom().getId();
        var command = message.getText();

        BiFunction<Long, Long, Optional<Content>> func =
                this.commandDispatcher.getOrDefault(command, (aLong, aLong2) -> Optional.empty());
        return func.apply(chatId, clientId);
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

    private enum BotCommands {

        START("/start"),
        WEEK_MOD_LOG("/week_mood_log"),
        MONTH_MOD_LOG("/month_mood_log"),
        AWARD("/award"),
        DAILY_ADVICE("/daily_advice"),
        DAILY_ADVICE_ON("/daily_advice_on"),
        DAILY_ADVICE_OFF("/daily_advice_off"),
        DAILY_ADVICE_GET("/daily_advice_get");

        private final String name;

        BotCommands(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
