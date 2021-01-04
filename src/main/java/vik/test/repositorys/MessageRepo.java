package vik.test.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import vik.test.models.Messages;
import vik.test.models.User;

@EnableJpaRepositories
public interface MessageRepo extends JpaRepository<Messages, Long> {
    Iterable<Messages> findByUserId(User user);
}
