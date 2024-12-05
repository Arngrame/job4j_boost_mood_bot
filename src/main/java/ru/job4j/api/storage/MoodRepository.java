package ru.job4j.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.api.model.Mood;

import java.util.List;

@Repository
public interface MoodRepository extends CrudRepository<Mood, Long> {

    List<Mood> findAll();

    Mood save(Mood mood);
}
