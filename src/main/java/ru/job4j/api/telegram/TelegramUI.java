package ru.job4j.api.telegram;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.job4j.api.model.Mood;
import ru.job4j.api.storage.MoodRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramUI {

    private final MoodRepository moodRepository;

    private Map<String, String> dailyAdviceMenu = new HashMap<>();

    public TelegramUI(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;

        dailyAdviceMenu.put("Подключить ежедневный совет", "/daily_advice_on");
        dailyAdviceMenu.put("Отключить ежедневный совет", "/daily_advice_off");
        dailyAdviceMenu.put("Получить совет", "/daily_advice_get");
    }

    public InlineKeyboardMarkup buildButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Mood mood : moodRepository.findAll()) {
            keyboard.add(List.of(createButton(mood.getText(), mood.getId())));
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup buildDailyAdviceButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Map.Entry<String, String> dailyAdviceContent : dailyAdviceMenu.entrySet()) {
            keyboard.add(List.of(createDailyAdviceMenuButton(dailyAdviceContent.getKey(), dailyAdviceContent.getValue())));
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton createButton(String name, Long moodId) {
        var inline = new InlineKeyboardButton();
        inline.setText(name);
        inline.setCallbackData(String.valueOf(moodId));
        return inline;
    }

    private InlineKeyboardButton createDailyAdviceMenuButton(String name, String command) {
        var inline = new InlineKeyboardButton();
        inline.setText(name);
        inline.setCallbackData(command);
        return inline;
    }
}
