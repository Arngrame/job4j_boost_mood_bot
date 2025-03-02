package ru.job4j.api.telegram;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.condition.OnRealCondition;
import ru.job4j.api.content.Content;
import ru.job4j.api.telegram.sender.AudioContentSender;
import ru.job4j.api.telegram.sender.MarkedUpTextContentSender;
import ru.job4j.api.telegram.sender.PhotoContentSender;
import ru.job4j.api.telegram.sender.SimpleTextContentSender;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Conditional(OnRealCondition.class)
@Service
public class RealTelegramBotService extends TelegramLongPollingBot implements SendContent {

    private final String botName;
    private final String botToken;

    private BotCommandHandler commandHandler;
    private AudioContentSender audioContentSender;
    private PhotoContentSender photoContentSender;
    private SimpleTextContentSender simpleTextContentSender;
    private MarkedUpTextContentSender markedUpTextContentSender;

    public RealTelegramBotService(@Value("${telegram.bot.name}") String botName,
                                  @Value("${telegram.bot.token}") String botToken,
                                  BotCommandHandler commandHandler,
                                  AudioContentSender audioContentSender,
                                  PhotoContentSender photoContentSender,
                                  SimpleTextContentSender simpleTextContentSender,
                                  MarkedUpTextContentSender markedUpTextContentSender
    ) {
        this.botName = botName;
        this.botToken = botToken;
        this.commandHandler = commandHandler;

        this.audioContentSender = audioContentSender;
        this.photoContentSender = photoContentSender;
        this.simpleTextContentSender = simpleTextContentSender;
        this.markedUpTextContentSender = markedUpTextContentSender;
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
            if (content.getAudio() != null) {
                audioContentSender.sendMessage(content, this);
            } else if (content.getPhoto() != null) {
                photoContentSender.sendMessage(content, this);
            } else if (content.getMarkup() != null) {
                markedUpTextContentSender.sendMessage(content, this);
            } else if (content.getText() != null && StringUtils.isNotEmpty(content.getText())) {
                simpleTextContentSender.sendMessage(content, this);
            }
        } catch (TelegramApiException ex) {
            throw new SendContentException(ex);
        }
    }
}
