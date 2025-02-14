package ru.job4j.api.telegram;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.job4j.api.condition.OnFakeCondition;
import ru.job4j.api.content.Content;
import ru.job4j.api.printer.ConsolePrinter;
import ru.job4j.api.printer.Printer;

@Conditional(OnFakeCondition.class)
@Service
public class FakeTelegramBotService extends TelegramLongPollingBot implements SendContent {

    private final String botName;
    private final String botToken;
    private Printer printer;

    public FakeTelegramBotService(@Value("${telegram.bot.name}") String botName,
                                  @Value("${telegram.bot.token}") String botToken) {
        this.botName = botName;
        this.botToken = botToken;
        this.printer = new ConsolePrinter();
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Content content = new Content(update.getMessage().getChatId());
            content.setText(update.getMessage().getText());

            send(content);
        }

        if (update.hasCallbackQuery()) {
            Content content = new Content(update.getCallbackQuery().getMessage().getChatId());
            content.setText(update.getCallbackQuery().getData());

            send(content);
        }
    }

    @Override
    public void send(Content content) {
        if (StringUtils.isNotEmpty(content.getText())) {
            printer.print(content.getText());
        }
    }
}
