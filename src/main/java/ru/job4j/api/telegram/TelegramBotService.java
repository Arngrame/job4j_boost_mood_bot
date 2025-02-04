package ru.job4j.api.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.content.Content;
import ru.job4j.api.model.User;
import ru.job4j.api.storage.UserRepository;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements SendContent {

    private final String botName;
    private final String botToken;

    private final UserRepository userRepository;
    private final TelegramUI telegramUI;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                                 @Value("${telegram.bot.token}") String botToken,
                                 UserRepository userRepository,
                                 TelegramUI telegramUI) {
        this.botName = botName;
        this.botToken = botToken;
        this.userRepository = userRepository;
        this.telegramUI = telegramUI;
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
            var message = update.getMessage();
            if ("/start".equals(message.getText())) {
                long chatId = message.getChatId();
                var user = new User();
                user.setClientId(message.getFrom().getId());
                user.setChatId(chatId);
                userRepository.save(user);
                send(sendButtons(chatId));
            }
        }

        if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            send(new SendMessage(String.valueOf(chatId), data));
        }
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage sendButtons(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Как настроение сегодня?");
        message.setReplyMarkup(telegramUI.buildButtons());

        return message;
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
            } else if (content.getText() != null || !content.getText().isEmpty()) {
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
