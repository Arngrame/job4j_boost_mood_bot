package ru.job4j.api.telegram.sender;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.api.content.Content;

@Component
public class AudioContentSender implements ContentSender {

    @Override
    public void sendMessage(Content content, TelegramLongPollingBot bot) throws TelegramApiException {
        Long chatId = content.getChatId();
        String text = content.getText();

        SendAudio audioContent = new SendAudio();
        audioContent.setAudio(content.getAudio());
        audioContent.setCaption(text);
        audioContent.setChatId(chatId);

        bot.execute(audioContent);
    }
}
