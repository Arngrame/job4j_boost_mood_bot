package ru.job4j.api.content;

import ru.job4j.api.content.Content;

public interface ContentProvider {

    Content byMood(Long chatId, Long moodId);

}
