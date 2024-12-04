package ru.job4j.api.content;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;

@Component
public class ContentProviderVideo implements ContentProvider {
    @Override
    public Content byMood(Long chatId, Long moodId) {
        InputFile inputFile = new InputFile(new File("./video/news.avi"));

        Content content = new Content(chatId);
        content.setVideo(inputFile);

        return content;
    }
}
