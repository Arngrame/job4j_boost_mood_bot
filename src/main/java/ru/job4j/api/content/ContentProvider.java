package ru.job4j.api.content;

public interface ContentProvider {

    Content byMood(Long chatId, Long moodId);

}
