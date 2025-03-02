package ru.job4j.api.telegram.sender;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.content.Content;

@Component
public class SimpleTextContentSender implements ContentSender {

    @Override
    public void sendMessage(Content content, TelegramLongPollingBot bot) throws TelegramApiException {
        Long chatId = content.getChatId();
        String text = content.getText();

        SendMessage messageContent = new SendMessage();
        messageContent.setText(text);
        messageContent.setChatId(chatId);

        bot.execute(messageContent);
    }
}
