package ru.job4j.api.telegram.sender;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.content.Content;

@Component
public class MarkedUpTextContentSender implements ContentSender {

    @Override
    public boolean isApplicableToSend(Content content) {
        return content.getMarkup() != null;
    }

    @Override
    public void sendMessage(Content content, TelegramLongPollingBot bot) throws TelegramApiException {
        Long chatId = content.getChatId();
        String text = content.getText();

        SendMessage markupContent = new SendMessage();
        markupContent.setReplyMarkup(content.getMarkup());
        markupContent.setText(text);
        markupContent.setChatId(chatId);

        bot.execute(markupContent);
    }
}
