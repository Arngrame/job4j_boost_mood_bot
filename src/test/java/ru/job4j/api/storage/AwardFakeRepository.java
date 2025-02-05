package ru.job4j.api.storage;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.api.model.Award;

import java.util.ArrayList;
import java.util.List;

public class AwardFakeRepository extends CrudRepositoryFake<Award, Long> implements AwardRepository {

    @Override
    public List<Award> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Award save(Award award) {
        return memory.put(award.getId(), award);
    }
}
