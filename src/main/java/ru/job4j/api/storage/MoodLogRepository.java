package ru.job4j.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.api.model.MoodLog;
import ru.job4j.api.model.User;

import java.util.List;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {

    List<MoodLog> findAll();

    MoodLog save(MoodLog moodLog);

    default List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay) {
        List<User> votedUsers = findAll().stream()
                .filter(moodLog -> moodLog.getCreatedAt() >= startOfDay)
                .filter(moodLog -> moodLog.getCreatedAt() <= startOfDay)
                .map(MoodLog::getUser)
                .distinct()
                .toList();

        return findAll().stream()
                .map(MoodLog::getUser)
                .filter(user -> !votedUsers.contains(user))
                .distinct()
                .toList();
    }

    default List<MoodLog> findByUserChatId(long chatId) {
        return findAll().stream()
                .filter(moodLog -> moodLog.getUser().getChatId() == chatId)
                .toList();
    }
}
