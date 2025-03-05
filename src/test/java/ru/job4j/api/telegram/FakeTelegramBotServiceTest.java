package ru.job4j.api.telegram;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import ru.job4j.api.printer.Printer;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.properties")
class FakeTelegramBotServiceTest {

    @Autowired
    private LongPollingBot fakeTelegramBotService;

    @MockBean
    private Printer consolePrinter;

    @Test
    void sendPrintsIntoConsole() {
        Message message = new Message();
        message.setText("Some text");
        message.setChat(new Chat());
        message.setFrom(new User());

        Update update = new Update();
        update.setMessage(message);

        fakeTelegramBotService.onUpdateReceived(update);

        verify(consolePrinter, times(1)).print(anyString());
    }

}
