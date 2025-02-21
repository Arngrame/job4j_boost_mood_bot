package ru.job4j.api.telegram;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.condition.OnRealCondition;
import ru.job4j.api.content.Content;

@Conditional(OnRealCondition.class)
@Service
public class RealTelegramBotService extends TelegramLongPollingBot implements SendContent {

    private final String botName;
    private final String botToken;

    private BotCommandHandler commandHandler;

    public RealTelegramBotService(@Value("${telegram.bot.name}") String botName,
                                  @Value("${telegram.bot.token}") String botToken,
                                  BotCommandHandler commandHandler) {
        this.botName = botName;
        this.botToken = botToken;
        this.commandHandler = commandHandler;
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
        Long chatId = content.getChatId();
        String text = content.getText();

        try {
            if (content.getAudio() != null) {
                sendAudio(content, text, chatId);
            } else if (content.getPhoto() != null) {
                sendPhoto(content, text, chatId);
            } else if (content.getMarkup() != null) {
                sendMarkedUpText(content, text, chatId);
            } else if (content.getText() != null && StringUtils.isNotEmpty(content.getText())) {
                sendSimpleText(text, chatId);
            }
        } catch (TelegramApiException ex) {
            throw new SendContentException(ex);
        }
    }

    private void sendSimpleText(String text, Long chatId) throws TelegramApiException {
        SendMessage messageContent = new SendMessage();
        messageContent.setText(text);
        messageContent.setChatId(chatId);

        execute(messageContent);
    }

    private void sendMarkedUpText(Content content, String text, Long chatId) throws TelegramApiException {
        SendMessage markupContent = new SendMessage();
        markupContent.setReplyMarkup(content.getMarkup());
        markupContent.setText(text);
        markupContent.setChatId(chatId);

        execute(markupContent);
    }

    private void sendPhoto(Content content, String text, Long chatId) throws TelegramApiException {
        SendPhoto photoContent = new SendPhoto();
        photoContent.setPhoto(content.getPhoto());
        photoContent.setCaption(text);
        photoContent.setChatId(chatId);

        execute(photoContent);
    }

    private void sendAudio(Content content, String text, Long chatId) throws TelegramApiException {
        SendAudio audioContent = new SendAudio();
        audioContent.setAudio(content.getAudio());
        audioContent.setCaption(text);
        audioContent.setChatId(chatId);

        execute(audioContent);
    }
}
