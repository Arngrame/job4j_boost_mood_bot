package ru.job4j.api.storage;

import org.springframework.stereotype.Component;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.api.model.Advice;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdviceFakeRepository extends CrudRepositoryFake<Advice, Long> implements AdviceRepository {

    public List<Advice> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Advice save(Advice advice) {
        return memory.put(advice.getId(), advice);
    }
}