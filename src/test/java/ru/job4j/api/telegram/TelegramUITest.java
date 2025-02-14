package ru.job4j.api.telegram;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.job4j.api.storage.MoodFakeRepository;
import ru.job4j.api.storage.MoodRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ContextConfiguration(classes = {TelegramUI.class, MoodFakeRepository.class})
class TelegramUITest {

    @Autowired
    private TelegramUI telegramUI;

    @MockBean
    private MoodRepository moodRepository;

    @Test
    public void whenBtnGood() {
        assertThat(moodRepository).isNotNull();
    }

    @Test
    public void whenButtonBuildSuccessfully() {
        telegramUI.buildButtons();
        verify(moodRepository, times(1)).findAll();
    }

}