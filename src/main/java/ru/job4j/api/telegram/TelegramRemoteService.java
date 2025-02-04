package ru.job4j.api.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.model.User;
import ru.job4j.api.storage.UserRepository;

@Service
public class TelegramRemoteService extends TelegramLongPollingBot {

    private final String botName;
    private final String botToken;

    private final UserRepository userRepository;
    private final TelegramUI telegramUI;

    public TelegramRemoteService(@Value("${telegram.bot.name}") String botName,
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
}
