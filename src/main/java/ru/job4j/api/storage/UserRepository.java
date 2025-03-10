package ru.job4j.api.storage;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.api.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByClientId(Long clientId);

    User findByClientIdAndChatId(Long clientId, Long chatId);
}
