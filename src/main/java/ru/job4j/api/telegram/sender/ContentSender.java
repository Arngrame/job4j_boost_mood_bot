package ru.job4j.api.telegram.sender;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.content.Content;

public interface ContentSender {

    void sendMessage(Content content, TelegramLongPollingBot bot) throws TelegramApiException;

}
