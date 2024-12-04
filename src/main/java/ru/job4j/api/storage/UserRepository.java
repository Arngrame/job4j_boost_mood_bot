package ru.job4j.api.storage;

import ru.job4j.api.model.User;

import java.util.List;

public interface UserRepository {

    List<User> findAll();

    User findByClientId(Long clientId);

    void save(User user);
}
