package ru.job4j.api.telegram.command;

import org.springframework.stereotype.Component;
import ru.job4j.api.content.Content;
import ru.job4j.api.model.User;
import ru.job4j.api.storage.UserRepository;
import ru.job4j.api.telegram.TelegramUI;

import java.util.Optional;

@Component
public class StartCommandHandler implements CommandHandler {

    private final UserRepository userRepository;
    private final TelegramUI telegramUI;

    public StartCommandHandler(UserRepository userRepository,
                               TelegramUI telegramUI) {
        this.userRepository = userRepository;
        this.telegramUI = telegramUI;
    }

    @Override
    public Optional<Content> execute(long chatId, long clientId) {
        User user = userRepository.findByClientId(clientId);

        if (user == null) {
            user = new User();
            user.setClientId(clientId);
            user.setChatId(chatId);

            userRepository.save(user);
        }

        var content = new Content(user.getChatId());
        content.setText("Как настроение?");
        content.setMarkup(telegramUI.buildButtons());

        return Optional.of(content);
    }
}
