package ru.job4j.api.storage;

import org.springframework.stereotype.Component;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.api.model.Mood;

import java.util.ArrayList;
import java.util.List;

@Component
public class MoodFakeRepository extends CrudRepositoryFake<Mood, Long> implements MoodRepository {

    public List<Mood> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Mood save(Mood mood) {
        return memory.put(mood.getId(), mood);
    }
}

