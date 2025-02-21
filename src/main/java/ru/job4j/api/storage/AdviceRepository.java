package ru.job4j.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.api.model.Advice;

import java.util.List;

@Repository
public interface AdviceRepository extends CrudRepository<Advice, Long> {

    List<Advice> findAll();

    Advice save(Advice advice);
}