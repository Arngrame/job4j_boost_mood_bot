package ru.job4j.api.telegram.command;

import ru.job4j.api.content.Content;

import java.util.Optional;

public interface CommandHandler {

    Optional<Content> execute(long chatId, long clientId);

}
