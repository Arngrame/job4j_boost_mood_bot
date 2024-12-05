package ru.job4j.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.api.model.MoodContent;

import java.util.List;

@Repository
public interface MoodContentRepository extends CrudRepository<MoodContent, Long> {

    List<MoodContent> findAll();

    MoodContent save(MoodContent moodContent);
}
