package ru.job4j.api.telegram.sender;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.content.Content;

@Component
public class PhotoContentSender implements ContentSender {

    @Override
    public boolean isApplicableToSend(Content content) {
        return content.getPhoto() != null;
    }

    @Override
    public void sendMessage(Content content, TelegramLongPollingBot bot) throws TelegramApiException {
        Long chatId = content.getChatId();
        String text = content.getText();

        SendPhoto photoContent = new SendPhoto();
        photoContent.setPhoto(content.getPhoto());
        photoContent.setCaption(text);
        photoContent.setChatId(chatId);

        bot.execute(photoContent);
    }
}
