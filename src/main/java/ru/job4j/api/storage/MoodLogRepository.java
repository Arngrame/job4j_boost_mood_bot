package ru.job4j.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.api.model.MoodLog;

import java.util.List;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {

    List<MoodLog> findAll();

    MoodLog save(MoodLog moodLog);
}
