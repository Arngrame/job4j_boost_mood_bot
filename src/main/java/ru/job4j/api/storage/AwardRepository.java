package ru.job4j.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.api.model.Award;

import java.util.List;

@Repository
public interface AwardRepository extends CrudRepository<Award, Long> {

    List<Award> findAll();

    Award save(Award moodLog);
}
