package ru.job4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.job4j.api.model.Advice;
import ru.job4j.api.model.Award;
import ru.job4j.api.model.Mood;
import ru.job4j.api.model.MoodContent;
import ru.job4j.api.storage.AdviceRepository;
import ru.job4j.api.storage.AwardRepository;
import ru.job4j.api.storage.MoodContentRepository;
import ru.job4j.api.storage.MoodRepository;

import java.util.List;

@EnableScheduling
@SpringBootApplication
@EnableAspectJAutoProxy
public class MoodBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoodBotApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var bot = ctx.getBean(LongPollingBot.class);
            var botsApi = new TelegramBotsApi(DefaultBotSession.class);
            try {
                botsApi.registerBot(bot);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };
    }

    @Bean
    CommandLineRunner loadDatabase(MoodRepository moodRepository,
                                   MoodContentRepository moodContentRepository,
                                   AwardRepository awardRepository,
                                   AdviceRepository adviceRepository) {
        return args -> {
            List<Mood> moods = moodRepository.findAll();
            if (!moods.isEmpty()) {
                return;
            }

            List<MoodContent> data = listDefaultMoodContent();
            moodRepository.saveAll(data.stream().map(MoodContent::getMood).toList());
            moodContentRepository.saveAll(data);

            List<Award> awards = listDefaultAwards();
            awardRepository.saveAll(awards);

            List<Advice> defaultAdvice = listDefaultAdvice();
            adviceRepository.saveAll(defaultAdvice);
        };
    }

    private List<Award> listDefaultAwards() {
        return List.of(
                new Award("Смайлик дня", "За 1 день хорошего настроения.", 1),
                new Award("Настроение недели", "За 7 последовательных дней хорошего или отличного настроения.", 7),
                new Award("Бонусные очки", "За каждые 3 дня хорошего настроения.", 3),
                new Award("Персонализированные рекомендации", "После 5 дней хорошего настроения.", 5),
                new Award("Достижение 'Солнечный луч'", "За 10 дней непрерывного хорошего настроения.", 10),
                new Award("Виртуальный подарок", "После 15 дней хорошего настроения.", 15),
                new Award("Титул 'Лучезарный'", "За 20 дней хорошего или отличного настроения.", 20),
                new Award("Доступ к премиум-функциям", "После 30 дней хорошего настроения.", 30),
                new Award("Участие в розыгрыше призов", "За каждую неделю хорошего настроения.", 7),
                new Award("Эксклюзивный контент", "После 25 дней хорошего настроения.", 25),
                new Award("Награда 'Настроение месяца'", "За поддержание хорошего или отличного настроения в течение целого месяца.", 30),
                new Award("Физический подарок", "После 60 дней хорошего настроения.", 60),
                new Award("Коучинговая сессия", "После 45 дней хорошего настроения.", 45),
                new Award("Разблокировка мини-игр", "После 14 дней хорошего настроения.", 14),
                new Award("Персональное поздравление", "За значимые достижения (например, 50 дней хорошего настроения).", 50)
        );
    }

    private List<MoodContent> listDefaultMoodContent() {
        return List.of(
                new MoodContent(new Mood("Счастливейший на свете \uD83D\uDE0E", true), "Невероятно! Вы сияете от счастья, продолжайте радоваться жизни."),
                new MoodContent(new Mood("Воодушевленное настроение 🌟", true), "Великолепно! Вы чувствуете себя на высоте. Продолжайте в том же духе."),
                new MoodContent(new Mood("Успокоение и гармония 🧘‍♂️", true), "Потрясающе! Вы в состоянии внутреннего мира и гармонии."),
                new MoodContent(new Mood("В состоянии комфорта ☺️", true), "Отлично! Вы чувствуете себя уютно и спокойно."),
                new MoodContent(new Mood("Легкое волнение 🎈", false), "Замечательно! Немного волнения добавляет жизни краски."),
                new MoodContent(new Mood("Сосредоточенное настроение 🎯", false), "Хорошо! Ваш фокус на высоте, используйте это время эффективно."),
                new MoodContent(new Mood("Тревожное настроение 😟", false), "Не волнуйтесь, всё пройдет. Попробуйте расслабиться и найти источник вашего беспокойства."),
                new MoodContent(new Mood("Разочарованное настроение 😞", false), "Бывает. Не позволяйте разочарованию сбить вас с толку, всё наладится."),
                new MoodContent(new Mood("Усталое настроение 😴", false), "Похоже, вам нужен отдых. Позаботьтесь о себе и отдохните."),
                new MoodContent(new Mood("Вдохновенное настроение 💡", true), "Потрясающе! Вы полны идей и энергии для их реализации."),
                new MoodContent(new Mood("Раздраженное настроение 😠", true), "Попробуйте успокоиться и найти причину раздражения, чтобы исправить ситуацию.")
        );
    }

    private List<Advice> listDefaultAdvice() {
        return List.of(
                new Advice(true, "Практика благодарности", "Каждый день находите время для перечисления благ и радостей. Практика благодарности поможет видеть светлые стороны даже в трудных ситуациях"),
                new Advice(true, "Общение с позитивными людьми", " Позитивные люди поддерживают оптимистический настрой и уберегают от отрицательного влияния"),
                new Advice(true, "Умение прощать", "Негативные чувства и обиды могут отравлять душевную гармонию. Примите решение отпустить прошлое и сосредоточиться на настоящем"),
                new Advice(true, "Саморазвитие", "Чтение книг, учёба новым навыкам, путешествия и познание культур различных стран — всё это расширяет кругозор и помогает увидеть мир в более позитивном свете"),
                new Advice(false, "Не сравнивайте себя с другими", "У каждого есть свои достоинства и недостатки, слабые и сильные стороны. Вы - уникальны и ваша задача пройти свой собственный уникальный путь"),
                new Advice(false, "Живите в настоящем моменте", "Нет ни вчера, ни завтра. Всё что у вас есть - вы сегодняшний. И это всё, что нужно знать для победы над собой"),
                new Advice(false, "Отмечайте победы", "Признавайте свои вехи, какими бы маленькими они ни казались"),
                new Advice(false, "Ошибки", "Ошибки - не признак слабости или неудачи, это возможности научиться новым навыкам или разработать новые подходы"),
                new Advice(false, "Природа", "Прогулки на свежем воздухе, в лесу, около водоёмов значительно снижают уровень беспокойства и стресса"),
                new Advice(true, "Физическая активность", "Спорт, прогулки на свежем воздухе, йога или медитация помогут не только улучшить физическое здоровье, но и укрепить позитивное мышление"),
                new Advice(true, "Развитие навыка решения комплексных проблем", "Разбивайте сложные задачи на более маленькие шаги, учитесь искать позитивные решения, а не застревать в проблемах")
        );
    }

}