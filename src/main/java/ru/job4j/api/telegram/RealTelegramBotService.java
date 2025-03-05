package ru.job4j.api.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.condition.OnRealCondition;
import ru.job4j.api.content.Content;
import ru.job4j.api.telegram.sender.ContentSender;

import java.util.List;

@Conditional(OnRealCondition.class)
@Service
public class RealTelegramBotService extends TelegramLongPollingBot implements SendContent {

    private final String botName;
    private final String botToken;

    private BotCommandHandler commandHandler;

    private List<ContentSender> senders;

    public RealTelegramBotService(@Value("${telegram.bot.name}") String botName,
                                  @Value("${telegram.bot.token}") String botToken,
                                  BotCommandHandler commandHandler,
                                  List<ContentSender> senders
    ) {
        this.botName = botName;
        this.botToken = botToken;
        this.commandHandler = commandHandler;
        this.senders = senders;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            commandHandler.commands(update.getMessage()).ifPresent(this::send);
        } else if (update.hasCallbackQuery()) {
            commandHandler.handleCallback(update.getCallbackQuery()).ifPresent(this::send);
        }
    }

    @Override
    public void send(Content content) {
        try {
            for (ContentSender sender : senders) {
                if (sender.isApplicableToSend(content)) {
                    sender.sendMessage(content, this);
                }
            }
        } catch (TelegramApiException ex) {
            throw new SendContentException(ex);
        }
    }
}
